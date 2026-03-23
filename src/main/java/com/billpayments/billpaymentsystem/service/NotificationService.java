package com.billpayments.billpaymentsystem.service;

import com.billpayments.billpaymentsystem.enums.NotificationType;
import com.billpayments.billpaymentsystem.models.User;
import com.billpayments.billpaymentsystem.payload.response.NotificationResponse;
import com.billpayments.billpaymentsystem.payload.response.NotificationSummaryResponse;

import java.security.Principal;
import java.util.List;

public interface NotificationService {
    //internal methods called by other services
    void createNotification(User user, NotificationType type, String title, String message, String referenceId);

    //external methods exposed via api
    NotificationSummaryResponse getAllNotifications(Principal principal);
    List<NotificationResponse> getUnreadNotifications(Principal principal);
    NotificationResponse markAsRead(Long id, Principal principal);
    void markAllAsRead(Principal principal);
    void deleteNotification(Long id, Principal principal);
}