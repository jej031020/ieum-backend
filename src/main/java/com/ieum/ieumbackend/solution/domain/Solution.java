package com.ieum.ieumbackend.solution.domain;

import com.ieum.ieumbackend.common.domain.TimeStamped;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Table(name = "solution")
@Entity
public class Solution extends TimeStamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 사용자
    @Column(name = "user_id", nullable = false)
    private Long userId;

    // 체질 결과 ID (Constitution)
    @Column(name = "constitution_id", nullable = false)
    private Long constitutionId;

    // 체질 타입
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 30)
    private SolutionType type;

    // 솔루션 내용
    @Column(name = "content", columnDefinition = "TEXT", nullable = false)
    private String content;

    public enum SolutionType { DIET, EXERCISE, LIFESTYLE }
}