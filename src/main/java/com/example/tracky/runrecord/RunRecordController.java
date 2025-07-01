package com.example.tracky.runrecord;

import com.example.tracky._core.utils.Resp;
import com.example.tracky.user.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
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
    public ResponseEntity<?> getActivitiesWeek(@RequestParam(value = "base-date", required = false)
                                               @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate baseDate) {
        // 유저 아이디를 임시로 1 로 함
        Integer userId = 1;

        // 필터에서 가져올거 미리 가져옴 나중에 세션에서 가져와야함
        User user = User.builder().id(userId).build();

        if (baseDate == null) baseDate = LocalDate.now();  // 오늘 날짜로 기본값 설정
        RunRecordResponse.WeekDTO respDTO = runRecordService.getActivitiesWeek(baseDate, user);
        return Resp.ok(respDTO);
    }

    @GetMapping("/activities/month")
    public ResponseEntity<?> getActivitiesMonth(@RequestParam(value = "month", required = false) Integer month,
                                                @RequestParam(value = "year", required = false) Integer year) {
        // 유저 아이디를 임시로 1 로 함
        Integer userId = 1;

        // 필터에서 가져올거 미리 가져옴 나중에 세션에서 가져와야함
        User user = User.builder().id(userId).build();

        // 오늘 날짜 기준으로 기본값 설정
        LocalDate today = LocalDate.now();
        if (month == null) month = today.getMonthValue();  // 1~12
        if (year == null) year = today.getYear();

        RunRecordResponse.MonthDTO respDTO = runRecordService.getActivitiesMonth(month, year, user);
        return Resp.ok(respDTO);
    }

    @GetMapping("/activities/year")
    public ResponseEntity<?> getActivitiesYear(@RequestParam(value = "year", required = false) Integer year) {
        // 유저 아이디를 임시로 1 로 함
        Integer userId = 1;

        // 필터에서 가져올거 미리 가져옴 나중에 세션에서 가져와야함
        User user = User.builder().id(userId).build();

        // 오늘 날짜 기준으로 기본값 설정
        LocalDate today = LocalDate.now();
        if (year == null) year = today.getYear();

        RunRecordResponse.YearDTO respDTO = runRecordService.getActivitiesYear(year, user);
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

    @GetMapping("/activities/recent")
    public ResponseEntity<?> getActivitiesRecent(@RequestParam(value = "order", defaultValue = "latest") String order,
                                                 @RequestParam(value = "year", required = false) Integer year,
                                                 @RequestParam(value = "page", defaultValue = "1") Integer page) {
        // 유저 아이디를 임시로 1 로 함
        Integer userId = 1;

        // 필터에서 가져올거 미리 가져옴 나중에 세션에서 가져와야함
        User user = User.builder().id(userId).build();

        if ("latest".equals(order) || "oldest".equals(order)) {
            RunRecordResponse.GroupedRecentListDTO grouped = runRecordService.getGroupedActivities(user, order, year, page);
            return Resp.ok(grouped);
        } else {
            RunRecordResponse.FlatRecentListDTO flat = runRecordService.getFlatActivities(user, order, year, page);
            return Resp.ok(flat);
        }
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