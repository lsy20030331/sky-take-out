package com.sky.config;

import com.sky.properties.AliOssProperties;
import com.sky.utils.AliOssUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 创建Oss的配置类对象
 */
@Configuration
@Slf4j
public class OssConfiguration {


    @Bean  // 因为此方法没有成为容器的Bean所以不能被调用到所以加上Bean的注解
    @ConditionalOnMissingBean  // 保证容器内只有它一个对象,对于工具类对象不需要那么多,如果在其他地方有它加了注解后就不会创建了
    public AliOssUtil aliOssUtil(AliOssProperties aliOssProperties){
        log.info("开始创建阿里云文件上传对象: {}", aliOssProperties);
        return new AliOssUtil(aliOssProperties.getEndpoint(),
                aliOssProperties.getAccessKeyId(),
                aliOssProperties.getAccessKeySecret(),
                aliOssProperties.getBucketName());
    }
}
