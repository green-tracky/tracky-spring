package com.example.tracky.s3;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.example.tracky._core.error.ex.ExceptionApi400;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URL;
import java.util.Date;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class S3Service {

    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    /**
     * 파일 업로드를 위한 Pre-signed URL을 생성합니다.
     *
     * @param originalFileName 원본 파일 이름
     * @return 생성된 Pre-signed URL 문자열
     */
    public String generatePresignedUrl(String originalFileName) {
        // S3에 저장될 파일 이름 (중복을 피하기 위해 UUID 사용)
        String storedFileName = createStoredFileName(originalFileName);

        // Pre-signed URL 만료 시간 설정 (예: 10분)
        Date expiration = new Date();
        long expTimeMillis = expiration.getTime();
        expTimeMillis += 1000 * 60 * 10; // 10분
        expiration.setTime(expTimeMillis);

        // Pre-signed URL 생성 요청 객체 생성
        GeneratePresignedUrlRequest generatePresignedUrlRequest =
                new GeneratePresignedUrlRequest(bucket, storedFileName)
                        .withMethod(HttpMethod.PUT) // HTTP PUT 메서드로 업로드 허용
                        .withExpiration(expiration);

        // URL 생성
        URL url = amazonS3.generatePresignedUrl(generatePresignedUrlRequest);
        return url.toString();
    }

    /**
     * S3에 저장될 파일 이름을 생성합니다. (원본 파일명 + UUID)
     *
     * @param originalFileName 원본 파일 이름
     * @return S3에 저장될 고유한 파일 이름
     */
    private String createStoredFileName(String originalFileName) {
        String uuid = UUID.randomUUID().toString();
        String extension = extractExtension(originalFileName);
        return uuid + "." + extension;
    }

    /**
     * 파일 이름에서 확장자를 추출합니다.
     *
     * @param originalFileName 원본 파일 이름
     * @return 파일 확장자
     */
    private String extractExtension(String originalFileName) {
        try {
            return originalFileName.substring(originalFileName.lastIndexOf("."));
        } catch (StringIndexOutOfBoundsException e) {
            // 확장자가 없는 경우 예외 처리
            throw new ExceptionApi400("파일에 확장자가 없습니다: " + originalFileName);
        }
    }
}
