package org.logitrack.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.logitrack.enums.Gender;
import org.logitrack.enums.Role;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class User  implements Serializable {

    private static  final long serialVersionUID=1l;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String fullName;

    @Column(nullable = false)
    private String email;

    //@Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String phoneNumber;

    private Gender gender;

    private String city;

    private String address;

    private String drivingLicenseNumber;

    private String State;

    @JsonFormat(pattern="yyyy-MM-dd")
    private LocalDate birthday;


    @Column
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime lastLogin;

    @Column
    @CreationTimestamp
    private LocalDateTime creationDate;

    @Column
    @UpdateTimestamp
    private LocalDateTime updatedDate;

    @Enumerated(EnumType.STRING)
    private Role role;
    @OneToOne
    @JoinColumn(name = "verification_token_id")
    private VerificationToken verificationToken;
    @Column
    private String uuid;

    private Boolean isVerified = false;
}
