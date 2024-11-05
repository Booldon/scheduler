package com.example.scheduler.controller;

import com.example.scheduler.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class AuthCheckController {

    @PostMapping("/check")
    public ResponseEntity<User> checkAuth() {

        return ResponseEntity.ok().build();
    }
}
