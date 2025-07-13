package com.example.tracky._core.error;

import com.example.tracky._core.error.ex.ExceptionApi400;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;

import java.util.List;

// Aspect, PointCut, Advice
@Aspect // 관점 관리
@Component
public class GlobalValidationHandler {

    // 관심사를 분리시킴
    // PostMapping 혹은 PutMapping이 붙어있는 메서드를 실행하기 직전에 Advice를 호출하라.
    @Before("@annotation(org.springframework.web.bind.annotation.PostMapping) || @annotation(org.springframework.web.bind.annotation.PutMapping)")
    public void badRequestAdvice(JoinPoint jp) { // jp는 실행될 실제 메서드의 모든 것을 투영하고 있다.
        Object[] args = jp.getArgs(); // 메서드의 매개변수들
        for (Object arg : args) { // 매개변수 개수만큼 반복 (어노테이션은 제외)

            // Errors 타입 매개변수에 존재하고!!
            if (arg instanceof Errors) {
                Errors errors = (Errors) arg;

                // 에러가 존재한다면!!
                if (errors.hasErrors()) {
                    List<FieldError> fErrors = errors.getFieldErrors();

                    for (FieldError fieldError : fErrors) {
                        throw new ExceptionApi400(fieldError.getField() + " : " + fieldError.getDefaultMessage());
                    }
                }
            }
        }
    }
}