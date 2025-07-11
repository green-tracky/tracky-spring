package com.example.tracky.notification;

import com.example.tracky._core.constants.SessionKeys;
import com.example.tracky._core.utils.Resp;
import com.example.tracky.user.kakaojwt.OAuthProfile;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/s/api")
public class NotificationController {
    private final NotificationService notificationService;
    private final HttpSession session;

    @GetMapping("/notifications")
    public ResponseEntity<?> getNotifications() {
        OAuthProfile sessionProfile = (OAuthProfile) session.getAttribute(SessionKeys.PROFILE);

        NotificationResponse.NotificationListDTO respDTO = notificationService.getNotifications(sessionProfile);
        return Resp.ok(respDTO);
    }

}
