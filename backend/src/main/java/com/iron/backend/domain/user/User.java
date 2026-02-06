package com.iron.backend.domain.user;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AccessLevel;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(nullable = false, length = 50)
    private String nickname;

    @Column(name = "current_weight")
    private Double currentWeight;

    @Column(name = "target_weight")
    private Double targetWeight;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "gemini_api_key")
    private String geminiApiKey;

    @Column(name = "current_streak")
    private Integer currentStreak = 0;

    @Column(name = "last_workout_date")
    private java.time.LocalDate lastWorkoutDate;

    public User(String email, String passwordHash, String nickname) {
        this.email = email;
        this.passwordHash = passwordHash;
        this.nickname = nickname;
    }

    public void updateGeminiApiKey(String apiKey) {
        this.geminiApiKey = apiKey;
    }

    public void updateStreak(boolean isConsecutive) {
        if (currentStreak == null) currentStreak = 0;
        
        if (isConsecutive) {
            this.currentStreak++;
        } else {
            this.currentStreak = 1;
        }
        this.lastWorkoutDate = java.time.LocalDate.now(java.time.ZoneId.of("Asia/Seoul"));
    }
}
