package com.angrybug.ysjd.DTO;

import jakarta.persistence.Column;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.sql.Date;

@Getter
@Setter
public class PatientInfo {

    private String name;

    private String birthdate;

    private Long genderNumber;

    private Long weight;

    private Long height;

    private String bloodPressure;

    private String pastDiseases;

    private String currentMedications;

    private String allergies;

    private String familyHistory;

    private String symptoms;

    private String onset;

    private Long painLevel;

}
