package com.xczx.base.handler;

import com.xczx.base.exception.RestExceptionResponse;
import com.xczx.base.exception.XczxException;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: ilovesshan
 * @date: 2023/4/23
 * @description:
 */

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 处理自定义异常
    @ExceptionHandler(XczxException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public RestExceptionResponse handlXczxException(XczxException e) {
        String traceID = MDC.get("traceID");
        String traceTime = MDC.get("traceTime");
        log.error("XczxException：", e);
        return new RestExceptionResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), traceID, traceTime, e.getMessage());
    }

    // 处理JSR303异常
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public RestExceptionResponse handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        ObjectError error = e.getBindingResult().getAllErrors().get(0);
        String traceID = MDC.get("traceID");
        String traceTime = MDC.get("traceTime");
        log.error("JSR303Exception：", e);
        return new RestExceptionResponse(HttpStatus.BAD_REQUEST.value(), traceID, traceTime, error.getDefaultMessage());
    }

    // 处理其他异常
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public RestExceptionResponse handleException(Exception e) {
        if ("不允许访问".equals(e.getMessage())) {
            return new RestExceptionResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), null, null, "您暂时没有访问权限");
        }
        String traceID = MDC.get("traceID");
        String traceTime = MDC.get("traceTime");
        log.error("Exception：", e);
        return new RestExceptionResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), traceID, traceTime, e.getMessage());
    }
}
