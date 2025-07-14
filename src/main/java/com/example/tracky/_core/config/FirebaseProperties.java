package com.example.tracky._core.config;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "firebase") // "firebase"로 시작하는 속성을 이 클래스에 매핑
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class FirebaseProperties {

    // application.properties의 속성 이름과 필드 이름이 자동으로 매핑됩니다.
    // (예: "project-id" -> projectId)
    private String type;
    private String projectId;
    private String privateKeyId;
    private String privateKey; // privateKey는 \n이 포함된 채로 들어옵니다.
    private String clientEmail;
    private String clientId;
    private String authUri;
    private String tokenUri;
    private String authProviderX509CertUrl;
    private String clientX509CertUrl;
    private String universeDomain;
}
