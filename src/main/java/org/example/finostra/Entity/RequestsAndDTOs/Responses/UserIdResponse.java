package org.example.finostra.Entity.RequestsAndDTOs.Responses;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserIdResponse {
    private String publicUUID;
}
