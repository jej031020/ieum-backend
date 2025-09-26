package com.ieum.ieumbackend.bio_measurement.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class BMRequestDto {

    private Long userId;
    private Long sessionId;
    private String organ;
    private String side;
    private Double value;
    private LocalDateTime measuredAt;
}
