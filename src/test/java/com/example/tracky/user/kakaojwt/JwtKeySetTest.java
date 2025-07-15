package com.example.tracky.user.kakaojwt;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.RestTemplate;

@SpringBootTest
@Slf4j
public class JwtKeySetTest {

    @Autowired
    private RSAUtil rsaUtil;

    /**
     * 공개키 다운받고 객체에 저장한 뒤 출력
     */
    @Test
    public void publicKeyDownload() {
        String jwtUrl = "https://kauth.kakao.com/.well-known/jwks.json";

        RestTemplate restTemplate = new RestTemplate();
        JwtKeySet keySet = restTemplate.getForObject(jwtUrl, JwtKeySet.class);

        assert keySet != null;
        for (JwtKeySet.JwtKey key : keySet.getKeys()) {
            log.debug(key.toString());
        }
    }

    /**
     * 플러터에서 카카오에 로그인 요청해서 받은 idToken으로 검증 테스트
     */
//    @Test
//    public void rsaVerify_test() {
//        // idToken 값을 직접 변경해야함
//        String idToken = Constants.idTokenTest;
//        rsaUtil.verify(idToken);
//    }

    /**
     * RSAUtil 클래스의 검증 메서드의 결과로 나온 페이로드를 OAuthProfile 에 맵핑
     *
     * @throws JsonProcessingException
     */
    @Test
    public void payloadParse() throws JsonProcessingException {
        String payload = """
                {"aud":"56b4b3fb65fdc427cf8947ffa48668ce","sub":"4320402961","auth_time":1752042910,"iss":"https://kauth.kakao.com","nickname":"최재원","exp":1752086110,"iat":1752042910,"picture":"http://img1.kakaocdn.net/thumb/R110x110.q70/?fname=http%3A%2F%2Ft1.kakaocdn.net%2Faccount_images%2Fdefault_profile.jpeg"}
                """;
        ObjectMapper objectMapper = new ObjectMapper();
        OAuthProfile profile = objectMapper.readValue(payload, OAuthProfile.class);
        log.debug(profile.toString());
    }
}
