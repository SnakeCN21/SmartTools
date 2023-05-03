package com.snake.smarttools.config;

import lombok.Data;

import java.io.Serializable;

/**
 * 响应信息主体
 */
@Data
public class ResponseData<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    private int code;
    private String msg;
    private T data;

    public ResponseData() {
    }

    public ResponseData(int code, String msg) {
        this(code, msg, null);
    }

    public ResponseData(int code, String msg, T result) {
        super();
        this.code(code).message(msg).result(result);
    }

    public static <T> ResponseData<T> success(T data) {
        ResponseData<T> responseData = new ResponseData<>();
        responseData.setCode(SysStubInfo.DEFAULT_SUCCESS.getCode());
        responseData.setMsg(SysStubInfo.DEFAULT_SUCCESS.getMsg());
        responseData.setData(data);
        return responseData;
    }

    public static <T> ResponseData<T> error(String msg) {
        return error(SysStubInfo.DEFAULT_FAIL.getCode(), msg);
    }

    public static <T> ResponseData<T> error(int code, String msg) {
        ResponseData<T> responseData = new ResponseData<>();
        responseData.setCode(code);
        responseData.setMsg(msg);
        return responseData;
    }

    public ResponseData(RetStub sysStubInfo, T result) {
        this.code = sysStubInfo.getCode();
        this.msg = sysStubInfo.getMsg();
        this.data = result;
    }

    public ResponseData(RetStub sysStubInfo) {
        this.code = sysStubInfo.getCode();
        this.msg = sysStubInfo.getMsg();
    }

    public static ResponseData success() {
        ResponseData view = new ResponseData(SysStubInfo.DEFAULT_SUCCESS);
        return view;
    }

    public static ResponseData success(String message) {
        ResponseData view = new ResponseData(SysStubInfo.DEFAULT_SUCCESS);
        view.setMsg(message);
        return view;
    }

    public static ResponseData fail() {
        ResponseData view = new ResponseData(SysStubInfo.DEFAULT_FAIL);
        return view;
    }

    public static ResponseData fail(String message) {
        ResponseData view = new ResponseData(SysStubInfo.DEFAULT_FAIL);
        view.setMsg(message);
        return view;
    }

    public static <E> ResponseData<E> info(int code, String message) {
        return new ResponseData<>(code, message);
    }

    public static <E> ResponseData<E> info(int code, String message, E result) {
        return new ResponseData<>(code, message, result);
    }


    private ResponseData<T> message(String message) {
        this.setMsg(message);
        return this;
    }

    private ResponseData<T> code(int code) {
        this.setCode(code);
        return this;
    }

    private ResponseData<T> result(T result) {
        this.setData(result);
        return this;
    }

    public boolean assertSuccess() {
        return this.code == SysStubInfo.DEFAULT_SUCCESS.getCode();
    }
}
