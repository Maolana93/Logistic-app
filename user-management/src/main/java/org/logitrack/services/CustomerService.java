package org.logitrack.services;

import org.logitrack.dto.request.AppUserLoginRequest;
import org.logitrack.dto.request.AppUserRegistrationRequest;
import org.logitrack.dto.request.DeliveryManCreationRequest;
import org.logitrack.dto.response.ApiResponse;
import org.logitrack.dto.response.LoginResponse;

public interface CustomerService {
    ApiResponse registerUser(AppUserRegistrationRequest registrationDTO);


    ApiResponse<LoginResponse> login(AppUserLoginRequest loginDTO);

    ApiResponse registerDeliveryMan(DeliveryManCreationRequest request);


    ApiResponse regenerateVerificationTokenAndSendEmail(String email);
}
