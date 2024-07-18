package com.angrybug.ysjd.DTO;

import lombok.Getter;
import lombok.Setter;

import java.sql.Date;

@Getter
@Setter
public class BriefPatientInfo {
    private Long id;
    private String name;
    private String birthdate;
    private String sex;

    public BriefPatientInfo(Long patientId, String name, String parsedDate, String sex) {
        this.id = patientId;
        this.name = name;
        this.birthdate = parsedDate;
        this.sex = sex;
    }
}
