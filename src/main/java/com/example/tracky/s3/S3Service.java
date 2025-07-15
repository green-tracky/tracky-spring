package com.example.tracky.s3;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.example.tracky._core.error.ex.ExceptionApi400;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URL;
import java.util.Date;
import java.util.UUID;

@Slf4j
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

    /**
     * S3에 저장된 파일의 전체 URL을 받아 파일을 삭제합니다. (수정됨)
     *
     * @param fileUrl 데이터베이스에 저장된 전체 파일 URL
     */
    public void deleteFileByUrl(String fileUrl) {
        // 1. 방어 로직: URL이 null이거나 비정상적인 경우, 로그만 남기고 작업을 조용히 종료합니다.
        if (fileUrl == null || fileUrl.isEmpty() || !fileUrl.startsWith("https://")) {
            log.warn("유효하지 않거나 비어있는 파일 URL에 대한 삭제 시도가 있었습니다: {}", fileUrl);
            return; // 예외를 발생시키지 않고 그냥 넘어갑니다.
        }

        // 2. URL에서 파일 키(파일 이름)를 추출합니다.
        String fileKey = extractFileKeyFromUrl(fileUrl);

        // 3. 파일 키가 정상적으로 추출된 경우에만 삭제를 시도합니다.
        //    (fileKey가 null이면, 우리 버킷의 URL이 아니라는 뜻이므로 삭제하지 않습니다.)
        if (fileKey != null) {
            amazonS3.deleteObject(bucket, fileKey);
        }
    }

    /**
     * 전체 S3 URL에서 파일 키를 추출하는 헬퍼 메서드입니다. (수정됨)
     *
     * @param fileUrl 전체 파일 URL
     * @return 정상적인 경우 파일 키, 비정상적인 경우 null
     */
    private String extractFileKeyFromUrl(String fileUrl) {
        String bucketUrl = "https://" + bucket + ".s3." + amazonS3.getRegionName() + ".amazonaws.com/";

        // 1. 방어 로직: 전달된 fileUrl이 우리 버킷의 URL 형식으로 시작하는지 먼저 확인합니다.
        if (!fileUrl.startsWith(bucketUrl)) {
            // 형식이 다르다면, 경고 로그를 남기고 null을 반환하여 잘못된 삭제를 막습니다.
            log.warn("우리 버킷의 URL 형식이 아닙니다. 삭제를 건너뜁니다: {}", fileUrl);
            return null;
        }

        // 2. 형식이 올바른 경우에만 안전하게 파일 키를 추출합니다.
        return fileUrl.substring(bucketUrl.length());
    }
}
