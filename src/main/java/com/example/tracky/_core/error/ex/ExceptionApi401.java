package com.example.tracky._core.error.ex;

import com.example.tracky._core.error.ErrorCodeEnum;

public class ExceptionApi401 extends RuntimeException {
    public ExceptionApi401(ErrorCodeEnum errorCode) {
        super(errorCode.getMessage());
    }
}
