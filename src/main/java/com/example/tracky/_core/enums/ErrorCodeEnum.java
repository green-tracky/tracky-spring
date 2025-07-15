package com.example.tracky._core.enums;

/**
 * API 전반에서 사용하는 공통 에러 코드와 메시지를 정의한 Enum 클래스입니다.
 * <p>
 * HTTP 상태 코드와 사용자에게 보여줄 에러 메시지를 함께 관리하여
 * 일관된 에러 처리와 응답 메시지 표준화를 돕습니다.
 * </p>
 */
public enum ErrorCodeEnum {

    /**
     * 400 Bad Request - fileName 파라미터는 필수입니다.
     */
    MISSING_FILE_NAME_PARAMETER(400, "fileName 파라미터는 필수입니다."),

    /**
     * 400 Bad Request - 유효하지 않은 리워드 값입니다.
     */
    INVALID_INVITE_STATUS(400, "유효하지 않은 초대 상태입니다"),

    /**
     * 400 Bad Request - 유효하지 않은 리워드 값입니다.
     */
    INVALID_RUNBADGE_TYPE(400, "유효하지 않은 러닝뱃지 타입입니다"),

    /**
     * 400 Bad Request - 유효하지 않은 리워드 값입니다.
     */
    INVALID_REWARD_TYPE(400, "유효하지 않은 리워드 값입니다"),

    /**
     * 400 Bad Request - 유효하지 않은 장소 값입니다.
     */
    INVALID_RUN_PLACE_TYPE(400, "유효하지 않은 장소 값입니다"),

    /**
     * 400 Bad Request - 유효하지 않은 유저 타입 값입니다.
     */
    INVALID_USER_TYPE(400, "유효하지 않은 유저 타입입니다"),

    /**
     * 400 Bad Request - 유효하지 않은 성별 값입니다.
     */
    INVALID_GENDER(400, "유효하지 않은 성별 값입니다"),

    /**
     * 400 Bad Request - 유효하지 않은 챌린지 타입 값입니다.
     */
    INVALID_CHALLENGE_TYPE(400, "유효하지 않은 챌린지 타입입니다"),

    /**
     * 400 Bad Request - 이미 종료된 챌린지입니다.
     */
    CHALLENGE_ALREADY_ENDED(400, "이미 종료된 챌린지 입니다"),

    /**
     * 400 Bad Request - 본인에게 친구 요청을 보낼 수 없습니다.
     */
    INVALID_SELF_REQUEST(400, "본인에게 친구 요청을 보낼 수 없습니다"),

    /**
     * 400 Bad Request - 이미 친구 요청을 보냈습니다.
     */
    DUPLICATE_INVITE(400, "이미 요청을 보냈습니다."),

    /**
     * 400 Bad Request - 이미 응답된 요청입니다.
     */
    INVALID_INVITE_RESPONSE_STATE(400, "이미 응답된 요청입니다"),

    /**
     * 400 Bad Request - 잘못된 형식의 토큰으로 요청이 들어왔습니다.
     */
    INVALID_TOKEN_FORMAT(400, "잘못된 형식의 토큰으로 요청이 들어왔습니다"),


    /**
     * 401 Unauthorized - 로그인이 필요합니다.
     */
    LOGIN_REQUIRED(401, "로그인이 필요합니다"),

    /**
     * 401 Unauthorized - 관리자 권한이 필요합니다.
     */
    ADMIN_PRIVILEGE_REQUIRED(401, "관리자 권한이 필요합니다"),

    /**
     * 401 Unauthorized - 토큰이 만료되었습니다.
     */
    TOKEN_EXPIRED(401, "토큰이 만료되었습니다"),

    /**
     * 401 Bad Request - 유효하지 않은 토큰입니다.
     */
    INVALID_TOKEN(401, "유효하지 않는 토큰입니다"),

    /**
     * 401 Bad Request - Authorization 헤더에 'Bearer'가 누락되었습니다.
     */
    BEARER_PREFIX_MISSING(401, "Authorization 헤더에 'Bearer'가 누락되었습니다"),

    /**
     * 401 Unauthorized - 토큰이 존재하지 않습니다.
     */
    TOKEN_NOT_FOUND(401, "토큰이 존재하지 않습니다"),

