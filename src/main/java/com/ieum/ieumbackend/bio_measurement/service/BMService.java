package com.ieum.ieumbackend.bio_measurement.service;

import com.ieum.ieumbackend.auth.domain.User;
import com.ieum.ieumbackend.bio_measurement.dto.BMRequestDto;
import com.ieum.ieumbackend.bio_measurement.dto.BMResponseDto;

import java.util.List;

public interface BMService {
    BMResponseDto createMeasurement(BMRequestDto request, User user);
    BMResponseDto getMeasurement(Long id);
    List<BMResponseDto> getMeasurements(User user, Long sessionId);
    BMResponseDto updateMeasurement(Long id, BMRequestDto request, User user);
}
