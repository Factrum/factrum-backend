package com.angrybug.ysjd.Repository;

import com.angrybug.ysjd.Entity.Simulation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SimulationRepository extends JpaRepository<Simulation, Long> {

    @Modifying
    @Query(value = "SELECT * FROM simulation s WHERE s.patient_id = :id", nativeQuery = true)
    List<Simulation> findByPatientId(Long id);
}
