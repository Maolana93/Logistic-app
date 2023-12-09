package org.logitrack.services.CustomerServiceImpl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.logitrack.dto.request.AppUserLoginRequest;
import org.logitrack.dto.request.AppUserRegistrationRequest;
import org.logitrack.dto.request.DeliveryManCreationRequest;
import org.logitrack.dto.response.ApiResponse;
import org.logitrack.dto.response.LoginResponse;
import org.logitrack.emails.EmailService;
import org.logitrack.entities.DeliveryMan;
import org.logitrack.entities.User;
import org.logitrack.entities.VerificationToken;
import org.logitrack.enums.Role;
import org.logitrack.exceptions.UserExistException;
import org.logitrack.repository.TokenRepository;
import org.logitrack.repository.UserRepository;
import org.logitrack.security.JWTService;
import org.logitrack.services.CustomerService;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.AbstractMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RequiredArgsConstructor
@Slf4j
@Service
public class CustomerServicImpl implements CustomerService {
    private final JWTService jwtService;
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final TokenRepository tokenRepository;
    private final EmailService emailService;

    @Override
    public ApiResponse registerUser(AppUserRegistrationRequest registrationDTO){
        Optional<User> optionalUser = userRepository.findByEmail(registrationDTO.getEmail());
        if (optionalUser.isPresent()){
            return new ApiResponse("00","User Already Exist", HttpStatus.ALREADY_REPORTED,"success");
        }
        User newUser = new User();
        newUser.setPassword(passwordEncoder.encode(registrationDTO.getPassword()));
        newUser.setFullName(registrationDTO.getFullName());
        newUser.setPhoneNumber(registrationDTO.getPhoneNumber());
        newUser.setIsVerified(false);
        newUser.setEmail(registrationDTO.getEmail());
        newUser.setRole(Role.CUSTOMER);
        User savedUser = userRepository.save(newUser);
        log.info("user saved to database... about generating email");
        VerificationToken confirmationToken = new VerificationToken(savedUser);
        tokenRepository.save(confirmationToken);
        log.info("verification token generated...");

    String confirmationLink = confirmationToken.getConfirmationToken();
        emailService.sendConfirmationEmail(savedUser, confirmationLink);
        log.info("email sent suceesfully");
        ApiResponse genericResponse = new ApiResponse<>();

        genericResponse.setMessage("Registration Successful, Please check your email to verify your account");
        genericResponse.setStatus("Success");
        genericResponse.setCode("00");
        return genericResponse;
    }




    @Override
    public ApiResponse registerDeliveryMan(DeliveryManCreationRequest request) {
        Optional<User> optionalUser = userRepository.findByEmail(request.getEmail());
        if (optionalUser.isPresent()) {
            return new ApiResponse("00", "Rider Already Exist", HttpStatus.ALREADY_REPORTED, "success");
        }
        DeliveryMan newDelivery = new DeliveryMan();
        newDelivery.setFullName(request.getFullName());
        newDelivery.setPhoneNumber(request.getPhoneNumber());
        newDelivery.setIsVerified(true);
        newDelivery.setEmail(request.getEmail());
        newDelivery.setRole(Role.DELIVERY_MAN);
        newDelivery.setBirthday(request.getBirthday());
        newDelivery.setCity(request.getCity());
        newDelivery.setAddress(request.getAddress());
        newDelivery.setGender(request.getGender());
        newDelivery.setPassword(passwordEncoder.encode(request.getPassword()));
        newDelivery.setDrivingLicenseNumber(request.getDrivingLicenseNumber());
        newDelivery.setCity(request.getCity());
        userRepository.save(newDelivery);
        ApiResponse genericResponse = new ApiResponse();
        genericResponse.setMessage("You have successfully create a rider");
        genericResponse.setStatus("Success");
        genericResponse.setCode("00");
        return genericResponse;
    }

    @Override
    public ApiResponse<LoginResponse> login(AppUserLoginRequest loginDTO) {
        log.info("Request to login at the service layer");

        Authentication authenticationUser;
        try {
            authenticationUser = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginDTO.getEmail(), loginDTO.getPassword())
            );
            log.info("Authenticated the User by the Authentication manager");
        } catch (DisabledException es) {
            return Stream.of(
                            new AbstractMap.SimpleEntry<>("message", "Disabled exception occurred"),
                            new AbstractMap.SimpleEntry<>("status", "failure"),
                            new AbstractMap.SimpleEntry<>("httpStatus", HttpStatus.BAD_REQUEST)
                    )
                    .collect(
                            Collectors.collectingAndThen(
                                    Collectors.toMap(AbstractMap.SimpleEntry::getKey, entry -> entry.getValue()),
                                    map -> new ApiResponse<>((Map<String, String>) map)
                            )
                    );

        } catch (BadCredentialsException e) {
            throw new UserExistException("Invalid email or password", HttpStatus.BAD_REQUEST);
        }

        // Tell securityContext that this user is in the context
        SecurityContextHolder.getContext().setAuthentication(authenticationUser);

        // Retrieve the user from the repository
        User appUser = userRepository.findByEmail(loginDTO.getEmail()).orElseThrow(() ->
                new UserExistException("User not found", HttpStatus.BAD_REQUEST));

        // Update the lastLoginDate field
        appUser.setLastLogin(LocalDateTime.now());
        log.info("last-login date updated");

        // Save the updated user entity
        User user = userRepository.save(appUser);
        log.info("user saved back to database");

        // Generate and send token
        String tokenGenerated = "Bearer " + jwtService.generateToken(authenticationUser, user.getRole());
        log.info("Jwt token generated for the user " + tokenGenerated);
        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setToken(tokenGenerated);

        ApiResponse<LoginResponse> apiResponse = new ApiResponse<>("00", "Success", loginResponse, "Successfully logged in", HttpStatus.OK);
        apiResponse.setData(loginResponse); // Set the LoginResponse as data

        return apiResponse;
    }




    @Override
    public ApiResponse regenerateVerificationTokenAndSendEmail(String email) {
        // Find the user by email
        log.info("Regeneration started");
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isEmpty()) {
            log.info("User not in database");
        }
        User existingUser = optionalUser.get();
        log.info("user found in database" + existingUser.getEmail());
        Optional<VerificationToken> optionalToken = tokenRepository.findTokenByEmail(existingUser.getEmail());
        if (optionalToken.isEmpty()){
            log.info("No existing token found for the user");
        }
        else {
            VerificationToken existingToken = optionalToken.get();
            log.info("Existing token retrieved: " + existingToken.getConfirmationToken());
            tokenRepository.delete(existingToken);
            log.info("Existing token deleted");
            VerificationToken newToken = new VerificationToken(existingUser);
            tokenRepository.save(newToken);
            log.info("New token generated: " + newToken);
            existingUser.setVerificationToken(newToken);
            String confirmationLink = existingUser.getVerificationToken().getConfirmationToken();


            emailService.sendConfirmationEmail(existingUser, confirmationLink);

            log.info("Email sent successfully " + confirmationLink);
        }

        ApiResponse genericResponse = new ApiResponse<>();
        genericResponse.setMessage("Token resent successfully, Please check your email to verify your account");
        genericResponse.setStatus("Success");
        genericResponse.setCode("00");
        genericResponse.setHttpStatus(HttpStatus.OK);
        return genericResponse;
    }
}
