package com.example.tracky._core.error.ex;

import com.example.tracky._core.enums.ErrorCodeEnum;

public class ExceptionApi400 extends RuntimeException {
    private final ErrorCodeEnum errorCode;

//    /**
//     * 400 Bad Request (잘못된 요청)
//     *
//     * @param errorCodeEnum
//     */
//    public ExceptionApi400(ErrorCodeEnum errorCodeEnum) {
//        super(errorCodeEnum.getMessage());
//    }

    // 메시지와 에러코드 모두 받는 생성자
    public ExceptionApi400(String message, ErrorCodeEnum errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    // 기존 : 에러코드만 받으면 에러코드 메시지로 세팅
    public ExceptionApi400(ErrorCodeEnum errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public ErrorCodeEnum getErrorCode() {
        return errorCode;
    }
}
