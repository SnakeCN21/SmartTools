package com.snake.smarttools.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
@Slf4j
public class ResponseAdvice implements ResponseBodyAdvice {
    private static final Map EMPTY_DATA = new HashMap();

    @Override
    public boolean supports(MethodParameter methodParameter, Class aClass) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object o,
                                  MethodParameter methodParameter,
                                  MediaType mediaType,
                                  Class aClass, ServerHttpRequest serverHttpRequest,
                                  ServerHttpResponse serverHttpResponse) {
        Object result;
        if (o == null) {
            o = EMPTY_DATA;
        }
        if (o instanceof ResponseData) {
            result = o;
        } else {
            ResponseData objectJsonView = ResponseData.success();
            objectJsonView.setData(o);
            result = objectJsonView;
        }
        return result;
    }

}