package com.example.tracky.user.kakaojwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.util.Base64URL;
import com.nimbusds.jwt.SignedJWT;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.client.RestTemplate;

import java.math.BigInteger;
import java.util.Base64;

@Slf4j
public class RSAUtil {

    /**
     * 카카오 RSA 공개키 다운로드
     *
     * @return
     */
    public static JwtKeySet downloadRSAKey() {
        String jwtUrl = "https://kauth.kakao.com/.well-known/jwks.json";

        RestTemplate restTemplate = new RestTemplate();
        JwtKeySet keySet = restTemplate.getForObject(jwtUrl, JwtKeySet.class);

        return keySet;
    }

    /**
     * idToken 검증해서 페이로드를 저장
     *
     * @param idToken
     * @return
     */
    public static OAuthProfile verify(String idToken) {
        // 공개키가 있는지 없는지 확인해야함
        JwtKeySet keySet = downloadRSAKey();

        String n = keySet.getKeys().get(1).getN();
        String e = keySet.getKeys().get(1).getE();
        log.debug("n : " + n);
        log.debug("e : " + e);

        BigInteger bin = new BigInteger(1, Base64.getUrlDecoder().decode(n));
        BigInteger bie = new BigInteger(1, Base64.getUrlDecoder().decode(e));

        RSAKey rsaKey = new RSAKey.Builder(Base64URL.encode(bin), Base64URL.encode(bie)).build();
        try {
            // 1. 파싱
            SignedJWT signedJWT = SignedJWT.parse(idToken);

            // 2. 검증
            RSASSAVerifier verifier = new RSASSAVerifier(rsaKey.toRSAPublicKey());

            if (signedJWT.verify(verifier)) {
                log.debug("ID Token을 검증하였습니다");
                String payload = signedJWT.getPayload().toString();

                log.debug("페이로드 : " + payload);
                ObjectMapper objectMapper = new ObjectMapper();
                OAuthProfile profile = objectMapper.readValue(payload, OAuthProfile.class);
                return profile;

            } else {
                // TODO : 예외처리 하기
                throw new RuntimeException("id토큰 검증 실패");
            }
        } catch (Exception ex) { // 공개키가 카카오에서 변경됬을 때
            // 다시 다운 받고 처리를 다시 해야함
            throw new RuntimeException(ex.getMessage());
        }
    }
}