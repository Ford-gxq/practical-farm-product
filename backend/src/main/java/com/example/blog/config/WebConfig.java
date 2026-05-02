package com.example.blog.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Web MVC 全局配置。
 *
 * 主要负责：
 * 1. 跨域配置。
 * 2. 后台接口登录拦截。
 * 3. 图片静态资源映射。
 */
@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    /** 后台登录拦截器。 */
    private final AdminAuthInterceptor adminAuthInterceptor;

    /**
     * 图片根目录。
     *
     * 默认值 ../frontend/public/asset/images 是相对 backend 目录的路径。
     * 后端启动后会把它转换成绝对路径，再映射给浏览器访问。
     */
    @Value("${app.upload.image-root-dir:${app.upload.image-dir:../frontend/public/asset/images}}")
    private String imageRootDir;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(false)
                .maxAge(3600);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(adminAuthInterceptor)
                .addPathPatterns("/api/admin/**")
                .excludePathPatterns("/api/admin/auth/login");
    }

    /**
     * 浏览器访问 /asset/images/** 时，映射到前端 public 目录下的图片根目录。
     *
     * 示例：
     * /asset/images/hero-farm.svg -> frontend/public/asset/images/hero-farm.svg
     * /asset/images/covers/a.jpg -> frontend/public/asset/images/covers/a.jpg
     * /asset/images/content/b.jpg -> frontend/public/asset/images/content/b.jpg
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/asset/images/**")
                .addResourceLocations("file:" + resolveImageRootDir());
    }

    /** 把相对路径或 Windows 路径转换为 Spring 静态资源映射需要的绝对目录路径。 */
    private String resolveImageRootDir() {
        Path configuredPath = Paths.get(imageRootDir);
        Path rootPath;

        if (configuredPath.isAbsolute()) {
            rootPath = configuredPath.toAbsolutePath().normalize();
        } else {
            Path userDir = Paths.get(System.getProperty("user.dir")).toAbsolutePath().normalize();
            rootPath = userDir.resolve(configuredPath).normalize();
        }

        String normalized = rootPath.toString().replace("\\", "/");
        if (!normalized.endsWith("/")) {
            normalized += "/";
        }
        return normalized;
    }
}
