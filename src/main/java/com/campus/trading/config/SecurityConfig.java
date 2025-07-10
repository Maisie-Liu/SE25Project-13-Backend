package com.campus.trading.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

/**
 * Spring Security 配置类
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    /**
     * 配置安全过滤器链
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .cors().and()
            .csrf().disable()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .authorizeRequests()
            // 明确设置所有用户相关接口为公开接口
            .antMatchers("/api/users/**").permitAll()
            .antMatchers("/users/**").permitAll()
            .antMatchers("/api/users/debug/access").permitAll()
            .antMatchers("/users/debug/access").permitAll()
            // 明确设置所有图片相关接口为公开接口
            .antMatchers("/image/**").permitAll()
            .antMatchers("/api/image/**").permitAll()
            .antMatchers("/image/debug/status").permitAll()
            // 其他公开接口
            .antMatchers("/auth/login").permitAll()
            .antMatchers("/auth/register").permitAll()
            .antMatchers("/auth/check-username").permitAll()
            .antMatchers("/auth/check-email").permitAll()
            .antMatchers("/auth/check-phone").permitAll()
            .antMatchers("/auth/check-email-code").permitAll()
            .antMatchers("/auth/check-phone-code").permitAll()
            .antMatchers("/items/public/**").permitAll()
            .antMatchers("/items/statistics").permitAll()
            .antMatchers("/h2-console/**").permitAll()
            // Swagger UI相关接口
            .antMatchers("/swagger-ui/**", "/swagger-resources/**", "/v3/api-docs/**").permitAll()
            // 测试和调试接口
            .antMatchers("/**/debug/**").permitAll()
            // 其他所有请求需要认证
            .anyRequest().authenticated()
            .and()
            .exceptionHandling()
            .authenticationEntryPoint((request, response, authException) -> {
                response.setContentType("application/json;charset=UTF-8");
                response.setStatus(403);
                response.getWriter().write("{\"code\":403,\"message\":\"访问被拒绝，请先登录\"}");
            });
            
        // 允许H2控制台的frame显示
        http.headers().frameOptions().disable();
        
        // 添加JWT过滤器
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }

    /**
     * 密码编码器
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 认证管理器
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    /**
     * CORS配置
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000", "http://localhost:8080"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "X-Requested-With"));
        configuration.setExposedHeaders(Arrays.asList("Authorization"));
        configuration.setAllowCredentials(true);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
} 