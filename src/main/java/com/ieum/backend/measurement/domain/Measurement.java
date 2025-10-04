package com.ieum.backend.measurement.domain;

import com.ieum.backend.auth.domain.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "measurement")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Measurement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // User.id를 FK로 참조
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

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

    // 입력 날짜/시간
    @CreationTimestamp
    private LocalDateTime createdAt;
}