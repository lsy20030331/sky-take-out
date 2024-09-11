package com.sky.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "sky.jwt")  // 在Springboot中此注解表明的是一个配置类 并在配置文件中寻找skt下的jwt(sky-server.dev.yml)
@Data
public class JwtProperties {

    /**
     * 管理端员工生成jwt令牌相关配置
     */
    private String adminSecretKey;  // 签名加密时使用的秘钥
    private long adminTtl;  // 令牌有效时间
    private String adminTokenName;  // 令牌名称

    /**
     * 用户端微信用户生成jwt令牌相关配置
     */
    private String userSecretKey;
    private long userTtl;
    private String userTokenName;

}
