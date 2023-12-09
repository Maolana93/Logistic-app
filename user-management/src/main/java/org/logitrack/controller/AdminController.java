package org.logitrack.controller;

import lombok.AllArgsConstructor;
import org.logitrack.dto.request.DeliveryManCreationRequest;
import org.logitrack.dto.response.ApiResponse;
import org.logitrack.services.CustomerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RestController
@RequestMapping("api/v1/admin")
public class AdminController {
    private final CustomerService userService;

    @PostMapping("/create-delivery-man")
    public ResponseEntity<?> createDeliveryMan(@RequestBody DeliveryManCreationRequest request){
        ApiResponse response = userService.registerDeliveryMan(request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

}
