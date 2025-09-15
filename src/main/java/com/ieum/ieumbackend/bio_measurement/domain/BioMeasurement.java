package com.ieum.ieumbackend.bio_measurement.domain;

import com.ieum.ieumbackend.common.domain.TimeStamped;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Table(name = "bio_measurement")
@Entity
public class BioMeasurement extends TimeStamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    // "한 번의 검사"를 식별하는 외래키(FK) - 12개의 입력을 한 세트로
    @Column(name = "session_id", nullable = false)
    private Long sessionId;

    // 장기: 신장/비장/폐/심장/간/방광
    @Enumerated(EnumType.STRING)
    @Column(name = "organ", nullable = false)
    private Organ organ;

    // 좌/우 구분
    @Enumerated(EnumType.STRING)
    @Column(name = "side", nullable = false)
    private Side side;

    // 측정값
    @Column(name = "value", nullable = false)
    private Double value;

    // 측정 시각
    @Column(name = "measured_at", nullable = false)
    private LocalDateTime measuredAt;

    public enum Organ { KIDNEY, SPLEEN, LUNG, HEART, LIVER, BLADDER }
    public enum Side { LEFT, RIGHT }
}

