package com.example.tracky.runrecord;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.tracky._core.utils.Resp;

import lombok.RequiredArgsConstructor;

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
    public ResponseEntity<?> getActivitisWeek() {
        RunRecordResponse.MainPageDTO respDTO = runRecordsService.getActivitisWeek();
        return Resp.ok(respDTO);
    }

    @GetMapping("/activitis/month")
    public ResponseEntity<?> getActivitisMonth(@RequestParam("month") int month, @RequestParam("year") int year) {
        RunRecordResponse.MainPageDTO respDTO = runRecordsService.getActivitisMonth(month, year);
        return Resp.ok(respDTO);
    }

    @GetMapping("/activitis/year")
    public ResponseEntity<?> getActivitisYear() {
        RunRecordResponse.MainPageDTO respDTO = runRecordsService.getActivitisYear();
        return Resp.ok(respDTO);
    }

    @GetMapping("/activitis/all")
    public ResponseEntity<?> getActivitisAll() {
        RunRecordResponse.MainPageDTO respDTO = runRecordsService.getActivitisAll();
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