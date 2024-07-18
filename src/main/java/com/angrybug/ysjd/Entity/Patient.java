package com.angrybug.ysjd.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Check;
import org.hibernate.annotations.ColumnDefault;

import java.sql.Date;

@Entity
@Table(name = "patient")
@Check(constraints = "sex IN ('male', 'female')")
@Getter
@Setter
public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long patientId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Date birthdate;

    @Column(nullable = false)
    private String sex;

    @Column(nullable = false)
    private Long weight;

    @Column(nullable = false)
    private Long height;
    ;
    @Column(nullable = false)
    @ColumnDefault("'0'")
    private String bloodPressure;

    @Column(nullable = false)
    @ColumnDefault("'None'")
    private String pastDiseases;

    @Column(nullable = false)
    @ColumnDefault("'None'")
    private String currentMedications;

    @Column(nullable = false)
    @ColumnDefault("'None'")
    private String allergies;

    @Column(nullable = false)
    @ColumnDefault("'None'")
    private String familyHistory;

    @Column(nullable = false)
    private String symptoms;

    @Column(nullable = false)
    private String onset;

    @Column(nullable = false)
    @ColumnDefault("'0'")
    private Long painLevel;

    public Patient() {

    }

}
