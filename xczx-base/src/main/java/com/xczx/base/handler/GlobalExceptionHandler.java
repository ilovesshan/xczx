package com.xczx.base.handler;

import com.xczx.base.exception.RestExceptionResponse;
import com.xczx.base.exception.XczxException;
import lombok.extern.slf4j.Slf4j;
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
        log.error(e.getErrMessage());
        return new RestExceptionResponse(e.getErrMessage());
    }

    // 处理JSR303异常
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public RestExceptionResponse handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        ObjectError error = e.getBindingResult().getAllErrors().get(0);
        log.error(error.getDefaultMessage());
        return new RestExceptionResponse(error.getDefaultMessage());
    }

    // 处理其他异常
    @ExceptionHandler(Exception.class)
    public RestExceptionResponse handlException(Exception e) {
        log.error(e.getMessage());
        return new RestExceptionResponse(e.getMessage());
    }
}
