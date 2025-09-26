package com.ieum.ieumbackend.bio_measurement.service;

import com.ieum.ieumbackend.auth.domain.User;
import com.ieum.ieumbackend.bio_measurement.domain.BioMeasurement;
import com.ieum.ieumbackend.bio_measurement.dto.BMRequestDto;
import com.ieum.ieumbackend.bio_measurement.dto.BMResponseDto;
import com.ieum.ieumbackend.bio_measurement.repository.BMRepository;

import jakarta.persistence.EntityNotFoundException;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BMServiceImpl implements BMService {

    private final BMRepository bmRepository;

    @Override
    @Transactional
    public BMResponseDto createMeasurement(BMRequestDto request, User user) {
        BioMeasurement entity = new BioMeasurement();

        // 로그인한 User 설정
        entity.setUser(user);

        entity.setSessionId(requireNonNull(request.getSessionId(), "sessionId"));
        entity.setOrgan(parseOrgan(requireNonNull(request.getOrgan(), "organ")));
        entity.setSide(parseSide(requireNonNull(request.getSide(), "side")));
        entity.setValue(requireNonNull(request.getValue(), "value"));

        LocalDateTime measuredAt = request.getMeasuredAt() != null
                ? request.getMeasuredAt()
                : LocalDateTime.now();
        entity.setMeasuredAt(measuredAt);

        // 중복 방지 체크
        if (bmRepository.existsBySessionIdAndOrganAndSide(
                entity.getSessionId(), entity.getOrgan(), entity.getSide())) {
            throw new IllegalArgumentException(
                    "이미 존재하는 측정값입니다: sessionId=" + entity.getSessionId()
                            + ", organ=" + entity.getOrgan()
                            + ", side=" + entity.getSide());
        }

        BioMeasurement saved = bmRepository.save(entity);
        return new BMResponseDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public BMResponseDto getMeasurement(Long id) {
        BioMeasurement found = bmRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("BioMeasurement not found: id=" + id));
        return new BMResponseDto(found);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BMResponseDto> getMeasurements(User user, Long sessionId) {
        List<BioMeasurement> list;
        if (sessionId != null) {
            list = bmRepository.findByUserAndSessionIdOrderByMeasuredAtDesc(user, sessionId);
        } else {
            list = bmRepository.findByUserOrderByMeasuredAtDesc(user);
        }
        return list.stream().map(BMResponseDto::new).toList();
    }

    @Override
    @Transactional
    public BMResponseDto updateMeasurement(Long id, BMRequestDto request, User user) {
        BioMeasurement entity = bmRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("BioMeasurement not found: id=" + id));

        // 해당 측정값이 로그인한 유저의 것인지 확인 (보안)
        if (!entity.getUser().getId().equals(user.getId())) {
            throw new SecurityException("본인 측정값만 수정할 수 있습니다.");
        }

        Long newSessionId = entity.getSessionId();
        BioMeasurement.Organ newOrgan = entity.getOrgan();
        BioMeasurement.Side newSide = entity.getSide();

        boolean keyChanged = false;
        if (request.getSessionId() != null) { newSessionId = request.getSessionId(); keyChanged = true; }
        if (request.getOrgan() != null)      { newOrgan     = parseOrgan(request.getOrgan()); keyChanged = true; }
        if (request.getSide() != null)       { newSide      = parseSide(request.getSide());   keyChanged = true; }

        if (keyChanged && bmRepository.existsBySessionIdAndOrganAndSideAndIdNot(
                newSessionId, newOrgan, newSide, id)) {
            throw new IllegalArgumentException(
                    "이미 존재하는 측정값입니다: sessionId=" + newSessionId
                            + ", organ=" + newOrgan
                            + ", side=" + newSide);
        }

        if (request.getSessionId() != null) entity.setSessionId(newSessionId);
        if (request.getOrgan() != null) entity.setOrgan(newOrgan);
        if (request.getSide() != null) entity.setSide(newSide);
        if (request.getValue() != null) entity.setValue(request.getValue());
        if (request.getMeasuredAt() != null) entity.setMeasuredAt(request.getMeasuredAt());

        BioMeasurement updated = bmRepository.save(entity);
        return new BMResponseDto(updated);
    }

    // ===== Helpers =====

    private static <T> T requireNonNull(T value, String field) {
        if (value == null) throw new IllegalArgumentException("필수 입력 값이 누락되었습니다: " + field);
        return value;
    }

    private BioMeasurement.Organ parseOrgan(String raw) {
        try {
            return BioMeasurement.Organ.valueOf(raw.trim().toUpperCase());
        } catch (Exception e) {
            throw new IllegalArgumentException("유효하지 않은 organ 값: " + raw);
        }
    }

    private BioMeasurement.Side parseSide(String raw) {
        try {
            return BioMeasurement.Side.valueOf(raw.trim().toUpperCase());
        } catch (Exception e) {
            throw new IllegalArgumentException("유효하지 않은 side 값: " + raw);
        }
    }
}
