package com.digitaltravel.erp.config;

import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.digitaltravel.erp.service.NhatKyHeThongService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class AuditLogConfig implements HandlerInterceptor, WebMvcConfigurer {

    private final NhatKyHeThongService nhatKyHeThongService;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(this).addPathPatterns("/api/**");
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
            org.springframework.web.servlet.ModelAndView modelAndView) {
        if (response.getStatus() >= 400 || !(handler instanceof HandlerMethod handlerMethod)
                || shouldSkip(request) || isReadOnly(handlerMethod)) {
            return;
        }

        String hanhDong = resolveAction(handlerMethod);
        if (hanhDong == null) {
            return;
        }
        String maTaiKhoan = currentAccountId();
        nhatKyHeThongService.ghiNhan(maTaiKhoan, hanhDong, request.getRequestURI(), primaryPathVariable(request));
    }

    private boolean shouldSkip(HttpServletRequest request) {
        String uri = request.getRequestURI();
        return uri.startsWith("/api/auth/") || uri.startsWith("/api/quan-tri/nhat-ky-he-thong");
    }

    private boolean isReadOnly(HandlerMethod handlerMethod) {
        return handlerMethod.hasMethodAnnotation(GetMapping.class);
    }

    private String resolveAction(HandlerMethod handlerMethod) {
        if (handlerMethod.hasMethodAnnotation(PostMapping.class)) {
            return NhatKyHeThongService.THEM;
        }
        if (handlerMethod.hasMethodAnnotation(DeleteMapping.class)) {
            return NhatKyHeThongService.XOA;
        }
        if (handlerMethod.hasMethodAnnotation(PutMapping.class)
                || handlerMethod.hasMethodAnnotation(PatchMapping.class)) {
            return NhatKyHeThongService.CAP_NHAT;
        }
        return null;
    }

    private String currentAccountId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof TaiKhoanDetails details)) {
            return null;
        }
        return details.getTaiKhoan().getMaTaiKhoan();
    }

    @SuppressWarnings("unchecked")
    private String primaryPathVariable(HttpServletRequest request) {
        Object variables = request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
        if (variables instanceof Map<?, ?> pathVariables && !pathVariables.isEmpty()) {
            return ((Map<String, String>) pathVariables).values().stream()
                    .collect(Collectors.joining(", "));
        }
        return null;
    }
}
