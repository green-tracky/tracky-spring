package com.example.tracky._core.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * ✅ 공통 응답 객체 (Generic)
 *
 * <p>
 * 컨트롤러에서 일관된 응답 포맷을 제공하기 위한 클래스입니다.
 * status, msg, data 세 가지 필드를 포함하며,
 * 성공과 실패 응답을 쉽게 만들 수 있는 정적 메서드를 제공합니다.
 * </p>
 *
 * @param <T> 응답 데이터의 타입
 */
@AllArgsConstructor
@Data
public class Resp<T> {
    /**
     * HTTP 상태 코드 (예: 200, 400 등)
     */
    private Integer status;

    /**
     * 응답 메시지 (예: "성공", "잘못된 요청" 등)
     */
    private String msg;

    /**
     * 응답 데이터 본문
     */
    private T data;

    /**
     * 기본 생성자를 private으로 막아 외부에서 사용하지 못하도록 함
     */
    private Resp() {
    }

    /**
     * ✅ 성공 응답을 생성하고 ResponseEntity로 감싸서 반환
     *
     * @param <B>  응답 데이터의 타입
     * @param data 실제 응답 데이터
     * @return ResponseEntity로 감싼 Resp 객체 (HTTP 200 OK)
     */
    public static <B> ResponseEntity<Resp<B>> ok(B data) {
        Resp<B> resp = new Resp<>(HttpStatus.OK.value(), "성공", data);
        return new ResponseEntity<>(resp, HttpStatus.OK);
    }

    /**
     * ❌ 실패 응답을 생성하고 ResponseEntity로 감싸서 반환
     *
     * @param status 실패 HTTP 상태 (예: 400, 404, 500 등) HttpStatus Enum 사용
     * @param msg    에러 메시지
     * @return ResponseEntity로 감싼 Resp 객체
     */
    public static ResponseEntity<Resp<?>> fail(HttpStatus status, String msg) {
        Resp<?> resp = new Resp<>(status.value(), msg, null);
        return new ResponseEntity<>(resp, status);
    }

    /**
     * ❌ ResponseEntity를 사용하지 않는 상황에서 실패 응답을 직접 생성
     *
     * @param status 실패 상태 코드 (예: 400)
     * @param msg    에러 메시지
     * @return Resp 객체
     */
    public static Resp<?> fail(Integer status, String msg) {
        Resp<?> resp = new Resp<>(status, msg, null);
        return resp;
    }
}
