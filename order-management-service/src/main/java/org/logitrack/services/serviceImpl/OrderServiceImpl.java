package org.logitrack.services.serviceImpl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.logitrack.dto.request.OrderRequest;
import org.logitrack.dto.response.ApiResponse;
import org.logitrack.entities.Order;
import org.logitrack.entities.User;
import org.logitrack.enums.OrderProgress;
import org.logitrack.exceptions.OrderNotFoundException;
import org.logitrack.repository.OrderRepository;
import org.logitrack.repository.UserRepository;
import org.logitrack.services.OrderService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;

    @Value("${PRICING_COST_PER_KM}")
    private double costPerKilometer;

    @Value("${PRICING_FLAT_RATE_WEIGHT_THRESHOLD}")
    private double flatRateWeightThreshold;

    @Value("${PRICING_FLAT_RATE_COST}")
    private double flatRateCost;

    @Value("${PRICING_ADDITIONAL_COST_PER_KILOGRAM}")
    private double additionalCostPerKilogram;
    private static final double EARTH_RADIUS_KM = 6371.0; // Earth's radius in kilometers

    @Override
    public ApiResponse createOrder(OrderRequest request, String email) {
        log.info("checking if user exist");
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isEmpty()) {
            return new ApiResponse("00", "User not found", HttpStatus.NOT_FOUND);
        }
        Order order = new Order();
        order.setPickUpAddress(request.getPickUpAddress());
        order.setDeliveryAddress(request.getDeliveryAddress());
        order.setPackageDetails(request.getPackageInfo());
        order.setRecipientName(request.getRecipientName());
        order.setRecipientNumber(request.getRecipientNumber());
        order.setWeight(String.valueOf(request.getWeight()));
        LocalDate localDate = LocalDate.parse(request.getPickUpTime(), formatter);
        LocalTime localTime = LocalTime.of(0, 0); // midnight (00:00:00)
        // Combine LocalDate and LocalTime to create LocalDateTime
        LocalDateTime pickUpTime = LocalDateTime.of(localDate, localTime);
        order.setPickUpTime(pickUpTime);
        order.setInstruction(request.getInstruction());
        order.setOrderProgress(OrderProgress.NEW);
        order.setCreationTime(LocalDateTime.now());
        orderRepository.save(order);
        return new ApiResponse("00", "Order created successfully", HttpStatus.CREATED);
    }
    @Override
    public ApiResponse<Order> findOrderById(Long orderId) throws OrderNotFoundException {
        Optional<Order> orderOptional = orderRepository.findById(orderId);

        if (orderOptional.isPresent()) {
            Order order = orderOptional.get();
            return new ApiResponse<>("00", "Success", order, "OK");
        } else {
            throw new OrderNotFoundException("Order does not exist");
        }
    }
    public ApiResponse<Double> calculateOrderCost(OrderRequest request, String email, Long orderId) {
        createOrder(request, email);
        Optional<Order> createdOrder = orderRepository.findById(orderId);
        if (createdOrder.isPresent()) {
            double pickupLatitude = extractLatitude(request.getPickUpAddress());
            double pickupLongitude = extractLongitude(request.getPickUpAddress());
            double deliveryLatitude = extractLatitude(request.getDeliveryAddress());
            double deliveryLongitude = extractLongitude(request.getDeliveryAddress());

            log.info("Calculating order cost for pickup latitude: {}, longitude: {}", pickupLatitude, pickupLongitude);
            log.info("Calculating order cost for delivery latitude: {}, longitude: {}", deliveryLatitude, deliveryLongitude);

            double distance = calculateDistance(
                    pickupLatitude,
                    pickupLongitude,
                    deliveryLatitude,
                    deliveryLongitude
            );
            log.info("Calculated distance: {} km", distance);

            Double weightCost = calculateWeightCost(request.getWeight());
            log.info("Calculated weight cost: {}", weightCost);

            Double totalCost = distance * costPerKilometer + weightCost;
            log.info("Calculated total cost: {}", totalCost);
            return new ApiResponse<>("00", "Success", totalCost, "OK");
        } else {
            return new ApiResponse<>("404", "Order not created", null, "NOT_FOUND");
        }
    }
    private long calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        double lat1Rad = Math.toRadians(lat1);
        double lon1Rad = Math.toRadians(lon1);
        double lat2Rad = Math.toRadians(lat2);
        double lon2Rad = Math.toRadians(lon2);

        double longitude = lon2Rad - lon1Rad;
        double latitude = lat2Rad - lat1Rad;
        double a = Math.sin(latitude / 2) * Math.sin(latitude / 2) +
                Math.cos(lat1Rad) * Math.cos(lat2Rad) *
                        Math.sin(longitude / 2) * Math.sin(longitude / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        double distanceInKilometers = EARTH_RADIUS_KM * c;
        return Math.round(distanceInKilometers);
    }

    private Double calculateWeightCost(Long weight) {
        if (weight <= flatRateWeightThreshold) {
            return flatRateCost;
        } else {
            return flatRateCost + (weight - flatRateWeightThreshold) * additionalCostPerKilogram;
        }
    }
    private double extractLatitude(String address) {
        String[] location = address.split(",");
        return Double.parseDouble(location[0]);
    }
    private double extractLongitude(String address) {
        String[] location = address.split(",");
        return Double.parseDouble(location[1]);
    }
}
