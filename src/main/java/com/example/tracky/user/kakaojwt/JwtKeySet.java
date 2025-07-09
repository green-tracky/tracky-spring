package com.example.tracky.user.kakaojwt;

import lombok.Data;

import java.util.List;

/**
 * 카카오 공개키 저장소
 */
@Data
public class JwtKeySet {
    private List<JwtKey> keys;

    @Data
    public static class JwtKey {
        private String kid;
        private String kty;
        private String alg;
        private String use;
        private String n;
        private String e;
    }

}