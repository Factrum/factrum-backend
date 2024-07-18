package com.angrybug.ysjd.DTO;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SelectedSimulationResultRequest {
    private Long id;

    private Long explainType;
}
