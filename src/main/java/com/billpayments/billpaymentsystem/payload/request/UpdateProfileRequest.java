package com.billpayments.billpaymentsystem.payload.request;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateProfileRequest {
    @Size(max = 50, message = "First name must not exceed 50 characters")
    @Pattern(regexp = "^(?!\\s*$).+", message = "First name cannot be blank")
    private String firstName;

    @Size(max = 50, message = "Last name must not exceed 50 characters")
    @Pattern(regexp = "^(?!\\s*$).+", message = "Last name cannot be blank")
    private String lastName;

    @Pattern(
            regexp = "^(?:(?:\\+234|234)|0)(?:70|80|81|90|91)\\d{8}$",
            message = "Phone number must be a valid Nigerian mobile number"
    )
    private String phone;
}
