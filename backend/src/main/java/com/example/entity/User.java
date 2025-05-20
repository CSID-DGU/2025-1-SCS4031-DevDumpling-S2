package com.example.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import jakarta.persistence.*;
import org.hibernate.annotations.ColumnDefault;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Table(name = "user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String kakaoId;

    @Column(nullable = false)
    private String nickname;

    @JsonProperty("profileImage")
    @Column(name = "profile_image", nullable = true)
    private String profileImage;

    @Column(name = "mydata_consent")
    @ColumnDefault("false")
    private boolean myDataConsent;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_type", nullable = true)
    private UserType userType;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public boolean isMyDataConsent() {
        return myDataConsent;
    }

    public void setMyDataConsent(boolean myDataConsent) {
        this.myDataConsent = myDataConsent;
    }

    public enum UserType {
        A, B, C, D
    }
}
