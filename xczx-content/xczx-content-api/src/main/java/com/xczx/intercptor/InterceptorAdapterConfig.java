package com.xczx.intercptor;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import javax.annotation.Resource;


@Configuration
public class InterceptorAdapterConfig extends WebMvcConfigurerAdapter {
    @Resource
    private TraceInterceptor traceInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //注册自己的拦截器并设置拦截的请求路径
        registry.addInterceptor(traceInterceptor).addPathPatterns("/**");
        super.addInterceptors(registry);
    }
}