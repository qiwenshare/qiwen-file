package com.qiwenshare.file.aop;

import com.qiwenshare.common.anno.MyLog;
import com.qiwenshare.common.result.RestResult;
import com.qiwenshare.file.api.IOperationLogService;
import com.qiwenshare.file.api.IUserService;
import com.qiwenshare.file.domain.UserBean;
import com.qiwenshare.file.util.OperationLogUtil;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.CodeSignature;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * 操作日志切面
 */
@Aspect
@Component
public class WebLogAcpect {
    @Resource
    IOperationLogService operationLogService;
    @Resource
    IUserService userService;

    private String operation = "";
    private String module = "";
    private String token = "";
    private HttpServletRequest request;


    /**
     * 定义切入点，切入点为com.example.aop下的所有函数
     */
    @Pointcut("@annotation(com.qiwenshare.common.anno.MyLog)")
    public void webLog() {
    }

    /**
     * 前置通知：在连接点之前执行的通知
     *
     * @param joinPoint 切入点
     */
    @Before("webLog()")
    public void doBefore(JoinPoint joinPoint) {
        //从切面织入点处通过反射机制获取织入点处的方法
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        //获取切入点所在的方法
        Method method = signature.getMethod();
        Map<String, Object> map = getNameAndValue(joinPoint);

        //获取操作
        MyLog myLog = method.getAnnotation(MyLog.class);

        if (myLog != null) {
            operation = myLog.operation();
            module = myLog.module();
            token = (String) map.get("token");
        }

        // 接收到请求，记录请求内容
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        request = attributes.getRequest();


    }

    @AfterReturning(returning = "ret", pointcut = "webLog()")
    public void doAfterReturning(Object ret) throws Throwable {

        if (ret instanceof RestResult) {
            boolean isSuccess = ((RestResult) ret).getSuccess();
            String errorMessage = ((RestResult) ret).getMessage();
            UserBean sessionUserBean = userService.getUserBeanByToken(token);
            if (isSuccess) {

                operationLogService.insertOperationLog(
                        OperationLogUtil.getOperationLogObj(request,sessionUserBean, "成功", module, operation, "操作成功"));
            } else {
                operationLogService.insertOperationLog(
                        OperationLogUtil.getOperationLogObj(request,sessionUserBean, "失败", module, operation, errorMessage));
            }
        }


    }

    /**
     * 获取参数Map集合
     * @param joinPoint
     * @return
     */
    Map<String, Object> getNameAndValue(JoinPoint joinPoint) {
        Map<String, Object> param = new HashMap<>();
        Object[] paramValues = joinPoint.getArgs();
        String[] paramNames = ((CodeSignature)joinPoint.getSignature()).getParameterNames();
        for (int i = 0; i < paramNames.length; i++) {
            param.put(paramNames[i], paramValues[i]);
        }
        return param;
    }
}