package com.xczx.base.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.MDC;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: ilovesshan
 * @date: 2023/4/24
 * @description:
 */

//@Slf4j
//@Aspect
//@Configuration
public class LogoAspect {

    /**
     * 定义切点Pointcut
     * 第一个*号：表示返回类型， *号表示所有的类型
     * 第二个*号：表示类名，*号表示所有的类
     * 第三个*号：表示方法名，*号表示所有的方法
     * 后面括弧里面表示方法的参数，两个句点表示任何参数
     */
    // @Pointcut("execution(* com.xczx..*.service.*.*(..))")
    @Pointcut("execution(* com.xczx..*.*Impl.*(..))")
    public void executionService() {
    }

    /**
     * 方法调用之前调用
     *
     * @param joinPoint
     */
    @Before(value = "executionService()")
    public void doBefore(JoinPoint joinPoint) {
        String requestId = UUID.randomUUID().toString().replaceAll("-", "");
        MDC.put("requestId", requestId);
        MDC.put("requestTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date()));
    }

    /**
     * 方法之后调用
     *
     * @param joinPoint
     * @param returnValue 方法返回值
     */
    @AfterReturning(pointcut = "executionService()", returning = "returnValue")
    public void doAfterReturning(JoinPoint joinPoint, Object returnValue) {
        // 处理完请求，返回内容
        MDC.clear();
    }
}