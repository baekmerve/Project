package com.example.actionprice.security.config;

import com.example.actionprice.security.interceptor.ExceptionHandlerInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 필터에서 발생하는 에러를 잡아주기 위한 config
 * @author 연상훈
 * @created 2024-11-12 오후 4:37
 */
@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

  private final ExceptionHandlerInterceptor exceptionHandlerInterceptor;

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(exceptionHandlerInterceptor)
        .addPathPatterns("/**");
  }
}
