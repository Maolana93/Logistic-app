package org.logitrack.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.logitrack.enums.OrderProgress;
import org.logitrack.enums.Status;

import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String pickUpAddress;
    @Column(nullable = false)
    private String deliveryAddress;
    @Column(nullable = false)
    private String packageDetails;

    @Column(nullable = false)
    private String recipientName;

    @Column(nullable = false)
    private String recipientNumber;

    @Column(nullable = false)
    private String weight;

    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime creationTime;

    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime pickUpTime;

    @Column(nullable = false)
    private String instruction;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private OrderProgress orderProgress;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @ManyToOne
    @JoinColumn(name = "delivery_man_id")
    private DeliveryMan deliveryMan;
    @Column
    private String orderBatchId;
}
