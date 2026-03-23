package com.billpayments.billpaymentsystem.service.impl;

import com.billpayments.billpaymentsystem.exceptions.BadRequestException;
import com.billpayments.billpaymentsystem.exceptions.ResourceNotFoundException;
import com.billpayments.billpaymentsystem.models.User;
import com.billpayments.billpaymentsystem.payload.request.ChangePasswordRequest;
import com.billpayments.billpaymentsystem.payload.request.UpdateProfileRequest;
import com.billpayments.billpaymentsystem.payload.response.ProfileResponse;
import com.billpayments.billpaymentsystem.repository.UserRepository;
import com.billpayments.billpaymentsystem.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.Principal;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    //helper methods
    private User getUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private ProfileResponse mapToResponse(User user) {
        return ProfileResponse.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .role(user.getRole().name())
                .createdAt(user.getCreatedAt())
                .build();
    }

    @Override
    public ProfileResponse getProfile(Principal principal) {
        User user = getUser(principal.getName());
        log.info("Fetching profile for: {}", user.getEmail());
        return mapToResponse(user);
    }

    @Override
    public ProfileResponse updateProfile(UpdateProfileRequest request, Principal principal) {
        User user = getUser(principal.getName());
        log.info("Updating profile for: {}", user.getEmail());

        // Check if phone is taken by another user
        if (!user.getPhone().equals(request.getPhone()) &&
                userRepository.existsByPhone(request.getPhone())) {
            throw new BadRequestException("Phone number already in use");
        }

        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setPhone(request.getPhone());

        userRepository.save(user);
        log.info("Profile updated successfully for: {}", user.getEmail());

        return mapToResponse(user);
    }

    @Override
    public void changePassword(ChangePasswordRequest request, Principal principal) {
        User user = getUser(principal.getName());
        log.info("Changing password for: {}", user.getEmail());

        // Check current password is correct
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new BadRequestException("Current password is incorrect");
        }

        // Check new password and confirm password match
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new BadRequestException("New password and confirm password do not match");
        }

        // Check new password is different from current
        if (passwordEncoder.matches(request.getNewPassword(), user.getPassword())) {
            throw new BadRequestException("New password must be different from current password");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        log.info("Password changed successfully for: {}", user.getEmail());
    }
}
