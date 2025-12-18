//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.servlet.config.annotation.CorsRegistry;
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
//
//@Configuration
//public class CorsConfig implements WebMvcConfigurer {
//    @Override
//    public void addCorsMappings(CorsRegistry registry) {
//        registry.addMapping("/**") // 对所有接口生效
//                .allowedOrigins("http://localhost:5173") // 允许前端源
//                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // 允许请求方法
//                .allowedHeaders("*") // 允许所有请求头
//                .allowCredentials(true) // 允许携带cookie
//                .maxAge(3600); // 预检请求缓存时间
//    }
//}