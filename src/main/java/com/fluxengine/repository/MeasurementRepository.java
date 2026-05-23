package com.fluxengine.repository;

import com.fluxengine.model.Measurement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface MeasurementRepository extends JpaRepository<Measurement, Long>, JpaSpecificationExecutor<Measurement> {
    List<Measurement> findTop100ByOrderByCreatedAtDesc();
    List<Measurement> findTop1ByOrderByCreatedAtDesc();
    Page<Measurement> findByDeviceIdContainingIgnoreCase(String deviceId, Pageable pageable);
}
