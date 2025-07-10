package com.example.tracky.integration;

import com.example.tracky.MyRestDoc;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK) // MOCK -> 가짜 환경을 만들어 필요한 의존관계를 다 메모리에 올려서 테스트
@Slf4j
class UserControllerTest extends MyRestDoc {

    @Autowired
    private ObjectMapper om;

    @Test
    @DisplayName("카카오 로그인")
    void kakao_login_test() throws Exception {
        // given
        String idToken = "eyJraWQiOiI5ZjI1MmRhZGQ1ZjIzM2Y5M2QyZmE1MjhkMTJmZWEiLCJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJhdWQiOiI1NmI0YjNmYjY1ZmRjNDI3Y2Y4OTQ3ZmZhNDg2NjhjZSIsInN1YiI6IjQzMjA0MDI5NjEiLCJhdXRoX3RpbWUiOjE3NTIxMDg3NjEsImlzcyI6Imh0dHBzOi8va2F1dGgua2FrYW8uY29tIiwibmlja25hbWUiOiLstZzsnqzsm5AiLCJleHAiOjE3NTIxNTE5NjEsImlhdCI6MTc1MjEwODc2MSwicGljdHVyZSI6Imh0dHA6Ly9pbWcxLmtha2FvY2RuLm5ldC90aHVtYi9SMTEweDExMC5xNzAvP2ZuYW1lPWh0dHAlM0ElMkYlMkZ0MS5rYWthb2Nkbi5uZXQlMkZhY2NvdW50X2ltYWdlcyUyRmRlZmF1bHRfcHJvZmlsZS5qcGVnIn0.cO_UFNhM9GhC3MAMFewWO9G2gFYGKIlz1-CzaVn-4yp5i-GDPY04YaPoqfEDAPgWWmLtDjB8EbKE5GiTNonjTph-DwckDliH_1cJ4UXTRJFiwnTdXXAyuWgT0TcD-D6w0DM2EwFnqcv6xevP5B1IV_RIOUaGTtfd24xnEC08ek4gRJuYmfo8KOWA2tuAbHccWFg47ojvJVNbvFJoSz7PjQEvPcQImXzsmJg3dT6hAA5roSvIJLR-F9saDa7St51SDoK3Z1gC7-fk36HvStpdoFuKodp5F4fcNjumQVewbXxEa94j3dtP82RmvCZBVtZNoRC-Dz0VQ8u5iwGvp1clzA";

        // when
        ResultActions actions = mvc.perform(
                MockMvcRequestBuilders
                        .post("/api/oauth/login")
                        .contentType(MediaType.TEXT_PLAIN)
                        .content(idToken)
        );

        // eye
        String responseBody = actions.andReturn().getResponse().getContentAsString();
        log.debug("✅응답 바디: " + responseBody);

        // then

        // 디버깅 및 문서화 (필요시 주석 해제)
        // actions.andDo(MockMvcResultHandlers.print()).andDo(document);

    }
}
