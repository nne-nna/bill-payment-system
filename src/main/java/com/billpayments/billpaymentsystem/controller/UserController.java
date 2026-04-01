package com.billpayments.billpaymentsystem.controller;

import com.billpayments.billpaymentsystem.payload.request.ChangePasswordRequest;
import com.billpayments.billpaymentsystem.payload.request.UpdateProfileRequest;
import com.billpayments.billpaymentsystem.payload.response.ProfileResponse;
import com.billpayments.billpaymentsystem.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    @GetMapping("/profile")
    public ResponseEntity<ProfileResponse> getProfile(Principal principal) {
        ProfileResponse response = userService.getProfile(principal);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/profile")
    public ResponseEntity<ProfileResponse> updateProfile(
            @Valid @RequestBody UpdateProfileRequest request,
            Principal principal) {
        ProfileResponse response = userService.updateProfile(request, principal);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(
            @Valid @RequestBody ChangePasswordRequest request,
            Principal principal) {
        userService.changePassword(request, principal);
        return ResponseEntity.ok("Password changed successfully");
    }
}
