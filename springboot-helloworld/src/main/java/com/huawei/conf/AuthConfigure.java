// Copyright (C) 2019 Baidu Inc. All rights reserved.
package com.huawei.conf;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * AuthConfigure
 *
 * @author Cai Zhensheng(caizhensheng@baidu.com)
 * @since 2019-08-08
 */
@Configuration
public class AuthConfigure {

    @Bean
    @ConfigurationProperties(prefix = "digitalhuman.auth.spdb-auth-config")
    public SpdbAuthConfig spdbAuthConfig() {
        return new SpdbAuthConfig();
    }

//    @Bean
//    public SpdbAuthService spdbAuthService(SpdbAuthConfig config) {
//        Retrofit retrofit = new Retrofit.Builder()
//                .baseUrl(config.getBaseUrl())
//                .client(new OkHttpClient())
//                .addConverterFactory(GsonConverterFactory.create())
//                .build();
//        return retrofit.create(SpdbAuthService.class);
//    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SpdbAuthConfig {
        private String baseUrl;
    }
}
