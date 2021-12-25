package com.qiwenshare.file.util;

import com.qiwenshare.file.config.security.user.JwtUser;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.security.Principal;

public class SessionUtil {

    public static JwtUser getSession() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();
        if (principal instanceof String) {
            String userName = (String) principal;
            if ("anonymousUser".equals(userName)) {
                JwtUser userBean = new JwtUser();
                userBean.setUsername(userName);
                userBean.setUserId(0L);
                return userBean;
            }
        }
        JwtUser userBean = (JwtUser) authentication.getPrincipal();
        return userBean;
    }

    public static JwtUser getSession(Principal principal) {
        if (principal == null) {
            return null;
        }
        JwtUser userBean = (JwtUser) ((UsernamePasswordAuthenticationToken) principal).getPrincipal();
        return userBean;
    }
}
