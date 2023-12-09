package org.logitrack.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.logitrack.enums.Status;

@Entity
@Table(name = "transactions")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String AddressDelivered;

    @Column(nullable = false)
    private String orderDate;

    @Column(nullable = false)
    private String recipientName;

    @Column(nullable = false)
    private Status transactionStatus;

    @Column(nullable = false)
    private Double amount;

    @Column
    private String transactionReference;
}
