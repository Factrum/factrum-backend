package com.angrybug.ysjd.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Check;

@Entity
@Table(name = "simulation")
@Getter
@Setter
public class Simulation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long simulationId;

    @OneToOne(optional = true)
    @JoinColumn(name = "patientId")
    private Patient patientId;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String scenario;

}
