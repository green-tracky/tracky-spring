package com.example.tracky._core.error.ex;

import com.example.tracky._core.error.ErrorCodeEnum;

public class ExceptionApi403 extends RuntimeException {
    /**
     * 403 Forbidden (접근 금지 및 권한 없음)
     *
     * @param errorCode
     */
    public ExceptionApi403(ErrorCodeEnum errorCode) {
        super(errorCode.getMessage());
    }
}
