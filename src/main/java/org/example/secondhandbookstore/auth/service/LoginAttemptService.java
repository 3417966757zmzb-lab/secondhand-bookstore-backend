package org.example.secondhandbookstore.auth.service;


import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;


// 登录失败计数与退避：按"账号 + IP"记录，使用 Redis 滑动窗口计数


@Service
@RequiredArgsConstructor
public class LoginAttemptService {

    //记录登录失败次数
    private static final String FAILURE_KEY_PREFIX = "login:fail:";
    //还需要等待的时间key
    private static final String RETRY_KEY_PREFIX = "login:retry:";
    // 计数窗口：每次失败都刷新；必须大于最大退避 15 分钟，取两倍
    private static final Duration FAILURE_WINDOW = Duration.ofMinutes(30);
    // 这个次数以内不等待
    private static final int NO_DELAY_MAX_FAILURES = 3;
    // 下标即失败次数；10 次及以后取 MAX_RETRY_SECONDS
    private static final int[] RETRY_SECONDS = {0, 0, 0, 0, 10, 30, 60, 120, 240, 480};
    private static final int MAX_RETRY_SECONDS = 900;
    // 抖动幅度百分比
    private static final int JITTER_PERCENT = 20;

    //// 原子自增失败次数并刷新窗口，避免"自增成功却没设上过期时间"
    private static final DefaultRedisScript<Long> SAVE_FAILURE_SCRIPT = new DefaultRedisScript<>(
            "local count = redis.call('INCR',KEYS[1])"
                    + "redis.call('EXPIRE',KEYS[1],ARGV[1])"
                    + "return count",
            Long.class);

    private final StringRedisTemplate stringRedisTemplate;

    //返回重试的剩余时间
    public long getRetryAfterSeconds(String account , String ip){
        String retryValue = stringRedisTemplate.opsForValue().get(retryKey(account,ip));
        if(retryValue == null){
            return 0;
        }
        //允许重试的截止时间(转化成了时间戳)
        long retryUntilMillis = Long.parseLong(retryValue);
        //还需要等待的时间
        long remainingMillis = retryUntilMillis - System.currentTimeMillis();
        //向上取整,剩 1 毫秒也要返回 1 秒，否则客户端立刻重试还是被拒
        return remainingMillis > 0 ? (remainingMillis+999) / 1000 : 0;

    }

    //生成失败次数和失败后重试剩余等待时间的 redis 的 key
    public void saveLoginFailure(String account , String ip){
        //execute执行脚本，生成失败次数的key
        Long failureCount = stringRedisTemplate.execute(
                SAVE_FAILURE_SCRIPT,
                List.of(failureKey(account,ip)),
                String.valueOf(FAILURE_WINDOW.toSeconds()));
        if (failureCount == null) {
            return;
        }
        //先根据失败次数查表得到基础退避秒数，然后将该秒数作为参数进行抖动;
        int retrySeconds = applyJitter(resolveRetrySeconds(failureCount));
        if(retrySeconds <= 0){
            return;
        }
        //设置重试剩余时间的key
        stringRedisTemplate.opsForValue().set(
                retryKey(account, ip),
                String.valueOf(System.currentTimeMillis()+retrySeconds * 1000L),
                Duration.ofSeconds(retrySeconds)
        );


    }


    // 查表得到基础退避秒数
    private int resolveRetrySeconds(long failureCount){
        if(failureCount <= NO_DELAY_MAX_FAILURES){
            return 0;
        }
        if(failureCount >= RETRY_SECONDS.length){
            return MAX_RETRY_SECONDS;
        }
        return RETRY_SECONDS[(int)failureCount];
    }

    private int applyJitter(int retrySeconds){
        //如果时间过小，抖动无意义，直接返回
        if(retrySeconds <= 1){
            return retrySeconds;
        }
        //保证抖动范围至少是 1 秒，即使计算出 0，也能有 ±1 秒的随机抖动。
        int jitterRange = Math.max(1,retrySeconds * JITTER_PERCENT/100);
        int jittered = retrySeconds + ThreadLocalRandom.current().nextInt(-jitterRange, jitterRange+1);
        return Math.max(1,jittered);
    }

    // 登录成功后清除计数和退避记录
    public void removeLoginFailures(String account, String ip) {
        stringRedisTemplate.delete(List.of(failureKey(account, ip), retryKey(account, ip)));
    }

    //拼接成失败次数的key
    private String failureKey(String account,String ip){
        return FAILURE_KEY_PREFIX + account+":"+ip;
    }

    //拼接成重试剩余时间的key
    private String retryKey(String account,String ip){
        return RETRY_KEY_PREFIX + account+":"+ip;
    }

}
