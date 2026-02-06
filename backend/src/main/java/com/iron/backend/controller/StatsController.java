package com.iron.backend.controller;

import com.iron.backend.dto.StatsDto;
import com.iron.backend.service.StatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/stats")
@RequiredArgsConstructor
public class StatsController {

    private final StatsService statsService;

    @GetMapping("/dashboard")
    public StatsDto getDashboardStats(@RequestParam Long userId) {
        return statsService.getDashboardStats(userId);
    }
}
