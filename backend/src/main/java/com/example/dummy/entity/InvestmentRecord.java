package com.example.dummy.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.hibernate.annotations.ColumnDefault;

@Entity
@Table(name = "investment_records")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InvestmentRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false, unique = true)
    private String accountNumber;

    @Column(nullable = false)
    private String accountName;

    @Column(nullable = false)
    private String investmentType;

    @Column(nullable = false)
    private LocalDate openDate;

    @Column(nullable = false)
    private Long balance;

    @Column(nullable = false)
    private LocalDate recentTransactionDate;

    @Column(nullable = false)
    private Long recentAmount;

    @Column(name = "recent_stock")
    private String recentStock;

    @Column(name = "is_active")
    @ColumnDefault("false")
    private boolean isActive;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public void setIsActive(boolean isActive) {
        this.isActive = isActive;
    }

    public boolean getIsActive() {
        return isActive;
    }
} 