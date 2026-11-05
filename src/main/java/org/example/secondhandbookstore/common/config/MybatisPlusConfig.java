package org.example.secondhandbookstore.common.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MybatisPlusConfig {
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(this.createPaginationInnerInterceptor());
        return interceptor;
    }

    private PaginationInnerInterceptor createPaginationInnerInterceptor() {
        PaginationInnerInterceptor paginationInterceptor =
                new PaginationInnerInterceptor(DbType.MYSQL);
        // 单页最多查询100条，防止一次加载过多数据
        paginationInterceptor.setMaxLimit(100L);
        // 页码超出总页数时返回空页，不自动回到第一页
        paginationInterceptor.setOverflow(false);
        return paginationInterceptor;
    }
}
