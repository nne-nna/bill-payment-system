package com.billpayments.billpaymentsystem.service.impl;

import com.billpayments.billpaymentsystem.enums.NotificationType;
import com.billpayments.billpaymentsystem.exceptions.BadRequestException;
import com.billpayments.billpaymentsystem.exceptions.ResourceNotFoundException;
import com.billpayments.billpaymentsystem.models.Notification;
import com.billpayments.billpaymentsystem.models.User;
import com.billpayments.billpaymentsystem.payload.response.NotificationResponse;
import com.billpayments.billpaymentsystem.payload.response.NotificationSummaryResponse;
import com.billpayments.billpaymentsystem.repository.NotificationRepository;
import com.billpayments.billpaymentsystem.repository.UserRepository;
import com.billpayments.billpaymentsystem.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    // Helper methods

    private User getUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private NotificationResponse mapToResponse(Notification notification) {
        return NotificationResponse.builder()
                .id(notification.getId())
                .type(notification.getType())
                .title(notification.getTitle())
                .message(notification.getMessage())
                .isRead(notification.isRead())
                .referenceId(notification.getReferenceId())
                .createdAt(notification.getCreatedAt())
                .build();
    }
    @Override
    public void createNotification(User user, NotificationType type,
                                   String title, String message, String referenceId) {
        log.info("Creating {} notification for user: {}", type, user.getEmail());

        Notification notification = Notification.builder()
                .user(user)
                .type(type)
                .title(title)
                .message(message)
                .referenceId(referenceId)
                .isRead(false)
                .build();

        notificationRepository.save(notification);
        log.info("Notification created successfully for user: {}", user.getEmail());
    }

    @Override
    public NotificationSummaryResponse getAllNotifications(Principal principal) {
        User user = getUser(principal.getName());

        List<NotificationResponse> notifications = notificationRepository
                .findByUserOrderByCreatedAtDesc(user)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        long unreadCount = notificationRepository.countByUserAndIsReadFalse(user);

        return NotificationSummaryResponse.builder()
                .unreadCount(unreadCount)
                .notifications(notifications)
                .build();
    }

    @Override
    public List<NotificationResponse> getUnreadNotifications(Principal principal) {
        User user = getUser(principal.getName());

        return notificationRepository
                .findByUserAndIsReadFalseOrderByCreatedAtDesc(user)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public NotificationResponse markAsRead(Long id, Principal principal) {
        User user = getUser(principal.getName());

        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Notification not found with id: " + id));

        // Security check
        if (!notification.getUser().getId().equals(user.getId())) {
            throw new BadRequestException(
                    "You are not authorized to update this notification");
        }

        notification.setRead(true);
        notificationRepository.save(notification);
        log.info("Notification {} marked as read", id);

        return mapToResponse(notification);
    }

    @Override
    @Transactional
    public void markAllAsRead(Principal principal) {
        User user = getUser(principal.getName());
        notificationRepository.markAllAsRead(user);
        log.info("All notifications marked as read for user: {}", user.getEmail());
    }

    @Override
    @Transactional
    public void deleteNotification(Long id, Principal principal) {
        User user = getUser(principal.getName());

        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Notification not found with id: " + id));

        // Security check
        if (!notification.getUser().getId().equals(user.getId())) {
            throw new BadRequestException(
                    "You are not authorized to delete this notification");
        }

        notificationRepository.delete(notification);
        log.info("Notification {} deleted", id);
    }
}