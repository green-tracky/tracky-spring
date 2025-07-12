package com.example.tracky.user.kakaojwt;

import com.example.tracky._core.enums.ErrorCodeEnum;
import com.example.tracky._core.error.ex.ExceptionApi400;
import com.example.tracky._core.error.ex.ExceptionApi401;
import com.example.tracky._core.error.ex.ExceptionApi500;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.util.Base64URL;
import com.nimbusds.jwt.SignedJWT;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.math.BigInteger;
import java.text.ParseException;
import java.time.Instant;
import java.util.Base64;

@Slf4j // Lombok을 통해 로거(log) 객체를 자동으로 생성
@Component // 스프링이 관리하는 클래스
public class RSAUtil {

    // 캐시된 공개키 저장소
    private JwtKeySet cachedKeySet = null;

    // 마지막으로 공개키를 가져온 시간 (epoch seconds)
    private long lastFetchedTime = 0L;

    // 캐시 만료 시간 (예: 1시간 = 3600초)
    private static final long CACHE_EXPIRATION_TIME = 3600L;

    /**
     * 카카오의 OIDC 공개키 목록을 캐싱하여 반환
     * 캐시가 만료되었거나 없으면 네트워크에서 다시 다운로드
     *
     * @return JwtKeySet - 카카오 공개키 목록
     */
    private JwtKeySet getCachedRSAKeySet() {
        long now = Instant.now().getEpochSecond();

        // 캐시가 없거나 만료되었으면 다시 다운로드
        if (cachedKeySet == null || (now - lastFetchedTime) > CACHE_EXPIRATION_TIME) {
            log.debug("공개키 캐시가 없거나 만료되어 새로운 공개키를 다운로드합니다.");
            cachedKeySet = downloadRSAKey();
            lastFetchedTime = now;
        } else {
            log.debug("캐시된 공개키를 사용합니다.");
        }

        return cachedKeySet;
    }

    /**
     * 공개키 캐시를 강제로 무효화
     */
    private void invalidateCache() {
        log.warn("공개키 캐시를 강제로 무효화합니다.");
        cachedKeySet = null;
        lastFetchedTime = 0L;
    }

    /**
     * 서명 검증기를 생성한다
     *
     * @param keySet
     * @return
     * @throws JOSEException
     */
    private RSASSAVerifier createRSAVerifier(JwtKeySet keySet) throws JOSEException {
        // 2. ID 토큰을 서명한 공개키를 JWKS에서 찾음
        String n = keySet.getKeys().get(1).getN(); // RSA 공개키의 모듈러스(Modulus)
        String e = keySet.getKeys().get(1).getE(); // RSA 공개키의 공개 지수(Public Exponent)

        log.debug("n : " + n); // 디버깅: 모듈러스 값 로깅
        log.debug("e : " + e); // 디버깅: 공개 지수 값 로깅

        // 3. n, e 값을 사용하여 RSA 공개키 객체를 생성
        //    n, e 값은 Base64URL로 인코딩되어 있으므로, 디코딩하여 BigInteger로 변환
        BigInteger bin = new BigInteger(1, Base64.getUrlDecoder().decode(n));
        BigInteger bie = new BigInteger(1, Base64.getUrlDecoder().decode(e));

        // nimbusds 라이브러리의 RSAKey 빌더를 사용하여 공개키 객체 생성
        RSAKey rsaKey = new RSAKey.Builder(Base64URL.encode(bin), Base64URL.encode(bie)).build();

        return new RSASSAVerifier(rsaKey.toRSAPublicKey());
    }


