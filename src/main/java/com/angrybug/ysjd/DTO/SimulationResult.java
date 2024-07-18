package com.angrybug.ysjd.DTO;

import com.angrybug.ysjd.Entity.Patient;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class SimulationResult {

    private Long id;

    private JsonNode scenario;
}
