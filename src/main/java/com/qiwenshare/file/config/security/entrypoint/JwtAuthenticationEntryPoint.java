package com.qiwenshare.file.config.security.entrypoint;

import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * AuthenticationEntryPoint 用来解决匿名用户访问无权限资源时的异常
 * AccessDeineHandler 用来解决认证过的用户访问无权限资源时的异常
 * */
@Component
public class JwtAuthenticationEntryPoint implements org.springframework.security.web.AuthenticationEntryPoint {

    //当出错的时候 发送 Unauthorized
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {

        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, authException==null?"Unauthorized":authException.getMessage());
    }
}