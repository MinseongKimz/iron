package com.iron.backend.domain.exercise;

import com.iron.backend.domain.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AccessLevel;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "exercises", indexes = {
    // Index for synonym search optimization might need GIN index in Postgres, 
    // but for now standard index on name is enough. 
    // Synonyms logic will likely be handled by application/native query if array.
})
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Exercise {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "exercise_id")
    private Integer exerciseId;

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Column(name = "main_category", nullable = false, length = 50)
    private String mainCategory; // CHEST, BACK, etc.

    @Column(name = "sub_category", length = 50)
    private String subCategory;

    // Mapping synonyms as a collection of strings
    @ElementCollection
    @CollectionTable(name = "exercise_synonyms", joinColumns = @JoinColumn(name = "exercise_id"))
    @Column(name = "synonym")
    private List<String> synonyms = new ArrayList<>();

    @Column(name = "is_compound")
    private Boolean isCompound = false;

    @Column(name = "exercise_type", length = 20)
    private String exerciseType = "WEIGHT";

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_user_id")
    private User createdByUser; // Nullable (System default exercises have null)

    public Exercise(String name, String mainCategory) {
        this.name = name;
        this.mainCategory = mainCategory;
    }
}
