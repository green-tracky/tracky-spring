package com.example.tracky._core.image;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * 물리적 이미지 파일의 저장 및 관리를 담당하는 서비스입니다.
 * 이 서비스는 특정 도메인에 종속되지 않으며, 파일 I/O 작업에만 집중합니다.
 */
@Service
public class ImageService {

    // application.properties에 설정된 파일 저장소의 루트 경로를 주입받습니다.
    @Value("${file.storage.location}")
    private String storageLocation;

    /**
     * Base64로 인코딩된 이미지 문자열을 받아 물리적 파일로 저장합니다.
     *
     * @param base64Image "data:image/jpeg;base64,..." 형태의 이미지 문자열
     * @param userTag     파일 경로 생성을 위한 유저 태그 (유니크 값)
     * @param runId       파일 경로 생성을 위한 달리기 기록 ID
     * @return 데이터베이스에 저장될 상대 파일 경로 (예: "img/uuid_tag/uuid_runId/uuid.jpeg")
     * @throws IOException 파일 저장 중 오류 발생 시
     */
    public String save(String base64Image, String userTag, Long runId) throws IOException {
        // 1. Base64 데이터 파싱
        // "data:image/jpeg;base64," 접두사를 제거하고 순수 Base64 데이터만 추출합니다.
        String[] parts = base64Image.split(",");
        String imageString = parts.length > 1 ? parts[1] : parts[0];

        // MIME 타입을 통해 파일 확장자를 추출합니다. (예: "jpeg", "png")
        String mimeType = parts[0].substring(parts[0].indexOf(":") + 1, parts[0].indexOf(";"));
        String extension = mimeType.split("/")[1];

        // Base64 문자열을 바이트 배열로 디코딩합니다.
        byte[] imageBytes = Base64.getDecoder().decode(imageString);

        // 2. 폴더 및 파일명 생성 (UUID를 사용하여 충돌 방지)
        String userFolderName = UUID.randomUUID().toString() + "_" + userTag;
        String runFolderName = UUID.randomUUID().toString() + "_" + runId;
        String randomFileName = UUID.randomUUID().toString() + "." + extension;

        // 3. 전체 저장 경로 계산
        Path finalFolderPath = Paths.get(storageLocation, "img", userFolderName, runFolderName);
        Path finalFilePath = finalFolderPath.resolve(randomFileName);

        // 4. 폴더가 존재하지 않으면 생성합니다.
        Files.createDirectories(finalFolderPath);

        // 5. 파일을 디스크에 씁니다.
        Files.write(finalFilePath, imageBytes);

        // 6. 데이터베이스에 저장할 상대 경로를 생성하여 반환합니다.
        // OS 호환성을 위해 경로 구분자를 '/'로 통일합니다.
        Path dbPath = Paths.get("img", userFolderName, runFolderName, randomFileName);
        return dbPath.toString().replace("\\", "/");
    }
}
