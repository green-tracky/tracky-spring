package com.example.tracky._core.error.ex;

import com.example.tracky._core.enums.ErrorCodeEnum;

public class ExceptionApi404 extends RuntimeException {
    /**
     * 404 Not Found (찾을 수 없음)
     *
     * @param errorCodeEnum
     */
    public ExceptionApi404(ErrorCodeEnum errorCodeEnum) {
        super(errorCodeEnum.getMessage());
    }
}
