package com.example.tracky.runrecord;

import com.example.tracky._core.constants.SessionKeys;
import com.example.tracky._core.utils.Resp;
import com.example.tracky._core.values.TimeValue;
import com.example.tracky.user.kakaojwt.OAuthProfile;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/s/api")
public class RunRecordController {

    private final RunRecordService runRecordService;
    private final HttpSession session;

    @GetMapping("/activities/week")
    public ResponseEntity<?> getActivitiesWeek(@RequestParam(value = "before", defaultValue = "0") Integer before) {
        // 세션에서 유저 정보 꺼내기
        OAuthProfile sessionProfile = (OAuthProfile) session.getAttribute(SessionKeys.PROFILE);

        // before가 0~4 사이가 아니면 0으로 기본 처리 (범위 제한)
        if (before == null || before < 0 || before > 4) {
            before = 0;
        }

        //배포시 사용
        LocalDate baseDate = TimeValue.getServerTime().toLocalDate();
        System.out.println("오늘 : " + baseDate);
        RunRecordResponse.WeekDTO respDTO = runRecordService.getActivitiesWeek(sessionProfile, baseDate, before);
        return Resp.ok(respDTO);
    }

    @GetMapping("/activities/month")
    public ResponseEntity<?> getActivitiesMonth(@RequestParam(value = "month", required = false) Integer month,
                                                @RequestParam(value = "year", required = false) Integer year) {
        // 세션에서 유저 정보 꺼내기
        OAuthProfile sessionProfile = (OAuthProfile) session.getAttribute(SessionKeys.PROFILE);

        // 오늘 날짜 기준으로 기본값 설정
        LocalDate today = TimeValue.getServerTime().toLocalDate();
        if (month == null) month = today.getMonthValue();  // 1~12
        if (year == null) year = today.getYear();

        RunRecordResponse.MonthDTO respDTO = runRecordService.getActivitiesMonth(sessionProfile, month, year);
        return Resp.ok(respDTO);
    }

    @GetMapping("/activities/year")
    public ResponseEntity<?> getActivitiesYear(@RequestParam(value = "year", required = false) Integer year) {
        // 세션에서 유저 정보 꺼내기
        OAuthProfile sessionProfile = (OAuthProfile) session.getAttribute(SessionKeys.PROFILE);

        // 오늘 날짜 기준으로 기본값 설정
        LocalDate today = TimeValue.getServerTime().toLocalDate();
        if (year == null) year = today.getYear();

        RunRecordResponse.YearDTO respDTO = runRecordService.getActivitiesYear(sessionProfile, year);
        return Resp.ok(respDTO);
    }

    @GetMapping("/activities/all")
    public ResponseEntity<?> getActivitiesAll() {
        // 세션에서 유저 정보 꺼내기
        OAuthProfile sessionProfile = (OAuthProfile) session.getAttribute(SessionKeys.PROFILE);

        RunRecordResponse.AllDTO respDTO = runRecordService.getActivitiesAll(sessionProfile);
        return Resp.ok(respDTO);
    }

    @GetMapping("/activities/recent")
    public ResponseEntity<?> getActivitiesRecent(@RequestParam(value = "order", defaultValue = "latest") String order,
                                                 @RequestParam(value = "year", required = false) Integer year,
                                                 @RequestParam(value = "page", defaultValue = "1") Integer page) {
        // 세션에서 유저 정보 꺼내기
        OAuthProfile sessionProfile = (OAuthProfile) session.getAttribute(SessionKeys.PROFILE);

        // page 값 0으로 들어오면 1로 바꿔주기
        if (page == null || page < 1) {
            page = 1;
        }

        if ("latest".equals(order) || "oldest".equals(order)) {
            RunRecordResponse.GroupedRecentListDTO grouped = runRecordService.getGroupedActivities(sessionProfile, order, year, page);
            return Resp.ok(grouped);
        } else {
            RunRecordResponse.FlatRecentListDTO flat = runRecordService.getFlatActivities(sessionProfile, order, year, page);
            return Resp.ok(flat);
        }
    }

    @PostMapping("/runs")
    public ResponseEntity<?> save(@Valid @RequestBody RunRecordRequest.SaveDTO reqDTO, Errors errors) {
        // 세션에서 유저 정보 꺼내기
        OAuthProfile sessionProfile = (OAuthProfile) session.getAttribute(SessionKeys.PROFILE);

        RunRecordResponse.SaveDTO respDTO = runRecordService.save(sessionProfile, reqDTO);

        return Resp.ok(respDTO);
    }

    @GetMapping("/runs/{id}")
    public ResponseEntity<?> getRunRecord(@PathVariable Integer id) {
        // 세션에서 유저 정보 꺼내기
        OAuthProfile sessionProfile = (OAuthProfile) session.getAttribute(SessionKeys.PROFILE);

        RunRecordResponse.DetailDTO respDTO = runRecordService.getRunRecord(sessionProfile, id);

        return Resp.ok(respDTO);
    }

    @DeleteMapping("/runs/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id) {
        // 세션에서 유저 정보 꺼내기
        OAuthProfile sessionProfile = (OAuthProfile) session.getAttribute(SessionKeys.PROFILE);

        runRecordService.delete(sessionProfile, id);

        return Resp.ok(null);
    }

    @PutMapping("/runs/{id}")
    public ResponseEntity<?> update(@PathVariable Integer id, @Valid @RequestBody RunRecordRequest.UpdateDTO reqDTO, Errors errors) {
        // 세션에서 유저 정보 꺼내기
        OAuthProfile sessionProfile = (OAuthProfile) session.getAttribute(SessionKeys.PROFILE);

        RunRecordResponse.UpdateDTO respDTO = runRecordService.update(sessionProfile, id, reqDTO);

        return Resp.ok(respDTO);
    }

    @GetMapping("/runs")
    public ResponseEntity<?> getRunRecords() {
        // 세션에서 유저 정보 꺼내기
        OAuthProfile sessionProfile = (OAuthProfile) session.getAttribute(SessionKeys.PROFILE);

        List<RunRecordResponse.SimpleDTO> respDTO = runRecordService.getRunRecords(sessionProfile);

        return Resp.ok(respDTO);
    }
}