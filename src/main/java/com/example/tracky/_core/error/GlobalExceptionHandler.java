package com.example.tracky._core.error;

import com.example.tracky._core.enums.ErrorCodeEnum;
import com.example.tracky._core.error.ex.*;
import com.example.tracky._core.utils.Resp;
import io.sentry.Sentry;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ExceptionApi400.class)
    public ResponseEntity<?> exApi400(ExceptionApi400 e) {
        log.warn(e.getMessage());
        return Resp.fail(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(ExceptionApi401.class)
    public ResponseEntity<?> exApi401(ExceptionApi401 e) {
        log.warn(e.getMessage());
        return Resp.fail(HttpStatus.UNAUTHORIZED, e.getMessage());
    }

    @ExceptionHandler(ExceptionApi403.class)
    public ResponseEntity<?> exApi403(ExceptionApi403 e) {
        log.warn(e.getMessage());
        return Resp.fail(HttpStatus.FORBIDDEN, e.getMessage());
    }

    @ExceptionHandler(ExceptionApi404.class)
    public ResponseEntity<?> exApi404(ExceptionApi404 e) {
        log.warn(e.getMessage());
        return Resp.fail(HttpStatus.NOT_FOUND, e.getMessage());
    }

    @ExceptionHandler(ExceptionApi500.class)
    public ResponseEntity<?> exApi500(ExceptionApi500 e) {
        log.error("지정된 서버 오류 발생: {}", e.getMessage());
        Sentry.captureException(e);
        return Resp.fail(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> exUnKnown(Exception e) {
        log.error("알 수 없는 오류 발생: {}", e.getMessage());
        log.error("스택 트레이스 시작");
        e.printStackTrace();
        log.error("스택 트레이스 끝");
        Sentry.captureException(e);
        return Resp.fail(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCodeEnum.INTERNAL_SERVER_ERROR.getMessage());
    }

    /**
     * @param e MethodArgumentTypeMismatchException 예외 객체
     * @return 클라이언트에게 전달할 에러 메시지를 담은 ResponseEntity
     * @RequestParam, @PathVariable 등에서 타입 변환 실패 시 발생하는 예외를 처리합니다.
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<?> handleTypeMismatch(MethodArgumentTypeMismatchException e) {
        // e.getName()을 통해 어떤 파라미터에서 오류가 났는지 알 수 있습니다. (예: "page", "year")
        // e.getValue()를 통해 클라이언트가 보낸 잘못된 값을 알 수 있습니다. (예: "abc")
        String errorMessage = String.format("'%s' 파라미터에 잘못된 값 '%s'이(가) 입력되었습니다. 유효한 타입을 입력해주세요.", e.getName(), e.getValue());

        // 직접 만드신 Resp 유틸리티를 사용하여 일관된 에러 응답을 보냅니다.
        return Resp.fail(HttpStatus.BAD_REQUEST, errorMessage);
    }

    // ConstraintViolationException 예외를 잡아서 처리하는 핸들러
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<?> handleConstraintViolationException(ConstraintViolationException e) {
        // 유효성 검사 실패 시 발생한 에러 메시지를 추출합니다.
        String errorMessage = e.getConstraintViolations().iterator().next().getMessage();

        // 직접 만드신 Resp 유틸리티를 사용하여 일관된 에러 응답을 보냅니다.
        return Resp.fail(HttpStatus.BAD_REQUEST, errorMessage);
    }
}