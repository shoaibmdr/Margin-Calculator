package com.margincalculator.repository;

import com.margincalculator.entity.MarginMonitoringSnapshot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MarginMonitoringSnapshotRepository extends JpaRepository<MarginMonitoringSnapshot, Long> {

    Optional<MarginMonitoringSnapshot> findByClientId(String clientId);

    List<MarginMonitoringSnapshot> findAllByOrderByClientIdAsc();
}
