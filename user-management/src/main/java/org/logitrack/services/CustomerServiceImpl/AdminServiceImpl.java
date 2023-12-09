package org.logitrack.services.CustomerServiceImpl;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.logitrack.dto.request.AppUserLoginRequest;
import org.logitrack.dto.response.LoginResponse;
import org.logitrack.entities.Admin;
import org.logitrack.enums.Role;
import org.logitrack.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.net.UnknownServiceException;
import java.time.LocalDateTime;
import java.util.AbstractMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Configuration
@Slf4j
@Service

public class AdminServiceImpl {
    private UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AdminServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
       this.passwordEncoder=passwordEncoder;
        runAtStart();
    }

    public void runAtStart() {
        log.info("Creating admin");
        Admin admin = new Admin();
        admin.setEmail("AdminOne@gmail.com");
        admin.setPassword(passwordEncoder.encode("OneAdmin246"));
        admin.setRole(Role.ADMIN);
        admin.setCreationDate(LocalDateTime.now());
        admin.setLastLogin(LocalDateTime.now());
        admin.setPhoneNumber("09039156872");
        admin.setFullName("David Black");
        admin.setIsVerified(true);
        userRepository.save(admin);
    }


}
