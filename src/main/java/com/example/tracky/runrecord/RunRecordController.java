package com.example.tracky.runrecord;

import com.example.tracky._core.utils.Resp;
import com.example.tracky.user.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/s/api")
public class RunRecordController {

    private final RunRecordService runRecordService;

    @GetMapping("/activities/week")
    public ResponseEntity<?> getActivitiesWeek(@RequestParam(value = "base-date", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate baseDate) {
        if (baseDate == null) baseDate = LocalDate.now();  // 오늘 날짜로 기본값 설정
        RunRecordResponse.WeekDTO respDTO = runRecordService.getActivitiesWeek(baseDate);
        return Resp.ok(respDTO);
    }

    @GetMapping("/activities/month")
    public ResponseEntity<?> getActivitiesMonth(@RequestParam("month") int month, @RequestParam("year") int year) {
        RunRecordResponse.MonthDTO respDTO = runRecordService.getActivitiesMonth(month, year);
        return Resp.ok(respDTO);
    }

    @GetMapping("/activities/year")
    public ResponseEntity<?> getActivitiesYear(@RequestParam("year") int year) {
        RunRecordResponse.YearDTO respDTO = runRecordService.getActivitiesYear(year);
        return Resp.ok(respDTO);
    }

    @GetMapping("/activities/all")
    public ResponseEntity<?> getActivitiesAll() {
        RunRecordResponse.AllDTO respDTO = runRecordService.getActivitiesAll();
        return Resp.ok(respDTO);
    }

    @PostMapping("/runs")
    public ResponseEntity<?> save(@RequestBody RunRecordRequest.SaveDTO reqDTO) {
        // 유저 아이디를 임시로 1 로 함
        Integer userId = 1;

        // 필터에서 가져올거 미리 가져옴 나중에 세션에서 가져와야함
        User user = User.builder().id(userId).build();

        RunRecordResponse.SaveDTO respDTO = runRecordService.save(user, reqDTO);

        return Resp.ok(respDTO);
    }

    @GetMapping("/runs/{id}")
    public ResponseEntity<?> getRunRecord(@PathVariable Integer id) {
        // 유저 아이디를 임시로 1 로 함
        Integer userId = 1;

        // 필터에서 가져올거 미리 가져옴 나중에 세션에서 가져와야함
        User user = User.builder().id(userId).build();

        RunRecordResponse.DetailDTO respDTO = runRecordService.getRunRecord(user, id);

        return Resp.ok(respDTO);
    }

    @DeleteMapping("/runs/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id) {
        // 유저 아이디를 임시로 1 로 함
        Integer userId = 1;

        // 필터에서 가져올거 미리 가져옴 나중에 세션에서 가져와야함
        User user = User.builder().id(userId).build();

        runRecordService.delete(user, id);

        return Resp.ok(null);
    }

    @PutMapping("/runs/{id}")
    public ResponseEntity<?> update(@PathVariable Integer id, @RequestBody RunRecordRequest.UpdateDTO reqDTO) {
        // 유저 아이디를 임시로 1 로 함
        Integer userId = 1;

        // 필터에서 가져올거 미리 가져옴 나중에 세션에서 가져와야함
        User user = User.builder().id(userId).build();

        RunRecordResponse.UpdateDTO respDTO = runRecordService.update(user, id, reqDTO);

        return Resp.ok(respDTO);
    }
}