package com.ieum.backend.measurement.repository;

import com.ieum.backend.measurement.domain.Measurement;
import com.ieum.backend.auth.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MeasurementRepository extends JpaRepository<Measurement, Long> {
    List<Measurement> findByUser(User user);
}
