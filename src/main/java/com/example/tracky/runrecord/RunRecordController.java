package com.example.tracky.runrecord;

import com.example.tracky._core.utils.Resp;
import com.example.tracky.user.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/s/api")
public class RunRecordController {

    private final RunRecordService runRecordService;

    @GetMapping("/activities/week")
    public ResponseEntity<?> getActivitiesWeek(@RequestParam(value = "before", defaultValue = "0") Integer before) {
        // 유저 아이디를 임시로 1 로 함
        Integer userId = 1;

        // 필터에서 가져올거 미리 가져옴 나중에 세션에서 가져와야함
        User user = User.builder().id(userId).build();

        // before가 0~4 사이가 아니면 0으로 기본 처리 (범위 제한)
        if (before == null || before < 0 || before > 4) {
            before = 0;
        }

        // 테스트
//        LocalDate baseDate = LocalDate.of(2025, 3, 31);

        //배포시 사용
        LocalDate baseDate = LocalDate.now();
        System.out.println("오늘 : " + baseDate);
        RunRecordResponse.WeekDTO respDTO = runRecordService.getActivitiesWeek(user, baseDate, before);
        return Resp.ok(respDTO);
    }

    @GetMapping("/activities/month")
    public ResponseEntity<?> getActivitiesMonth(@RequestParam("month") int month, @RequestParam("year") int year) {
        // 유저 아이디를 임시로 1 로 함
        Integer userId = 1;

        // 필터에서 가져올거 미리 가져옴 나중에 세션에서 가져와야함
        User user = User.builder().id(userId).build();

        RunRecordResponse.MonthDTO respDTO = runRecordService.getActivitiesMonth(user, month, year);
        return Resp.ok(respDTO);
    }

    @GetMapping("/activities/year")
    public ResponseEntity<?> getActivitiesYear(@RequestParam("year") int year) {
        // 유저 아이디를 임시로 1 로 함
        Integer userId = 1;

        // 필터에서 가져올거 미리 가져옴 나중에 세션에서 가져와야함
        User user = User.builder().id(userId).build();

        RunRecordResponse.YearDTO respDTO = runRecordService.getActivitiesYear(user, year);
        return Resp.ok(respDTO);
    }

    @GetMapping("/activities/all")
    public ResponseEntity<?> getActivitiesAll() {
        // 유저 아이디를 임시로 1 로 함
        Integer userId = 1;

        // 필터에서 가져올거 미리 가져옴 나중에 세션에서 가져와야함
        User user = User.builder().id(userId).build();

        RunRecordResponse.AllDTO respDTO = runRecordService.getActivitiesAll(user);
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