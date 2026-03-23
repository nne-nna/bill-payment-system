package com.billpayments.billpaymentsystem.service;

import com.billpayments.billpaymentsystem.payload.request.ChangePasswordRequest;
import com.billpayments.billpaymentsystem.payload.request.UpdateProfileRequest;
import com.billpayments.billpaymentsystem.payload.response.ProfileResponse;

import java.security.Principal;

public interface UserService {
    ProfileResponse getProfile(Principal principal);
    ProfileResponse updateProfile(UpdateProfileRequest request, Principal principal);
    void changePassword(ChangePasswordRequest request, Principal principal);
}
