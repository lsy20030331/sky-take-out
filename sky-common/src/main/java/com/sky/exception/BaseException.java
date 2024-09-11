package com.sky.exception;

/**
 * 业务异常
 */
public class BaseException extends RuntimeException {  // 自定义中所有异常都继承于它

    public BaseException() {
    }

    public BaseException(String msg) {
        super(msg);
    }

}