    /**
     * 403 Forbidden - 해당 기능은 관리자만 접근할 수 있습니다.
     */
    ADMIN_ACCESS_ONLY(403, "해당 기능은 관리자만 접근할 수 있습니다"),

    /**
     * 403 Forbidden - 다른 사용자의 정보를 수정할 수 없습니다.
     */
    MODIFY_OTHER_USER_INFO_FORBIDDEN(403, "다른 사용자의 정보를 수정할 수 없습니다"),

    /**
     * 403 Forbidden - 접근 권한이 없습니다.
     */
    ACCESS_DENIED(403, "접근 권한이 없습니다."),

    /**
     * 404 Not Found - 해당 사용자를 찾을 수 없습니다.
     */
    RUN_LEVEL_NOT_FOUND(404, "해당 러닝 레벨을 찾을 수 없습니다"),

    /**
     * 404 Not Found - 해당 사용자를 찾을 수 없습니다.
     */
    USER_NOT_FOUND(404, "해당 사용자를 찾을 수 없습니다"),

    /**
     * 404 Not Found - 해당 게시글을 찾을 수 없습니다.
     */
    POST_NOT_FOUND(404, "해당 게시글을 찾을 수 없습니다"),

    /**
     * 404 Not Found - 해당 댓글을 찾을 수 없습니다.
     */
    COMMENT_NOT_FOUND(404, "해당 댓글을 찾을 수 없습니다"),

    /**
     * 404 Not Found - 해당 좋아요를 찾을 수 없습니다.
     */
    LIKE_NOT_FOUND(404, "해당 좋아요를 찾을 수 없습니다"),

    /**
     * 404 Not Found - 해당 러닝을 찾을 수 없습니다.
     */
    RUN_NOT_FOUND(404, "해당 러닝을 찾을 수 없습니다"),

    /**
     * 404 Not Found - 해당 챌린지를 찾을 수 없습니다.
     */
    CHALLENGE_NOT_FOUND(404, "해당 챌린지를 찾을 수 없습니다"),

    /**
     * 404 Not Found - 해당 챌린지에 참가하지 않았습니다.
     */
    CHALLENGE_JOIN_NOT_FOUND(404, "해당 챌린지에 참가하지 않았습니다"),

    /**
     * 404 Not Found - 해당 챌린지에 참가하지 않았습니다.
     */
    REWARD_MASTER_NOT_FOUND(404, "해당 챌린지 보상이 존재하지 않습니다"),

    /**
     * 404 Not Found - 관리자가 존재하지 않습니다.
     */
    ADMIN_NOT_FOUND(404, "관리자가 존재하지 않습니다"),

    /**
     * 404 Not Found - 관리자가 존재하지 않습니다.
     */
    NOT_MY_FRIEND(404, "서로 친구가 아닙니다"),

    /**
     * 500 Internal Server Error - 알 수 없는 오류 발생 시 기본 메시지입니다.
     */
    INTERNAL_SERVER_ERROR(500, "알 수 없는 오류가 발생했습니다. 관리자에게 문의해주세요"),

    /**
     * [신규 추가]
     * 500 Internal Server Error - 데이터베이스에 저장된 값이 코드와 일치하지 않을 때 발생합니다.
     * 예: DB의 뱃지 타입 '월간기록'을 Java Enum으로 변환하려 할 때, 해당 Enum 상수가 없는 경우.
     */
    INVALID_DATABASE_DATA(500, "서버 데이터에 문제가 발생했습니다. 관리자에게 문의해주세요.");

    /**
     * HTTP 상태 코드
     */
    private final int status;

    /**
     * 클라이언트에 보여줄 에러 메시지
     */
    private final String message;

    /**
     * 생성자 - 상태 코드와 메시지를 설정합니다.
     *
     * @param status  HTTP 상태 코드
     * @param message 사용자에게 전달할 에러 메시지
     */
    ErrorCodeEnum(int status, String message) {
        this.status = status;
        this.message = message;
    }

    /**
     * HTTP 상태 코드를 반환합니다.
     *
     * @return HTTP 상태 코드 (예: 400, 401, 404 등)
     */
    public int getStatus() {
        return status;
    }

    /**
     * 에러 메시지를 반환합니다.
     *
     * @return 클라이언트에 보여줄 에러 메시지
     */
    public String getMessage() {
        return message;
    }
}
