package org.logitrack.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.logitrack.dto.request.OrderRequest;
import org.logitrack.dto.response.ApiResponse;
import org.logitrack.entities.Order;
import org.logitrack.exceptions.CommonApplicationException;
import org.logitrack.exceptions.OrderNotFoundException;
import org.logitrack.services.OrderService;
import org.logitrack.utils.Utils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
@Slf4j
@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/api/v1/order")
public class OrderController {
    private final OrderService orderService;

    @PostMapping("/create-order")
    public ResponseEntity<ApiResponse> createOrder(@Valid @RequestBody OrderRequest request,  @RequestHeader("Authorization") String authorizationHeader) throws CommonApplicationException {
        var userDetails=Utils.validateTokenAndReturnDetail(authorizationHeader.substring(7));
        log.info("request for customer {} to create order", userDetails.get("name"));
        ApiResponse apiResponse = orderService.createOrder(request, (String) userDetails.get("email"));
        return new ResponseEntity<>(apiResponse, HttpStatus.CREATED);
    }
    @PostMapping("/calculate-order-cost")
    public ResponseEntity<ApiResponse<Double>> calculateOrderCost(
            @RequestBody @Valid OrderRequest request,
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestParam("orderId") Long orderId
    ) throws CommonApplicationException {
        var userDetails = Utils.validateTokenAndReturnDetail(authorizationHeader.substring(7));
        log.info("Request for customer {} to calculate order", userDetails.get("name"));
        ApiResponse<Double> apiResponse = orderService.calculateOrderCost(request, (String) userDetails.get("email"), orderId);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }
    @GetMapping("/{orderId}")
    public ResponseEntity<ApiResponse<Order>> viewOrderById(
            @PathVariable Long orderId,
            @RequestHeader("Authorization") String authorizationHeader
    ) throws OrderNotFoundException, CommonApplicationException {
        var userDetails = Utils.validateTokenAndReturnDetail(authorizationHeader.substring(7));
        log.info("User {} is viewing order with ID {}", userDetails.get("name"), orderId);
        ApiResponse<Order> order = orderService.findOrderById(orderId);
        return new ResponseEntity<>(order, HttpStatus.FOUND);
    }

}