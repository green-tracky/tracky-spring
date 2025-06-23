package com.example.tracky._core.error.ex;

import com.example.tracky._core.error.ErrorCodeEnum;

public class ExceptionApi403 extends RuntimeException {
    public ExceptionApi403(ErrorCodeEnum errorCode) {
        super(errorCode.getMessage());
    }
}
