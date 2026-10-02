package br.com.labcontrol.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Registra o SessaoInterceptor para proteger todas as rotas /api/**,
 * exceto /api/auth/** (login não pode exigir estar logado).
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new SessaoInterceptor())
            .addPathPatterns("/api/**")
            .excludePathPatterns("/api/auth/**");
    }
}
