package com.campus.trading.dto;

import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import javax.servlet.http.HttpServletResponse;

@ControllerAdvice
public class ApiResponseAdvice implements ResponseBodyAdvice<Object> {
    @Override
    public boolean supports(MethodParameter returnType, Class converterType) {
        // 只处理ApiResponse类型
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
                                  Class selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        if (body instanceof ApiResponse) {
            ApiResponse<?> apiResponse = (ApiResponse<?>) body;
            // 设置HTTP状态码
            if (response instanceof org.springframework.http.server.ServletServerHttpResponse) {
                HttpServletResponse servletResponse =
                        ((org.springframework.http.server.ServletServerHttpResponse) response).getServletResponse();
                if (apiResponse.getCode() != null) {
                    servletResponse.setStatus(apiResponse.getCode());
                }
            }
        }
        return body;
    }
}
