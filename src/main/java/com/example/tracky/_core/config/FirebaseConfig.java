package com.example.tracky._core.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * 이 클래스는 Spring Boot 애플리케이션에서 Firebase Admin SDK를 초기화하고
 * FirebaseMessaging 인스턴스를 Bean으로 등록하는 설정을 담당합니다.
 * application.properties에 등록된 개별 키 값을 조합하여 유연하게 동작합니다.
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class FirebaseConfig {

    // 1. application.properties의 firebase 속성을 담고 있는 객체를 주입받습니다.
    private final FirebaseProperties firebaseProperties;
    // 2. 자바 객체를 JSON 문자열로 변환하기 위해 Spring이 관리하는 ObjectMapper를 주입받습니다.
    private final ObjectMapper objectMapper;

    /**
     * application.properties의 속성 값을 사용하여 FirebaseApp을 초기화하고 Bean으로 등록하는 메소드.
     *
     * @return 초기화된 FirebaseApp 인스턴스
     * @throws IOException 설정 파일 로드 중 발생할 수 있는 예외
     */
    @Bean
    public FirebaseApp firebaseApp() throws IOException {
        // 3. [핵심] 주입받은 FirebaseProperties 객체를 완전한 JSON 문자열로 다시 만듭니다.
        //    properties 파일에서 private_key의 줄바꿈 문자(\n)가 일반 텍스트로 인식되므로, 실제 줄바꿈 문자로 변경해줍니다.
        firebaseProperties.setPrivateKey(firebaseProperties.getPrivateKey().replace("\\n", "\n"));
        String json = objectMapper.writeValueAsString(firebaseProperties);

        log.debug("application.properties 파일에서 Firebase 설정을 로드합니다.");

        // 4. 동적으로 생성된 JSON 문자열로부터 InputStream을 생성합니다.
        InputStream serviceAccountStream = new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8));

        try (InputStream serviceAccount = serviceAccountStream) {
            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();

            // 5. 앱이 이미 초기화되었는지 확인하여 중복 초기화를 방지합니다.
            if (FirebaseApp.getApps().isEmpty()) {
                return FirebaseApp.initializeApp(options);
            } else {
                return FirebaseApp.getInstance();
            }
        }
    }

    /**
     * FirebaseMessaging 인스턴스를 Bean으로 등록하는 메소드.
     *
     * @param firebaseApp 위에서 초기화되고 등록된 FirebaseApp Bean을 주입받습니다.
     * @return FirebaseMessaging 인스턴스
     */
    @Bean
    public FirebaseMessaging firebaseMessaging(FirebaseApp firebaseApp) {
        return FirebaseMessaging.getInstance(firebaseApp);
    }
}
