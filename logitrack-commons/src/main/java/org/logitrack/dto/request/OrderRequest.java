package org.logitrack.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.logitrack.mapper.CustomLocalDateTimeDeserializer;

import java.time.LocalDateTime;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonDeserialize
public class OrderRequest {
    @NotBlank(message = "Pickup address can not be empty")
    @Pattern(
            regexp = "^-?[0-9]{1,2}(?:\\.[0-9]{1,6})?,\\s*-?[0-9]{1,3}(?:\\.[0-9]{1,6})?$",
            message = "Invalid pickup address format. Use latitude,longitude format."
    )
    private String pickUpAddress;

    @NotBlank(message = "Delivery address can not be empty")
    @Pattern(
            regexp = "^-?[0-9]{1,2}(?:\\.[0-9]{1,6})?,\\s*-?[0-9]{1,3}(?:\\.[0-9]{1,6})?$",
            message = "Invalid delivery address format. Use latitude,longitude format."
    )
    private String deliveryAddress;
    @NotBlank(message = "package Info can not be empty")
    private String packageInfo;
    @NotBlank(message = "Recipient name can not be empty")
    private String recipientName;
    @NotBlank(message = "Phone number is required")
    @Size( message = "Phone number length must be 11 digits")
    @Pattern(regexp = "^[0-9]+$", message = "Phone number must contain only numeric characters")
    private String recipientNumber;
    @NotNull(message = "Weight is required")
    @DecimalMin(value = "0.01", message = "Weight must be greater than or equal to 0.01 kg")
    @Digits(integer = 4, fraction = 2, message = "Invalid weight format")
    private Long weight;
    @NotBlank(message = "Date is required")
    @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}", message = "Invalid date format. Use yyyy-MM-dd.")
    private String pickUpTime;
    private String instruction;
}
