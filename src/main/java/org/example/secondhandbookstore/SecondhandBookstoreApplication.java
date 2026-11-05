package org.example.secondhandbookstore;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.time.Duration;

@SpringBootApplication

public class SecondhandBookstoreApplication {
	// 临时冒烟：验证 Redis 读写与 TTL，由退避服务的真实验证替代后删除
//	@Bean
//	public CommandLineRunner redisSmokeTest(StringRedisTemplate stringRedisTemplate) {
//		return args -> {
//			stringRedisTemplate.opsForValue().set("smoke:redis", "ok", Duration.ofMinutes(1));
//			System.out.println("redis smoke -> value=" + stringRedisTemplate.opsForValue().get("smoke:redis")
//					+ ", ttlSeconds=" + stringRedisTemplate.getExpire("smoke:redis"));
//		};
//	}

	public static void main(String[] args) {
			System.out.println("DB_USERNAME = " + System.getenv("DB_USERNAME"));
			System.out.println("DB_PASSWORD = " + System.getenv("DB_PASSWORD"));
			System.out.println("JWT_SECRET = " + System.getenv("JWT_SECRET"));
		SpringApplication.run(SecondhandBookstoreApplication.class, args);
	}

}
