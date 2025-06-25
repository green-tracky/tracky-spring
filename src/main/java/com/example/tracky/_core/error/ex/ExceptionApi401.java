package com.example.tracky._core.error.ex;

import com.example.tracky._core.error.ErrorCodeEnum;

public class ExceptionApi401 extends RuntimeException {
    /**
     * 401 Unauthorized (인증되지 않음)
     *
     * @param errorCode
     */
    public ExceptionApi401(ErrorCodeEnum errorCode) {
        super(errorCode.getMessage());
    }
}
