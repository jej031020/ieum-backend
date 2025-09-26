package com.ieum.ieumbackend.bio_measurement.domain;

import com.ieum.ieumbackend.auth.domain.User;
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

    // User와의 다대일 관계 (N:1) - 여러 측정값이 한 유저에 속함
    @ManyToOne(fetch = FetchType.LAZY)  // 필요 시 EAGER로도 가능
    @JoinColumn(name = "user_id", nullable = false) // FK 지정
    private User user;

    // "한 번의 검사"를 구분하는 session_id
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