package com.campus.trading.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * JWT认证过滤器
 * 在每个请求前检查JWT令牌的有效性，并设置Spring Security上下文
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private JwtUtils jwtUtils;
    
    private final AntPathMatcher pathMatcher = new AntPathMatcher();
    
    // 定义公开路径列表，不需要token验证
    private final List<String> publicPaths = Arrays.asList(
            "/auth/login",
            "/auth/register",
            "/auth/check-username",
            "/auth/check-email",
            "/auth/check-phone",
            "/auth/check-email-code",
            "/auth/check-phone-code",
            "/auth/check-email-code-by-phone",
            "/api/users/**", 
            "/image/**",
            "/api/image/**",
            "/items/public/**",
            "/items/statistics",
            "/h2-console/**",
            "/swagger-ui/**", 
            "/swagger-resources/**", 
            "/v3/api-docs/**",
            "/**/debug/**"
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        
        // 检查是否是公开路径，如果是，直接放行
        String requestPath = request.getServletPath();
        
        // 调试信息
        logger.info("处理请求: " + requestPath);
        
        if (isPublicPath(requestPath)) {
            logger.info("公开路径，不需要验证: " + requestPath);
            chain.doFilter(request, response);
            return;
        }

        final String authHeader = request.getHeader("Authorization");

        String username = null;
        String jwt = null;

        // 提取JWT令牌
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            jwt = authHeader.substring(7);
            try {
                username = jwtUtils.getUsernameFromToken(jwt);
            } catch (Exception e) {
                logger.error("无法验证JWT令牌", e);
            }
        }

        // 验证令牌并设置认证
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

            if (jwtUtils.validateToken(jwt, userDetails)) {
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                
                SecurityContextHolder.getContext().setAuthentication(authentication);
                logger.info("为用户 '" + username + "' 设置了安全上下文");
            }
        }

        chain.doFilter(request, response);
    }
    
    /**
     * 检查请求路径是否匹配公开路径列表中的任何一个
     */
    private boolean isPublicPath(String requestPath) {
        for (String pattern : publicPaths) {
            if (pathMatcher.match(pattern, requestPath)) {
                return true;
            }
        }
        return false;
    }
} 