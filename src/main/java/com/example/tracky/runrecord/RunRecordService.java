package com.example.tracky.runrecord;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.tracky._core.error.ErrorCodeEnum;
import com.example.tracky._core.error.ex.ExceptionApi404;
import com.example.tracky.runrecord.RunRecordResponse.MainPageDTO;
import com.example.tracky.runrecord.runbadge.RunBadge;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RunRecordService {

    private final RunRecordRepository runRecordsRepository;

    public void getRunRecord(Integer id) {
        // 러닝 기록 조회
        RunRecord runRecord = runRecordsRepository.findById(id)
                .orElseThrow(() -> new ExceptionApi404(ErrorCodeEnum.RUN_NOT_FOUND));

    }

    public MainPageDTO getRunRecords() {
        List<RunRecord> runRecords = runRecordsRepository.findAll();
        System.out.println("runRecords : "+ runRecords);
        List<RunBadge> runBadges = runRecordsRepository.findAllBadge();
        System.out.println("runBadges : "+ runBadges);

        Integer totalDistanceMeters = 0;
        Integer totalDurationSeconds= 0; // 총 시간. 초 단위
        double totalavgPace = 0.0;

         for (RunRecord runRecord : runRecords) {
            totalDistanceMeters += runRecord.getTotalDistanceMeters();
            totalDurationSeconds += runRecord.getTotalDurationSeconds();
            totalavgPace += runRecord.getAvgPace();
         }

         RunRecord totalRunRecord = RunRecord.builder()
         .totalDistanceMeters(totalDistanceMeters)
         .totalDurationSeconds(totalDurationSeconds)
         .avgPace(totalavgPace)
         .build();

         System.out.println(totalDistanceMeters);
         System.out.println(totalDurationSeconds);
         System.out.println(totalavgPace);

        return new RunRecordResponse.MainPageDTO(totalRunRecord, runRecords, runBadges);
    }

}