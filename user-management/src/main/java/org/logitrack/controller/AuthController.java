package org.logitrack.controller;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.logitrack.dto.request.AppUserLoginRequest;
import org.logitrack.dto.request.AppUserRegistrationRequest;
import org.logitrack.dto.response.ApiResponse;
import org.logitrack.dto.response.LoginResponse;
import org.logitrack.emails.EmailService;
import org.logitrack.entities.VerificationToken;
import org.logitrack.services.CustomerService;
import org.logitrack.services.CustomerServiceImpl.AdminServiceImpl;
import org.logitrack.validation.PasswordValidator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/auth")
public class AuthController {
    private final CustomerService userService;
    private final PasswordValidator passwordValidator;
    private final EmailService emailService;
    private final AdminServiceImpl adminService;

    @PostMapping(path = "/login")
    public ResponseEntity<ApiResponse<LoginResponse>> loginUser(@RequestBody @Valid AppUserLoginRequest request) {
        log.info("request to login user");
        ApiResponse<LoginResponse> response = userService.login(request);
        return ResponseEntity.status(response.getHttpStatus()).body(response);
    }

    @PostMapping(path = "/register")
    public ResponseEntity<?> registerUser(@RequestBody @Valid AppUserRegistrationRequest request) {
        log.info("controller register: register user :: [{}] ::", request.getEmail());
        passwordValidator.isValid(request);
        ApiResponse response = userService.registerUser(request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }


    @PostMapping("/confirm-account")
    public ResponseEntity<?> confirmUserAccount(@RequestBody VerificationToken tokenDTO) {
        String confirmationToken = tokenDTO.getConfirmationToken();
        String response = emailService.confirmEmail(confirmationToken);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/regenerate-verification-token")
    public ResponseEntity<ApiResponse> regenerateVerificationTokenAndSendEmail(@RequestParam("email") String email) {
        ApiResponse response = userService.regenerateVerificationTokenAndSendEmail(email);
        return ResponseEntity.ok(response);
    }



}
