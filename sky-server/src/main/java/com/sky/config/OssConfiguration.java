package com.sky.config;

import com.sky.properties.AliOssProperties;
import com.sky.utils.AliOssUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * 配置类，创建AliOssUtil对象
 *
 * @author yao
 * @version 1.0
 * @date 2024/7/24 - 1:11
 * @className OssConfiguration
 * @since 1.0
 */

@Component
@Slf4j
public class OssConfiguration {

    @Bean
    @ConditionalOnMissingBean // 当没有bean时创建
    public AliOssUtil aliOssUtil(AliOssProperties properties) {
        log.info("开始创建阿里云文件上传工具对象，{}", properties);
        return new AliOssUtil(properties.getEndpoint(),
                properties.getAccessKeyId(),
                properties.getAccessKeySecret(),
                properties.getBucketName());
    }
}
