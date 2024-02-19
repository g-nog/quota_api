package com.nogueira.quota;

import com.nogueira.quota.interceptor.RateLimiterInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
@EnableElasticsearchRepositories(basePackages = "com.nogueira.quota.repositories.elastic")
@EnableJpaRepositories(basePackages = "com.nogueira.quota.repositories.jpa")
public class QuotaApplication implements WebMvcConfigurer {

    private final RateLimiterInterceptor rateLimiterInterceptor;

    @Autowired
    public QuotaApplication(RateLimiterInterceptor rateLimiterInterceptor) {
        this.rateLimiterInterceptor = rateLimiterInterceptor;
    }

    public static void main(String[] args) {
        SpringApplication.run(QuotaApplication.class, args);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(rateLimiterInterceptor).addPathPatterns("/quota/**");
    }

}
