package org.example.secondhandbookstore.common.config;

import org.example.secondhandbookstore.catalog.dto.CategoryResponse;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.JacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import tools.jackson.databind.jsontype.PolymorphicTypeValidator;
import tools.jackson.databind.type.TypeFactory;

import java.time.Duration;
import java.util.List;

@Configuration(proxyBeanMethods = false)
@EnableCaching
public class CacheConfig {

    @Bean
    public RedisCacheConfiguration redisCacheConfiguration() {
        PolymorphicTypeValidator typeValidator = BasicPolymorphicTypeValidator.builder()
                .allowIfSubType("java.util.")
                .allowIfSubType("org.example.secondhandbookstore.")
                .allowIfSubTypeIsArray()
                .build();

        GenericJacksonJsonRedisSerializer valueSerializer =
                GenericJacksonJsonRedisSerializer.builder()
                        .typePropertyName("@class")
                        .enableDefaultTyping(typeValidator)
                        .build();

        return RedisCacheConfiguration.defaultCacheConfig()
                // 默认缓存30分钟
                .entryTtl(Duration.ofMinutes(30))
                // 不缓存null值，避免无意义缓存
                .disableCachingNullValues()
                // Redis key使用普通字符串
                .serializeKeysWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(
                                new StringRedisSerializer()))
                // Redis value使用带类型信息的JSON
                .serializeValuesWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(
                                valueSerializer))
                // 最终key示例：cache:catalog:categories::all
                .prefixCacheNameWith("cache:");
    }

    @Bean
    public RedisCacheManager redisCacheManager(
            RedisConnectionFactory connectionFactory,
            RedisCacheConfiguration redisCacheConfiguration) {
        JavaType categoryListType = TypeFactory.createDefaultInstance()
                .constructCollectionType(List.class, CategoryResponse.class);
        JacksonJsonRedisSerializer<List<CategoryResponse>> categoryValueSerializer =
                new JacksonJsonRedisSerializer<>(categoryListType);
        RedisCacheConfiguration categoryCacheConfiguration = redisCacheConfiguration
                .serializeValuesWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(
                                categoryValueSerializer));

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(redisCacheConfiguration)
                .withCacheConfiguration("catalog:categories", categoryCacheConfiguration)
                .build();
    }
}
