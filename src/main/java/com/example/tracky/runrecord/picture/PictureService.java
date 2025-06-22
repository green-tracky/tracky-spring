package com.example.tracky.runrecord.picture;

import java.io.IOException;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.tracky._core.error.ErrorCodeEnum;
import com.example.tracky._core.image.ImageService;
import com.example.tracky.runrecord.RunRecord;
import com.example.tracky.runrecord.RunRecordRequest;

import lombok.RequiredArgsConstructor;

/**
 * Picture 엔티티와 관련된 데이터베이스 작업을 처리하는 서비스입니다.
 * ImageService와 협력하여 이미지 파일 저장 및 DB 메타데이터 기록을 총괄합니다.
 */
@Service
@RequiredArgsConstructor
public class PictureService {

    private final PictureRepository pictureRepository;
    private final ImageService imageService; // 파일 처리 전문가를 주입받습니다.

    /**
     * 달리기 기록에 포함된 여러 장의 사진을 저장합니다.
     *
     * @param savedRunRecord DB에 저장된 RunRecord 엔티티
     * @param pictureDtos    요청으로 들어온 사진 정보 DTO 리스트
     * @param user           사진을 업로드한 User 엔티티
     */
    public void createAndSavePictures(RunRecord savedRunRecord, List<RunRecordRequest.DTO.Picture> pictureDtos,
            User user) {
        // 사진 정보가 없으면 아무 작업도 하지 않고 리턴합니다.
        if (pictureDtos == null || pictureDtos.isEmpty()) {
            return;
        }

        for (RunRecordRequest.DTO.Picture picDto : pictureDtos) {
            try {
                // 1. ImageService에 파일 저장을 위임하고, DB에 저장할 경로를 받습니다.
                String imagePath = imageService.save(
                        picDto.getImgBase64(),
                        user.getTag(), // User의 유니크한 태그를 사용합니다.
                        savedRunRecord.getId().longValue() // RunRecord의 ID를 사용합니다.
                );

                // 2. 받은 경로와 DTO 정보로 Picture 엔티티를 생성합니다.
                Picture picture = Picture.builder()
                        .runRecord(savedRunRecord)
                        .filePath(imagePath)
                        .lat(picDto.getLat())
                        .lon(picDto.getLon())
                        .build();

                // 3. Picture 엔티티를 데이터베이스에 저장합니다.
                pictureRepository.save(picture);

            } catch (IOException e) {
                // 파일 저장 중 I/O 에러가 발생하면, 트랜잭션 롤백을 위해 런타임 예외로 전환하여 던집니다.
                throw new Exception500(ErrorCodeEnum.IMAGE_SAVE_FAILED.getMessage());
            }
        }
    }
}
