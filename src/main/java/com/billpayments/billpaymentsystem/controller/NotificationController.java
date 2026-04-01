package com.billpayments.billpaymentsystem.controller;

import com.billpayments.billpaymentsystem.payload.response.NotificationResponse;
import com.billpayments.billpaymentsystem.payload.response.NotificationSummaryResponse;
import com.billpayments.billpaymentsystem.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
@Slf4j
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public ResponseEntity<NotificationSummaryResponse> getAllNotifications(Principal principal){
        NotificationSummaryResponse response = notificationService.getAllNotifications(principal);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/unread")
    public ResponseEntity<List<NotificationResponse>> getUnreadNotifications(Principal principal){
        List<NotificationResponse> response = notificationService.getUnreadNotifications(principal);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/read")
    public ResponseEntity<NotificationResponse> markAsRead(@PathVariable Long id, Principal principal){
        NotificationResponse response = notificationService.markAsRead(id, principal);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/read-all")
    public ResponseEntity<?> markAllAsRead(Principal principal) {
        notificationService.markAllAsRead(principal);
        return ResponseEntity.ok("All notifications marked as read");
    }
}
