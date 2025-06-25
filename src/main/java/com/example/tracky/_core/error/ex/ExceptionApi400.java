package com.example.tracky._core.error.ex;

import com.example.tracky._core.error.ErrorCodeEnum;

public class ExceptionApi400 extends RuntimeException {
    /**
     * 400 Bad Request (잘못된 요청)
     *
     * @param errorCode
     */
    public ExceptionApi400(ErrorCodeEnum errorCode) {
        super(errorCode.getMessage());
    }
}
