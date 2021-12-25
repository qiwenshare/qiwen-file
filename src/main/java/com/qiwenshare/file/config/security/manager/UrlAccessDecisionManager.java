package com.qiwenshare.file.config.security.manager;

import org.springframework.http.HttpMethod;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.FilterInvocation;
import org.springframework.stereotype.Component;

import java.util.Collection;

/**
 * 根据用户信息和权限去与当前访问的url需要的权限进行对比
 */
@Component
public class UrlAccessDecisionManager implements AccessDecisionManager {

    @Override
    public void decide(Authentication authentication, Object o, Collection<ConfigAttribute> collection) throws AccessDeniedException, InsufficientAuthenticationException {
        //放行options请求
        FilterInvocation fi = (FilterInvocation) o;
        if (HttpMethod.OPTIONS.name().equals(fi.getRequest().getMethod())) {
            return;
        }
        // collection是url过滤器给过来的权限列表，判断url是否需要拦截
        for (ConfigAttribute attribute : collection) {
            if (!"ROLE_ANONYMOUS".equals(attribute.toString())) {   // 需要拦截的
                // 看是否有用户信息
                Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
                if (principal == null) throw new AccessDeniedException("expire");

                // 看权限是否足够
                Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
                for (GrantedAuthority authority : authorities) {
                    // authority.getAuthority()是jwt过滤器给过来的权限
                    // attribute.getAttribute()是url过滤器给过来的权限
                    if (authority.getAuthority().equals(attribute.getAttribute())) {
                        return;
                    }
                }
                throw new AccessDeniedException("not allow");
            }
        }
    }

    @Override
    public boolean supports(ConfigAttribute configAttribute) {
        return true;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return true;
    }
}
