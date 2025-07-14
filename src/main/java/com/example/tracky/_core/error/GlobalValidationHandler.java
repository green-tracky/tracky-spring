package com.example.tracky._core.error;

import com.example.tracky._core.enums.ErrorCodeEnum;
import com.example.tracky._core.error.ex.ExceptionApi400;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;

import java.util.List;

@Aspect
@Component
public class GlobalValidationHandler {

    @Before("@annotation(org.springframework.web.bind.annotation.PostMapping) || @annotation(org.springframework.web.bind.annotation.PutMapping)")
    public void badRequestAdvice(JoinPoint jp) {
        Object[] args = jp.getArgs();

        for (Object arg : args) {
            if (arg instanceof Errors) {
                Errors errors = (Errors) arg;

                if (errors.hasErrors()) {
                    List<FieldError> fErrors = errors.getFieldErrors();

                    // 첫 번째 필드 오류 메시지 가져오기
                    FieldError firstError = fErrors.get(0);

                    // DTO에 명시한 메시지를 그대로 쓰고, ErrorCodeEnum은 INVALID_INVITE_STATUS 같은 기본값 사용
                    throw new ExceptionApi400(
                            firstError.getField() + ": " + firstError.getDefaultMessage(),
                            ErrorCodeEnum.INVALID_INVITE_STATUS
                    );
                }
            }
        }
    }
}