    /**
     * 카카오의 OIDC(OpenID Connect) 공개키 목록을 다운로드
     * 이 공개키들은 카카오가 서명한 ID 토큰의 진위 여부를 검증하는 데 사용
     *
     * @return JwtKeySet - 카카오의 공개키 목록을 담은 객체
     */
    private JwtKeySet downloadRSAKey() {
        // 카카오 OIDC의 공개키 목록을 제공하는 표준 URL(JWKS URI)
        String jwtUrl = "https://kauth.kakao.com/.well-known/jwks.json";

        // 외부 API(URL)에 HTTP 요청을 보내기 위한 Spring의 RestTemplate 객체 생성
        RestTemplate restTemplate = new RestTemplate();

        // HTTP GET 요청을 보내고, 응답으로 받은 JSON 데이터를 JwtKeySet 클래스 객체로 변환하여 반환
        // RestTemplate이 내부적으로 Jackson 라이브러리를 사용하여 JSON을 객체로 매핑
        JwtKeySet keySet = restTemplate.getForObject(jwtUrl, JwtKeySet.class);

        return keySet;
    }

    /**
     * 카카오로부터 받은 ID 토큰(JWT)의 서명을 검증하고,
     * 토큰에 담긴 사용자 정보(Payload)를 추출
     *
     * @param idToken - 카카오에서 발급한 ID 토큰 문자열
     * @return OAuthProfile - 토큰의 페이로드(사용자 정보)를 담은 객체
     */
    public OAuthProfile verify(String idToken) {
        // 1. 카카오의 공개키 목록을 다운로드합니다.
        // 캐시된 공개키를 가져옴
        JwtKeySet keySet = getCachedRSAKeySet();

        try {
            // 4. ID 토큰 파싱
            //    문자열 형태의 ID 토큰을 SignedJWT 객체로 파싱하여 헤더, 페이로드, 서명 부분을 분리. base64 디코딩
            SignedJWT signedJWT = SignedJWT.parse(idToken);

            // 5. 서명 검증 준비
            //    생성한 RSA 공개키를 이용하여 서명 검증기(RSASSAVerifier)를 생성
            RSASSAVerifier verifier = createRSAVerifier(keySet);

            // 6. 서명 검증 실행
            //    ID 토큰의 서명이 우리가 가진 공개키와 일치하는지(유효한지) 확인합니다. 시그니처 검증
            boolean isVerified = signedJWT.verify(verifier);
            if (!isVerified) {
                log.warn("ID 토큰 1차 검증 실패. 공개키를 갱신하고 재시도합니다.");
                invalidateCache(); // 캐시 비우기
                keySet = getCachedRSAKeySet(); // 새로운 공개키 다운로드
                verifier = createRSAVerifier(keySet);
                isVerified = signedJWT.verify(verifier);
            }

            if (isVerified) {
                log.debug("ID Token을 성공적으로 검증하였습니다.");

                // 7. 페이로드(Payload) 추출
                //    검증이 성공하면, 토큰의 페이로드 부분을 문자열(JSON 형식)로 가져옴
                String payload = signedJWT.getPayload().toString();
                log.debug("페이로드 : " + payload);

                // 8. 페이로드를 객체로 변환
                //    Jackson의 ObjectMapper를 사용하여 JSON 문자열을 OAuthProfile 객체로 변환
                ObjectMapper objectMapper = new ObjectMapper();
                OAuthProfile profile = objectMapper.readValue(payload, OAuthProfile.class);
                return profile; // 사용자 정보가 담긴 객체를 반환

            } else {
                // 서명 검증에 실패한 경우, 예외를 발생
                throw new ExceptionApi401(ErrorCodeEnum.INVALID_TOKEN);
            }
        } catch (ExceptionApi401 e) {
            throw e; // 그대로 다시 던져서 401 응답 유도

            // 2. 토큰 형식이 잘못된 경우 (400 Bad Request)
        } catch (ParseException | JsonProcessingException e) {
            // ParseException: JWT 토큰 문자열 자체가 잘못된 형식일 때 발생
            // JsonProcessingException: payload의 JSON 구조가 OAuthProfile 객체와 맞지 않을 때 발생
            throw new ExceptionApi400(ErrorCodeEnum.INVALID_TOKEN_FORMAT);

            // 3. 그 외 모든 예상치 못한 서버 오류 (500 Internal Server Error)
        } catch (Exception ex) {
            log.error("ID 토큰 처리 중 예외 발생: {}", ex.getMessage(), ex);
            throw new ExceptionApi500(ErrorCodeEnum.INTERNAL_SERVER_ERROR);
        }
    }
}
