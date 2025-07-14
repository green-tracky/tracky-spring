package com.example.tracky._core.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * 이 클래스는 Spring Boot 애플리케이션에서 Firebase Admin SDK를 초기화하고
 * FirebaseMessaging 인스턴스를 Bean으로 등록하는 설정을 담당합니다.
 * 운영 환경에서는 환경 변수를, 개발 환경에서는 로컬 파일을 사용하여 유연하게 동작합니다.
 */
@Configuration
public class FirebaseConfig {

    /**
     * 환경 변수 또는 로컬 파일을 사용하여 FirebaseApp을 초기화하고 Bean으로 등록하는 메소드.
     *
     * @return 초기화된 FirebaseApp 인스턴스
     * @throws IOException 설정 파일 로드 중 발생할 수 있는 예외
     */
    @Bean
    public FirebaseApp firebaseApp() throws IOException {
        // 1. 먼저 "FIREBASE_CONFIG_JSON" 이라는 이름의 환경 변수를 읽어옵니다.
        String firebaseConfigJson = System.getenv("FIREBASE_CONFIG_JSON");
        InputStream serviceAccountStream;

        // 2. 환경 변수가 존재하고 비어있지 않은 경우 (운영 환경)
        if (firebaseConfigJson != null && !firebaseConfigJson.isEmpty()) {
            System.out.println("운영 환경: 환경 변수에서 Firebase 설정을 로드합니다.");
            // 환경 변수의 JSON 문자열을 InputStream으로 변환합니다.
            serviceAccountStream = new ByteArrayInputStream(firebaseConfigJson.getBytes(StandardCharsets.UTF_8));
        }
        // 3. 환경 변수가 없는 경우 (개발 환경)
        else {
            System.out.println("개발 환경: 로컬 파일에서 Firebase 설정을 로드합니다.");
            // 기존과 동일하게 클래스패스에서 로컬 파일을 읽어옵니다.
            ClassPathResource resource = new ClassPathResource("env/fb-service-account.json");
            serviceAccountStream = resource.getInputStream();
        }

        try (InputStream serviceAccount = serviceAccountStream) {
            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();

            // 4. 앱이 이미 초기화되었는지 확인하여 중복 초기화를 방지합니다.
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
