package com.example.tracky._core.error.ex;

import com.example.tracky._core.enums.ErrorCodeEnum;

public class ExceptionApi403 extends RuntimeException {
    /**
     * 403 Forbidden (접근 금지 및 권한 없음)
     *
     * @param errorCodeEnum
     */
    public ExceptionApi403(ErrorCodeEnum errorCodeEnum) {
        super(errorCodeEnum.getMessage());
    }
}
