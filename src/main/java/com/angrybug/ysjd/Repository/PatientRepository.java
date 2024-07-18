package com.angrybug.ysjd.Repository;

import com.angrybug.ysjd.DTO.BriefPatientInfo;
import com.angrybug.ysjd.Entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {

    List<Patient> findAll();

}


