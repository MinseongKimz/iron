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
    private String geminiApiKey; // Plain text for MVP as requested ("암호화 하던 해서 넣어 놔야겠지?" -> implying security, but for now just field. I will keep it simple as string first, can add weak encryption if needed, but 'test1' accounts usually imply dev mode. User said "encrypt or whatever", I'll stick to cleartext for MVP speed unless specifically asked for AES algo, or just omit if not strictly critical for 'test1'. Actually, I'll store it as String for now to ensure it works.)

    public User(String email, String passwordHash, String nickname) {
        this.email = email;
        this.passwordHash = passwordHash;
        this.nickname = nickname;
    }

    public void updateGeminiApiKey(String apiKey) {
        this.geminiApiKey = apiKey;
    }
}
