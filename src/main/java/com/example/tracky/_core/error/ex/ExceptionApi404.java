package com.example.tracky._core.error.ex;

import com.example.tracky._core.error.ErrorCodeEnum;

public class ExceptionApi404 extends RuntimeException {
    public ExceptionApi404(ErrorCodeEnum errorCode) {
        super(errorCode.getMessage());
    }
}
