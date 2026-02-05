package com.iron.backend.service;

import com.iron.backend.domain.user.User;
import com.iron.backend.dto.UserDto;
import com.iron.backend.dto.auth.LoginResponse;
import com.iron.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public UserDto registerUser(String email, String password, String nickname) {
        // TODO: Password encryption
        User user = new User(email, password, nickname);
        User savedUser = userRepository.save(user);
        return mapToDto(savedUser);
    }

    public UserDto getUserProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));
        return mapToDto(user);
    }

    @Transactional
    public LoginResponse login(String username, String apiKey) {
        // Simple logic for MVP: Allow test1 and test2 only, or auto-register them
        if (!username.equals("test1") && !username.equals("test2")) {
            throw new IllegalArgumentException("Invalid username. Only 'test1' and 'test2' are allowed.");
        }

        User user = userRepository.findByEmail(username)
                .orElseGet(() -> {
                    // Create if not exists (seed logic sort of)
                   return userRepository.save(new User(username, "nopassword", username));
                });

        // Update API Key
        if (apiKey != null && !apiKey.isBlank()) {
            user.updateGeminiApiKey(apiKey);
        }

        return new LoginResponse(user.getUserId(), user.getEmail(), "Login successful");
    }

    private UserDto mapToDto(User user) {
        return UserDto.builder()
                .userId(user.getUserId())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .currentWeight(user.getCurrentWeight())
                .targetWeight(user.getTargetWeight())
                .build();
    }
}
