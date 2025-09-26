package com.ieum.ieumbackend.bio_measurement.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ieum.ieumbackend.bio_measurement.domain.BioMeasurement;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class BMResponseDto {

    private Long id;
    private Long userId;
    private Long sessionId;
    private String organ;
    private String side;
    private Double value;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime measuredAt;

    public BMResponseDto(BioMeasurement bm) {
        this.id = bm.getId();
        this.userId = bm.getUser().getId();
        this.sessionId = bm.getSessionId();
        this.organ = bm.getOrgan().name();
        this.side = bm.getSide().name();
        this.value = bm.getValue();
        this.measuredAt = bm.getMeasuredAt();
    }
}