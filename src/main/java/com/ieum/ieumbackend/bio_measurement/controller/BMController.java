package com.ieum.ieumbackend.bio_measurement.controller;

import com.ieum.ieumbackend.auth.domain.PrincipalDetails;
import com.ieum.ieumbackend.auth.domain.User;
import com.ieum.ieumbackend.bio_measurement.dto.BMRequestDto;
import com.ieum.ieumbackend.bio_measurement.dto.BMResponseDto;
import com.ieum.ieumbackend.bio_measurement.service.BMService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class BMController {

    private final BMService bmService;

    // CREATE
    @PostMapping("/bio-measurements")
    public ResponseEntity<BMResponseDto> createMeasurement(
            @AuthenticationPrincipal PrincipalDetails principal,   // 로그인 유저
            @RequestBody @Valid BMRequestDto bmRequestDto
    ) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); // principal 없는 경우 401 반환
        }
        User user = principal.getUser();
        BMResponseDto created = bmService.createMeasurement(bmRequestDto, user);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // READ (List)
    @GetMapping("/bio-measurements")
    public ResponseEntity<List<BMResponseDto>> getMeasurements(
            @AuthenticationPrincipal PrincipalDetails principal,
            @RequestParam(required = false) Long sessionId
    ) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        User user = principal.getUser();
        List<BMResponseDto> list = bmService.getMeasurements(user, sessionId);
        return ResponseEntity.ok(list);
    }

    // READ (Single)
    @GetMapping("/bio-measurements/{id}")
    public ResponseEntity<BMResponseDto> getMeasurement(@PathVariable Long id) {
        return ResponseEntity.ok(bmService.getMeasurement(id));
    }

    // UPDATE
    @PutMapping("/bio-measurements/{id}")
    public ResponseEntity<BMResponseDto> updateMeasurement(
            @PathVariable Long id,
            @AuthenticationPrincipal PrincipalDetails principal,
            @RequestBody @Valid BMRequestDto bmRequestDto
    ) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        User user = principal.getUser();
        BMResponseDto updated = bmService.updateMeasurement(id, bmRequestDto, user);
        return ResponseEntity.ok(updated);
    }
}
