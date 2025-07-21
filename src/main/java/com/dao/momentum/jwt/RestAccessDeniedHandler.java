package com.dao.momentum.jwt;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class RestAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException, ServletException {
        if (response.isCommitted()) {
            log.warn("응답이 이미 커밋되어 AccessDeniedHandler가 처리되지 못함. URI: {}, 예외: {}",
                    request.getRequestURI(), accessDeniedException.getMessage());
            return;
        }

        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_FORBIDDEN); // 403, 인가 실패
        String jsonResponse = "{\"error\": \"Forbidden\", \"message\": \"" + accessDeniedException.getMessage() + "\"}";
        response.getWriter().write(jsonResponse);
    }
}