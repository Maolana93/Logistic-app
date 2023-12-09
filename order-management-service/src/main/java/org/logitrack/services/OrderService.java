package org.logitrack.services;
import org.logitrack.dto.request.OrderRequest;
import org.logitrack.dto.response.ApiResponse;
import org.logitrack.entities.Order;
import org.logitrack.exceptions.OrderNotFoundException;
import org.springframework.stereotype.Service;


@Service
public interface OrderService {


    ApiResponse createOrder(OrderRequest request,String email);
    ApiResponse<Order> findOrderById(Long orderId) throws OrderNotFoundException;
    ApiResponse<Double> calculateOrderCost(OrderRequest request, String email, Long orderId);
}
