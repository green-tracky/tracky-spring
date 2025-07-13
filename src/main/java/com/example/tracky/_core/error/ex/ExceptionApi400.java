package com.example.tracky._core.error.ex;

import com.example.tracky._core.enums.ErrorCodeEnum;

public class ExceptionApi400 extends RuntimeException {
    /**
     * 400 Bad Request (잘못된 요청)
     *
     * @param errorCodeEnum
     */
    public ExceptionApi400(ErrorCodeEnum errorCodeEnum) {
        super(errorCodeEnum.getMessage());
    }

    /**
     * 400 Bad Request (잘못된 요청)
     * <p>
     * GlobalValidationHandler 용
     *
     * @param message
     */
    public ExceptionApi400(String message) {
        super(message);
    }
}
