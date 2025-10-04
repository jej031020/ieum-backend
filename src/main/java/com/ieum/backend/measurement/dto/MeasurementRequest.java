package com.ieum.backend.measurement.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MeasurementRequest {
    private Long userId;   // ✅ 어떤 사용자인지 명시 (User.id)

    private Integer leftKidney;
    private Integer rightKidney;
    private Integer leftSpleen;
    private Integer rightSpleen;
    private Integer leftLung;
    private Integer rightLung;
    private Integer leftHeart;
    private Integer rightHeart;
    private Integer leftLiver;
    private Integer rightLiver;
    private Integer leftBladder;
    private Integer rightBladder;
}