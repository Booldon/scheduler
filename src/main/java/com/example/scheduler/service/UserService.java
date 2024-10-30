package com.example.scheduler.service;

import com.example.scheduler.DTO.UserDTO;
import com.example.scheduler.config.context.ResourceContext;
import com.example.scheduler.domain.User;
import com.example.scheduler.repository.UserRepository;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public User updateUser(UserDTO userDTO) {

        UserDetails userDetails = ResourceContext.getUserContext();

        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setUsername(userDTO.getName());
        user.setEmail(userDTO.getEmail());
        user.setPassword(userDetails.getPassword());
        user.setUpdatedAt(LocalDateTime.now());
        return userRepository.save(user);
    }

    public String deleteUser() {
        UserDetails userDetails = ResourceContext.getUserContext();

        String deleteUsername = userDetails.getUsername();

        User user = userRepository.findByUsername(deleteUsername)
                .orElseThrow(() -> new RuntimeException("User not found"));

        userRepository.delete(user);

        return deleteUsername;


    }


}
