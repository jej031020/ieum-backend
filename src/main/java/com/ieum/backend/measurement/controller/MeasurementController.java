package com.ieum.backend.measurement.controller;

import com.ieum.backend.measurement.domain.Measurement;
import com.ieum.backend.measurement.dto.MeasurementRequest;
import com.ieum.backend.measurement.service.MeasurementService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/measurements")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class MeasurementController {

    private final MeasurementService measurementService;

    // 저장
    @PostMapping
    public Measurement createMeasurement(@RequestBody MeasurementRequest request) {
        return measurementService.saveMeasurement(request);
    }

    // 특정 사용자(userId)의 모든 기록 조회
    @GetMapping("/{userId}")
    public List<Measurement> getMeasurements(@PathVariable Long userId) {
        return measurementService.getMeasurements(userId);
    }

    // 특정 사용자(userId)의 측정한 날짜 리스트 조회
    @GetMapping("/{userId}/dates")
    public List<String> getMeasurementDates(@PathVariable Long userId) {
        return measurementService.getMeasurementDates(userId);
    }

    // 특정 사용자(userId)의 특정 날짜별 측정 기록 조회
    @GetMapping("/{userId}/by-date")
    public List<Measurement> getMeasurementsByDate(
            @PathVariable Long userId,
            @RequestParam String date) {
        return measurementService.getMeasurementsByDate(userId, date);
    }
}