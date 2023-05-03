package com.snake.smarttools.exception;

import com.snake.smarttools.config.RetStub;

/**
 * 全局自定义异常,所有可知异常有此异常统一抛出
 */
public class ApplicationException extends RuntimeException {
    private RetStub retStub;
    private Object data;

    public ApplicationException(RetStub retStub) {
        this.retStub = retStub;
    }

    public ApplicationException(RetStub retStub, Object data) {
        this.data = data;
        this.retStub = retStub;
    }

    public RetStub getRetStub() {
        return retStub;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
    public void setRetStub(RetStub retStub) {
        this.retStub = retStub;
    }

    @Override
    public String getMessage() {
        if(retStub != null){
            return retStub.getCode() + " : " + retStub.getMsg();
        } else {
            return data.toString();
        }
    }
}
