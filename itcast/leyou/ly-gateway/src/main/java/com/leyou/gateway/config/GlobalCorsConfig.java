package com.leyou.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class GlobalCorsConfig {
    @Bean
    public CorsFilter corsFilter(CorsProperties properties) {
        //1.添加CORS配置信息
        CorsConfiguration config = new CorsConfiguration();
        //1) 允许的域,不要写*，否则cookie就无法使用了
        properties.getAllowedOrigins().forEach(config::addAllowedOrigin);
        //2) 是否发送Cookie信息
        config.setAllowCredentials(properties.getAllowCredentials());
        //3) 允许的请求方式
        properties.getAllowedMethods().forEach(config::addAllowedMethod);
        // 4）允许的头信息
        properties.getAllowedHeaders().forEach(config::addAllowedHeader);
        //5)允许的时间
        config.setMaxAge(properties.getMaxAge());

        //2.添加映射路径，我们拦截一切请求
        UrlBasedCorsConfigurationSource configSource = new UrlBasedCorsConfigurationSource();
        configSource.registerCorsConfiguration(properties.getPath(), config);

        //3.返回新的CorsFilter.
        return new CorsFilter(configSource);
    }
}