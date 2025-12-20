package com.wunai.springbook1.config;
import com.wunai.springbook1.interceptors.LoginInterceptor;
import com.wunai.springbook1.interceptors.PermissionInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
@Configuration
public class WebConfig implements WebMvcConfigurer {


    @Autowired
    private LoginInterceptor loginInterceptor;
    @Value("${file.upload.base-path}")
    private String basePath;

    @Value("${file.upload.access-path}")
    private String accessPath;
    @Autowired
    private PermissionInterceptor permissionInterceptor;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // 对所有接口生效
                .allowedOrigins("http://localhost:5173") // 允许前端源
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH") // 允许请求方法
                .allowedHeaders("*") // 允许所有请求头
                .allowCredentials(true) // 允许携带cookie
                .maxAge(3600); // 预检请求缓存时间
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        registry.addInterceptor(loginInterceptor)
                // 需要登录的接口（放行登录、注册、文件下载）
                .addPathPatterns("/**")
                .excludePathPatterns("/user/login","/user/register", "/files/download/**");// 放行登录注册接口和文件下载接口

        registry.addInterceptor(permissionInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/user/login", "/user/register",
                        // 普通用户可访问的接口
                        "/bugs/submit", "/bugs/query", "/bugs/onebug/**",
                        "/user/userInfo", "/files/download/**");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 注意：不映射 /files/** 到静态资源，因为文件下载由 FileController 处理
        // 如果需要直接访问静态文件，可以使用其他路径，如 /static/**
        // 这里注释掉，避免与 FileController 的 /files/download/** 冲突
        // registry.addResourceHandler(accessPath)
        //         .addResourceLocations("file:" + basePath);
    }

}
