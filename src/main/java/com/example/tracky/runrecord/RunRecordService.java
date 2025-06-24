package com.example.tracky.runrecord;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.tracky._core.error.ErrorCodeEnum;
import com.example.tracky._core.error.ex.ExceptionApi404;
import com.example.tracky.runrecord.RunRecordResponse.MainPageDTO;
import com.example.tracky.runrecord.RunRecordResponse.MainPageDTO.RecentRunsDTO;
import com.example.tracky.runrecord.RunRecordResponse.MainPageDTO.StatsDTO;
import com.example.tracky.runrecord.runbadge.RunBadge;
import com.example.tracky.runrecord.runbadge.RunBadgeRepository;
import com.example.tracky.runrecord.runbadge.RunBadgeResponse;
import com.example.tracky.runrecord.utils.RunRecordUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RunRecordService {

    private final RunRecordRepository runRecordsRepository;
    private final RunBadgeRepository runBadgeRepository;

    public void getRunRecord(Integer id) {
        // 러닝 기록 조회
        RunRecord runRecord = runRecordsRepository.findById(id)
                .orElseThrow(() -> new ExceptionApi404(ErrorCodeEnum.RUN_NOT_FOUND));

    }

    public MainPageDTO getActivitis() {
        List<RunRecord> runRecords = runRecordsRepository.findAllByUserIdJoin(); // 나중에 유저 아이디로 조회 해야함
        List<RunBadge> runBadges = runBadgeRepository.findAllBadge(); // 나중에 획득한
        // 뱃지만 가져와야함

        Integer totalDistanceMeters = 0; // 총 거리. 미터 단위
        Integer totalDurationSeconds = 0; // 총 시간. 초 단위

        Integer recentDistanceMeters = 0; // 러닝별 거리. 미터 단위
        Integer recentDurationSeconds = 0; // 러닝별 시간. 초 단위

        // runBadgeList 생성
        List<RunBadgeResponse.DTO> runBadgeList = new ArrayList<>();
        for (RunBadge badge : runBadges) {
            runBadgeList.add(new RunBadgeResponse.DTO(badge));
        }

        // recentRunList 생성
        List<RecentRunsDTO> recentRunList = new ArrayList<>();
        for (RunRecord record : runRecords) {
            totalDistanceMeters += record.getTotalDistanceMeters();
            recentDistanceMeters = record.getTotalDistanceMeters();
            totalDurationSeconds += record.getTotalDurationSeconds();
            recentDurationSeconds = record.getTotalDurationSeconds();

            Integer recentAvgPace = RunRecordUtil.calculatePace(recentDistanceMeters, recentDurationSeconds);

            recentRunList.add(new MainPageDTO.RecentRunsDTO(record, runBadgeList, recentAvgPace));
        }

        // runStatsList 생성성
        RunRecord runRecord = RunRecord.builder()
                .totalDistanceMeters(totalDistanceMeters)
                .totalDurationSeconds(totalDurationSeconds)
                .build();

        int count = runRecords.size();
        Integer statsAvgPace = RunRecordUtil.calculatePace(totalDistanceMeters, totalDurationSeconds);

        List<StatsDTO> statsList = List.of(new StatsDTO(runRecord, count, statsAvgPace));

        // MainDTO
        return new RunRecordResponse.MainPageDTO(statsList, runBadgeList, recentRunList);
    }

    /**
     * 러닝 저장
     * 
     * @param userId
     * @param reqDTO
     */
    @Transactional
    public RunRecordResponse.SaveDTO save(Integer userId, RunRecordRequest.SaveDTO reqDTO) {
        // 엔티티 변환
        RunRecord runRecord = reqDTO.toEntity(userId);

        // 엔티티 저장
        RunRecord runRecordPS = runRecordsRepository.save(runRecord);

        // 응답 DTO 로 변환
        return new RunRecordResponse.SaveDTO(runRecordPS);
    }

}