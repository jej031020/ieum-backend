package com.ieum.backend.measurement.service;

import com.ieum.backend.measurement.domain.Measurement;
import com.ieum.backend.auth.domain.User;
import com.ieum.backend.measurement.dto.MeasurementRequest;
import com.ieum.backend.measurement.repository.MeasurementRepository;
import com.ieum.backend.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MeasurementService {

    private final MeasurementRepository measurementRepository;
    private final UserRepository userRepository;

    // 저장
    public Measurement saveMeasurement(MeasurementRequest dto) {
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Measurement measurement = Measurement.builder()
                .user(user)
                .leftKidney(dto.getLeftKidney())
                .rightKidney(dto.getRightKidney())
                .leftSpleen(dto.getLeftSpleen())
                .rightSpleen(dto.getRightSpleen())
                .leftLung(dto.getLeftLung())
                .rightLung(dto.getRightLung())
                .leftHeart(dto.getLeftHeart())
                .rightHeart(dto.getRightHeart())
                .leftLiver(dto.getLeftLiver())
                .rightLiver(dto.getRightLiver())
                .leftBladder(dto.getLeftBladder())
                .rightBladder(dto.getRightBladder())
                .build();

        return measurementRepository.save(measurement);
    }

    // 조회
    public List<Measurement> getMeasurements(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return measurementRepository.findByUser(user);
    }

    // 특정 사용자 → 기록한 날짜만 반환
    public List<String> getMeasurementDates(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return measurementRepository.findByUser(user).stream()
                .map(m -> m.getCreatedAt().toLocalDate())   // LocalDate 변환
                .map(LocalDate::toString)                  // yyyy-MM-dd
                .distinct()
                .toList();
    }

    // 특정 날짜의 기록 반환
    public List<Measurement> getMeasurementsByDate(Long userId, String date) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // ✅ yyyy-MM-dd 포맷 강제
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate targetDate = LocalDate.parse(date, formatter);

        return measurementRepository.findByUser(user).stream()
                .filter(m -> m.getCreatedAt().toLocalDate().isEqual(targetDate))
                .toList();
    }
}