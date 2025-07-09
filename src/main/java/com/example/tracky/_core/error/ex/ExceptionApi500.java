package com.example.tracky._core.error.ex;

import com.example.tracky._core.enums.ErrorCodeEnum;

public class ExceptionApi500 extends RuntimeException {
    /**
     * 500 Internal Server Error (서버 문제)
     *
     * @param errorCodeEnum
     */
    public ExceptionApi500(ErrorCodeEnum errorCodeEnum) {
        super(errorCodeEnum.getMessage());
    }
}
