package com.sky.properties;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 阿里云 OSS 配置属性类
 * 用于绑定配置文件中 {@code sky.aliyun.oss} 前缀下的属性，
 * 包括 OSS 访问地址、存储空间名称、地域等信息。
 */

@Data
@ConfigurationProperties(prefix = "sky.aliyun.oss")
public class AliOSSProperties {

    private String endpoint;

    private String bucketName;

    private String region;

}
