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
import java.util.Base64;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class FirebaseConfig {

    private final FirebaseProperties firebaseProperties;
    private final ObjectMapper objectMapper;

    @Bean
    public FirebaseApp firebaseApp() throws IOException {
        // 1. 환경 변수에서 '깨끗한 Base64' 키를 가져옵니다.
        String base64EncodedKey = firebaseProperties.getPrivateKey();

        // 2. Base64 디코딩만 수행합니다.
        byte[] decodedBytes = Base64.getDecoder().decode(base64EncodedKey);

        // 이유 디코딩 하면 \n 이라는 문자를 \n(이스케이프 문자)로 변환하는 replace가 필요하다
        String originalPemKey = new String(decodedBytes, StandardCharsets.UTF_8).replace("\\n", "\n");

        // 3. 디코딩된 키를 다시 설정합니다.
        firebaseProperties.setPrivateKey(originalPemKey);

        // 4. JSON으로 변환하여 초기화합니다.
        String json = objectMapper.writeValueAsString(firebaseProperties);
        InputStream serviceAccountStream = new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8));

        try (InputStream serviceAccount = serviceAccountStream) {
            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();

            if (FirebaseApp.getApps().isEmpty()) {
                return FirebaseApp.initializeApp(options);
            } else {
                return FirebaseApp.getInstance();
            }
        }
    }

    @Bean
    public FirebaseMessaging firebaseMessaging(FirebaseApp firebaseApp) {
        return FirebaseMessaging.getInstance(firebaseApp);
    }
}
