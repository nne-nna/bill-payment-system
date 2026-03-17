package com.billpayments.billpaymentsystem.service.impl;

import com.billpayments.billpaymentsystem.enums.Role;
import com.billpayments.billpaymentsystem.exceptions.BadRequestException;
import com.billpayments.billpaymentsystem.models.User;
import com.billpayments.billpaymentsystem.models.Wallet;
import com.billpayments.billpaymentsystem.payload.request.LoginRequest;
import com.billpayments.billpaymentsystem.payload.request.RegisterRequest;
import com.billpayments.billpaymentsystem.payload.response.AuthResponse;
import com.billpayments.billpaymentsystem.repository.UserRepository;
import com.billpayments.billpaymentsystem.repository.WalletRepository;
import com.billpayments.billpaymentsystem.security.JwtUtil;
import com.billpayments.billpaymentsystem.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final WalletRepository walletRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    @Override
    public AuthResponse register(RegisterRequest request){
        log.info("Registering new user with email: {}", request.getEmail());

        //check if email exists
        if(userRepository.existsByEmail(request.getEmail())){
            throw  new BadRequestException("Email already registered");
        }

        //check if phone already exists
        if(userRepository.existsByPhone(request.getPhone())){
            throw new BadRequestException("Phone number already registered");
        }

        //create and save the user
        User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .phone(request.getPhone())
                .role(Role.USER)
                .build();

        userRepository.save(user);
        log.info("User saved successfully: {}", user.getEmail());

        //create a wallet for the user with zero balance
        Wallet wallet = Wallet.builder()
                .user(user)
                .balance(BigDecimal.ZERO)
                .build();

        walletRepository.save(wallet);
        log.info("Wallet created for user: {}", user.getEmail());

        //Generate JWT token
        String token = jwtUtil.generateToken(user.getEmail());

        return AuthResponse.builder()
                .token(token)
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .message("Registration successful")
                .build();
    }

    @Override
    public AuthResponse login(LoginRequest request){
        log.info("Login attempt for email: {}", request.getEmail());

        //This verifies email and password and throws an exception is wrong
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        //If here, credentials and correct and user can be loaded
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadRequestException("User not found"));

        //Generate JWT token
        String token = jwtUtil.generateToken(user.getEmail());
        log.info("Login successful for: {}", user.getEmail());

        return AuthResponse.builder()
                .token(token)
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .message("Login successful")
                .build();
    }
}
