//package com.qiwenshare.file.interceptor;
//
//import com.qiwenshare.file.api.IUserService;
//import com.qiwenshare.file.domain.UserBean;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpHeaders;
//import org.springframework.web.servlet.HandlerInterceptor;
//import org.springframework.web.servlet.ModelAndView;
//
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//
///**
// * token验证拦截
// */
//@Slf4j
//public class AuthenticationInterceptor implements HandlerInterceptor {
//    @Autowired
//    private IUserService userService;
//    @Override
//    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
//            throws Exception {
//        log.info("---------------------------------------------------------");
//        // 取得token
//        String tokenHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
//        if (tokenHeader == null || "".equals(tokenHeader)) {
//            throw new Exception("token不存在");
//        }
//        if (!tokenHeader.startsWith("Bearer")) {
//            throw new Exception("token格式错误");
//        }
//        String token = tokenHeader.replace("Bearer", "");
//        UserBean userBean = userService.getUserBeanByToken(token);
//        if (userBean == null) {
//            return  false;
//        }
//
//        return true;
//    }
//
//    @Override
//    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
//                           ModelAndView modelAndView) throws Exception {
//        // TODO Auto-generated method stub
//
//    }
//
//    @Override
//    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
//            throws Exception {
//        // TODO Auto-generated method stub
//
//    }
//
//}
//