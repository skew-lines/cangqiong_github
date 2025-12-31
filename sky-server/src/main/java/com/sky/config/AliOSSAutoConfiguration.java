package com.sky.config;

import com.sky.properties.AliOSSProperties;
import com.sky.utils.AliOSSUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 阿里云 OSS 自动配置类
 * 根据配置文件中 {@code sky.aliyun.oss} 相关属性，
 * 自动装配 {@link AliOSSUtils} Bean，
 * 用于实现文件上传等 OSS 相关功能。
 * 使用 {@link EnableConfigurationProperties}
 * 启用 {@link AliOSSProperties} 的配置绑定。
 */

@Configuration
@EnableConfigurationProperties(AliOSSProperties.class)
@Slf4j
public class AliOSSAutoConfiguration {
    @Bean
    public AliOSSUtils aliOSSUtils(AliOSSProperties aliOSSProperties) {
        log.info("自动配置阿里云文件上传工具类对象：{}",aliOSSProperties);
        AliOSSUtils aliOSSUtils = new AliOSSUtils();
        aliOSSUtils.setAliOSSProperties(aliOSSProperties);
        return aliOSSUtils;
    }
}