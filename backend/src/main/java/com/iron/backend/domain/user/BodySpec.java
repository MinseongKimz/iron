package com.iron.backend.domain.user;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AccessLevel;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "body_specs", indexes = {
    @Index(name = "idx_body_specs_user_date", columnList = "user_id, recorded_date")
})
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BodySpec {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "spec_id")
    private Long specId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "recorded_date", nullable = false)
    private LocalDate recordedDate;

    private Double weight;

    @Column(name = "skeletal_muscle_mass")
    private Double skeletalMuscleMass;

    @Column(name = "body_fat_percentage")
    private Double bodyFatPercentage;

    @Column(columnDefinition = "TEXT")
    private String note;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    public BodySpec(User user, LocalDate recordedDate, Double weight) {
        this.user = user;
        this.recordedDate = recordedDate;
        this.weight = weight;
    }
}
