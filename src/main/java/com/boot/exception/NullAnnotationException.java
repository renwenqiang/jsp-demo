package com.boot.exception;

/**
 * 空注解异常
 * @author WindShadow
 * @verion 2020/8/17.
 */
public class NullAnnotationException extends Exception {

    public NullAnnotationException() {}

    public NullAnnotationException(String message) {
        super(message);
    }

    public NullAnnotationException(String message, Throwable cause) {
        super(message, cause);
    }

    public NullAnnotationException(Throwable cause) {
        super(cause);
    }

    public NullAnnotationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
