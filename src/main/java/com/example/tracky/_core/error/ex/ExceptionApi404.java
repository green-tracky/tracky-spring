package com.example.tracky._core.error.ex;

import com.example.tracky._core.error.ErrorCodeEnum;

public class ExceptionApi404 extends RuntimeException {
    /**
     * 404 Not Found (찾을 수 없음)
     *
     * @param errorCode
     */
    public ExceptionApi404(ErrorCodeEnum errorCode) {
        super(errorCode.getMessage());
    }
}
