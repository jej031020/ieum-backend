package com.ieum.ieumbackend.bio_measurement.repository;

import com.ieum.ieumbackend.auth.domain.User;
import com.ieum.ieumbackend.bio_measurement.domain.BioMeasurement;
import com.ieum.ieumbackend.bio_measurement.domain.BioMeasurement.Organ;
import com.ieum.ieumbackend.bio_measurement.domain.BioMeasurement.Side;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BMRepository extends JpaRepository<BioMeasurement, Long> {

    // 특정 유저의 모든 측정값을 최신순 조회
    List<BioMeasurement> findByUserOrderByMeasuredAtDesc(User user);

    // 특정 유저 + 세션의 측정값을 최신순 조회
    List<BioMeasurement> findByUserAndSessionIdOrderByMeasuredAtDesc(User user, Long sessionId);

    // 전체 측정값을 최신순으로 조회
    List<BioMeasurement> findAllByOrderByMeasuredAtDesc();

    // 중복 검사 (생성 시): 같은 세션 + organ + side 존재 여부
    boolean existsBySessionIdAndOrganAndSide(Long sessionId, Organ organ, Side side);

    // 중복 검사 (업데이트 시): 같은 세션 + organ + side 조합이 자기 자신 제외하고 존재 여부
    boolean existsBySessionIdAndOrganAndSideAndIdNot(Long sessionId, Organ organ, Side side, Long id);
}
