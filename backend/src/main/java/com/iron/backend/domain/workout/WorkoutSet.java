package com.iron.backend.domain.workout;

import com.iron.backend.domain.exercise.Exercise;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AccessLevel;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "workout_sets", indexes = {
    @Index(name = "idx_sets_session_exercise", columnList = "session_id, exercise_id")
})
@Getter
@Setter
@NoArgsConstructor
public class WorkoutSet {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "set_id")
    private UUID setId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false)
    private WorkoutSession workoutSession;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exercise_id", nullable = false)
    private Exercise exercise;

    @Column(name = "set_order", nullable = false)
    private Integer setOrder;

    @Column(nullable = false)
    private Double weight = 0.0;

    @Column(nullable = false)
    private Integer reps = 0;

    // Calculated volume (weight * reps)
    // We can calculate this in application or let DB do it. 
    // Implementing simple calculation in setter or PrePersist/PreUpdate for consistency.
    @Column(name = "volume")
    private Double volume;

    @Column(name = "is_warmup")
    private Boolean isWarmup = false;

    @Column(name = "rpe")
    private Integer rpe;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    @PreUpdate
    public void calculateVolume() {
        if (this.weight != null && this.reps != null) {
            this.volume = this.weight * this.reps;
        } else {
            this.volume = 0.0;
        }
    }
}
