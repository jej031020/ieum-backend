package com.ieum.ieumbackend.constitution.domain;

import com.ieum.ieumbackend.common.domain.TimeStamped;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Table(name = "constitution")
@Entity
public class Constitution extends TimeStamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 사용자 ID (FK 용도)
    @Column(name = "user_id", nullable = false)
    private Long userId;

    // 검사 세션 ID
    @Column(name = "session_id", nullable = false)
    private Long sessionId;

    // 판별된 체질 타입
    @Enumerated(EnumType.STRING)
    @Column(name = "constitution_type", nullable = false, length = 20)
    private ConstitutionType constitutionType;

    // 판별 시각
    @Column(name = "analyzed_at", nullable = false)
    private LocalDateTime analyzedAt;   // 판별 시각

    public enum ConstitutionType {
        TAIYANG, TAEUM, SOYANG, SOEUM   // 태양인, 태음인, 소양인, 소음인
    }
}
