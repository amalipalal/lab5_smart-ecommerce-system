package com.example.ecommerce_system.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@Order(2)
public class AdminInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler
    ) {
        HandlerMethod method = (HandlerMethod) handler;

        boolean requiresAdmin = method.hasMethodAnnotation(RequireAdmin.class)
                || method.getBeanType().isAnnotationPresent(RequireAdmin.class);

        if(requiresAdmin) {
            String userRole = (String)  request.getAttribute("userRole");

            if(!"ADMIN".equals(userRole)) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                return false;
            }
        }

        return true;
    }

}
