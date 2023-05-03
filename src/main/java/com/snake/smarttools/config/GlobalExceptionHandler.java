package com.snake.smarttools.config;

import com.snake.smarttools.exception.ApplicationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.List;
import java.util.Map;

/**
 * 全局的的异常拦截器（拦截所有的控制器）（带有@RequestMapping注解的方法上都会拦截）
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    private static final Map EMPTY_DATA = null;

    /**
     * 自定义异常
     *
     * @param ex
     * @return
     */
    @ExceptionHandler(ApplicationException.class)
    public ResponseData ApplicationExceptionHandler(ApplicationException ex) {
        log.error("Exception for handle ", ex);
        ResponseData responseData = new ResponseData();
        responseData.setData(EMPTY_DATA);
        responseData.setCode(ex.getRetStub().getCode());
        responseData.setMsg(ex.getRetStub().getMsg());
        if (ex.getData() != null) {
            responseData.setData(ex.getData());
        }
        return responseData;
    }

    /**
     * 方法非法请求
     *
     * @param ex
     * @return
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseData illegalArgumentExceptionHandler(IllegalArgumentException ex) {
        log.error("Exception for handle ", ex);
        ResponseData responseData = new ResponseData();
        responseData.setData(EMPTY_DATA);
        responseData.setCode(SysStubInfo.PARAMETER_TYPE_INVALID.getCode());
        responseData.setMsg(SysStubInfo.PARAMETER_TYPE_INVALID.getMsg());
        return responseData;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseData methodArgumentNotValidExceptionHandler(MethodArgumentNotValidException ex) {
        log.error("Exception for handle ", ex);
        List<ObjectError> allErrors = ex.getBindingResult().getAllErrors();
        StringBuilder stringBuilder = new StringBuilder();
        for (ObjectError error : allErrors) {
            stringBuilder.append("[").append(error.getDefaultMessage()).append("]");
        }
        String msg = stringBuilder.toString();
        log.error("ArgumentNotValid  msg is : " + msg);
        return new ResponseData(SysStubInfo.PARAMETER_TYPE_INVALID, msg);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseData missingServletRequestParameterExceptionHandler(MissingServletRequestParameterException ex) {
        log.error("Exception for handle ", ex);
        String parameterName = ex.getParameterName();
        String parameterType = ex.getParameterType();
        String msg = ("parameter " + parameterName + " is null " + " , expect: " + parameterType);
        return new ResponseData(SysStubInfo.PARAMETER_IS_NULL, msg);
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseData httpMediaTypeNotSupportedExceptionHandler(HttpMediaTypeNotSupportedException ex) {
        log.error("Exception for handle ", ex);
        String msg = ex.getContentType().getSubtype();
        return new ResponseData(SysStubInfo.CONTENT_TYPE_INVALID, msg);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseData httpRequestMethodNotSupportedExceptionHandler(HttpRequestMethodNotSupportedException ex) {
        log.error("Exception for handle ", ex);
        String msg = ex.getMethod();
        return new ResponseData(SysStubInfo.METHOD_INVALID, msg);
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseData noHandlerFoundExceptionHandler(NoHandlerFoundException ex) {
        log.error("Exception for handle ", ex);
        String msg = (ex.getHttpMethod() + " --> " + ex.getRequestURL());
        return new ResponseData(SysStubInfo.RESOURCE_INVALID, msg);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseData methodArgumentTypeMismatchExceptionExceptionHandler(MethodArgumentTypeMismatchException ex) {
        log.error("Exception for handle ", ex);
        String msg = ("parameter " + ex.getName() + " is not type of " + ex.getRequiredType().getSimpleName());
        return new ResponseData(SysStubInfo.PARAMETER_TYPE_INVALID, msg);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseData httpMessageNotReadableExceptionHandler(HttpMessageNotReadableException ex) {
        log.error("Exception for handle ", ex);
        String msg = ex.getMessage();
        return new ResponseData(SysStubInfo.REQUEST_BODY_INVALID, msg);
    }

    @ExceptionHandler(BindException.class)
    public ResponseData bindExceptionHandler(BindException ex) {
        log.error("Exception for handle ", ex);
        BindingResult bindingResult = ex.getBindingResult();
        FieldError fieldError = bindingResult.getFieldError();
        String field = fieldError.getField();
        return new ResponseData(SysStubInfo.PARAMETER_TYPE_INVALID, field);
    }

    @ExceptionHandler(Exception.class)
    public ResponseData exceptionHandler(Exception ex) {
        log.error("Exception for handle ", ex);
        return new ResponseData(SysStubInfo.DEFAULT_FAIL);
    }
}