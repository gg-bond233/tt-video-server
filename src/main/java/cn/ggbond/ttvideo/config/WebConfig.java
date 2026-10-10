package cn.ggbond.ttvideo.config;


import cn.ggbond.ttvideo.interceptor.LoginInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private LoginInterceptor loginInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 未登录可访问的公开接口不拦截
        registry.addInterceptor(loginInterceptor)
                .addPathPatterns("/**") // 拦截所有请求
                .excludePathPatterns(
                        // Spring Boot 内置错误转发路径，必须排除，否则
                        // 业务异常转发到 /error 时会被拦截器 403
                        "/error", "/error/**",
                        // 管理端公开接口（登录 / 注册 / 登出）
                        "/admin/login", "/admin/register", "/admin/logout",
                        // 用户端公开接口
                        "/user/login", "/user/logout", "/user/register",
                        "/user/sendSecurityCode", "/user/resetUserPassword",
                        "/banner/getBannerList",
                        "/video/getAllVideos", "/video/getRecommendedVideos",
                        "/video/getVideoDetail/**", "/video/getVideosByCategory");
    }
}
