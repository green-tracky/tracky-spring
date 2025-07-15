package com.example.tracky.s3;

import com.example.tracky._core.enums.ErrorCodeEnum;
import com.example.tracky._core.error.ex.ExceptionApi400;
import com.example.tracky._core.utils.Resp;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class S3Controller {

    private final S3Service s3Service;

    /**
     * S3 Pre-signed URL 생성 요청을 처리하는 API 엔드포인트입니다.
     *
     * @param fileName 클라이언트가 업로드할 파일의 원본 이름
     * @return 생성된 Pre-signed URL을 포함한 응답 객체
     */
    @GetMapping("/api/s3/presigned-url")
    public ResponseEntity<?> getPresignedUrl(@RequestParam String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            throw new ExceptionApi400(ErrorCodeEnum.MISSING_FILE_NAME_PARAMETER);
        }

        String presignedUrl = s3Service.generatePresignedUrl(fileName);
        return Resp.ok(presignedUrl);
    }
}
