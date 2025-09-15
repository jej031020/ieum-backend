package com.ieum.ieumbackend.qscc.domain;

import com.ieum.ieumbackend.common.domain.TimeStamped;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Table(name = "qscc")
@Entity
public class QSCC extends TimeStamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 설문 사용자
    @Column(name = "user_id", nullable = false)
    private Long userId;

    // "한 번의 검사"를 식별하는 외래키(FK) - 해당 세션의 설문
    @Column(name = "session_id", nullable = false)
    private Long sessionId;

    // 설문 응답 (JSON 문자열 권장)
    @Column(name = "answers", columnDefinition = "TEXT", nullable = false)
    private String answers;

    // 설문 제출 시각
    @Column(name = "submitted_at", nullable = false)
    private LocalDateTime submittedAt;

}
