package com.example.tracky.user.kakaojwt;

import lombok.Data;

import java.util.List;

/**
 * 카카오의 JWKS(JSON Web Key Set) 엔드포인트에서 반환하는 공개키 목록을 담기 위한 DTO(Data Transfer Object) 클래스입니다.
 * 이 구조는 RFC 7517 표준을 따릅니다.
 * 예: "https://kauth.kakao.com/.well-known/jwks.json"의 JSON 응답을 이 객체로 변환합니다.
 */
@Data
public class JwtKeySet {

    /**
     * 공개키 목록을 저장하는 리스트입니다.
     * JSON 응답의 "keys" 필드에 해당합니다.
     */
    private List<JwtKey> keys;

    /**
     * JWKS에 포함된 개별 공개키(JSON Web Key)의 정보를 담는 중첩 클래스입니다.
     */
    @Data
    public static class JwtKey {

        /**
         * Key ID (키 식별자).
         * 여러 개의 키가 있을 때 특정 키를 고유하게 식별하는 값입니다.
         * JWT의 헤더(header)에 있는 'kid' 값과 일치하는 키를 찾기 위해 사용됩니다.
         */
        private String kid;

        /**
         * Key Type (키 유형).
         * 키가 어떤 암호화 알고리즘 계열에 속하는지 나타냅니다. (예: "RSA")
         */
        private String kty;

        /**
         * Algorithm (알고리즘).
         * 이 키를 사용하여 서명하거나 암호화할 때 사용하는 특정 알고리즘을 나타냅니다. (예: "RS256")
         */
        private String alg;

        /**
         * Public Key Use (공개키 용도).
         * 이 키의 용도를 정의합니다. (예: "sig"는 서명 검증용임을 의미)
         */
        private String use;

        /**
         * Modulus (모듈러스).
         * RSA 공개키의 구성 요소 중 하나입니다. Base64URL로 인코딩되어 있습니다.
         */
        private String n;

        /**
         * Exponent (지수).
         * RSA 공개키의 공개 지수(public exponent) 값입니다. Base64URL로 인코딩되어 있습니다.
         */
        private String e;
    }
}
