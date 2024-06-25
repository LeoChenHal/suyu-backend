package com.lch.suyu.exception;

import com.lch.suyu.Result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class BusinessExceptionAdvice {
    @ExceptionHandler(BusinessException.class)
    public Result businessException(BusinessException e) {
        log.error("BusinessException was thrown");
        return Result.error(e.getMeg());
    }
}
