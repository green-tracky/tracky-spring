package com.example.tracky._core.config;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class S3Config {

    // application.properties 파일에서 설정한 값들을 주입받습니다.
    @Value("${cloud.aws.credentials.access-key}")
    private String accessKey;

    @Value("${cloud.aws.credentials.secret-key}")
    private String secretKey;

    @Value("${cloud.aws.region.static}")
    private String region;

    /**
     * AWS 자격 증명 정보를 담는 객체를 생성합니다.
     *
     * @return AWSCredentials
     */
    private AWSCredentials awsCredentials() {
        return new BasicAWSCredentials(accessKey, secretKey);
    }

    /**
     * AmazonS3 클라이언트 객체를 Spring Bean으로 등록합니다.
     * 이 클라이언트는 S3 버킷과의 모든 상호작용(파일 업로드, URL 생성 등)을 담당합니다.
     *
     * @return AmazonS3
     */
    @Bean
    public AmazonS3 amazonS3() {
        return AmazonS3ClientBuilder.standard()
                .withRegion(region) // 사용할 AWS 리전을 설정합니다.
                .withCredentials(new AWSStaticCredentialsProvider(awsCredentials())) // AWS 자격 증명 정보를 제공합니다.
                .build();
    }
}
