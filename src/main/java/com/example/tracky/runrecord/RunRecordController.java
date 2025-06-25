package com.example.tracky.runrecord;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.tracky._core.utils.Resp;

import lombok.RequiredArgsConstructor;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
@RequestMapping("/s/api")
public class RunRecordController {

    private final RunRecordService runRecordsService;

    @GetMapping("/activitis")
    public ResponseEntity<?> getActivitis() {
        RunRecordResponse.MainPageDTO respDTO = runRecordsService.getActivitis();
        return Resp.ok(respDTO);
    }

    @GetMapping("/activitis/week")
    public ResponseEntity<?> getActivitisWeek(@RequestParam(value = "baseDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate baseDate) {
        if (baseDate == null) baseDate = LocalDate.now();  // 오늘 날짜로 기본값 설정
        RunRecordResponse.StatsDTO respDTO = runRecordsService.getActivitisWeek(baseDate);
        return Resp.ok(respDTO);
    }

    @GetMapping("/activitis/month")
    public ResponseEntity<?> getActivitisMonth(@RequestParam("month") int month, @RequestParam("year") int year) {
        RunRecordResponse.StatsDTO respDTO = runRecordsService.getActivitisMonth(month, year);
        return Resp.ok(respDTO);
    }

    @GetMapping("/activitis/year")
    public ResponseEntity<?> getActivitisYear(@RequestParam("year") int year) {
        RunRecordResponse.StatsDTO respDTO = runRecordsService.getActivitisYear(year);
        return Resp.ok(respDTO);
    }

    @GetMapping("/activitis/all")
    public ResponseEntity<?> getActivitisAll() {
        RunRecordResponse.StatsDTO respDTO = runRecordsService.getActivitisAll();
        return Resp.ok(respDTO);
    }

    @PostMapping("/runs")
    public ResponseEntity<?> save(@RequestBody RunRecordRequest.SaveDTO reqDTO) {
        // 유저 아이디를 임시로 1 로 함
        Integer userId = 1;

        RunRecordResponse.SaveDTO respDTO = runRecordsService.save(userId, reqDTO);

        return Resp.ok(respDTO);
    }

}