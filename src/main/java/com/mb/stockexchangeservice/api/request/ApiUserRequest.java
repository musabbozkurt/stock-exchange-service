package com.mb.stockexchangeservice.api.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiUserRequest {

    @Schema(description = "User first name", example = "Jack")
    private String firstName;

    @Schema(description = "User last name", example = "Sparrow")
    private String lastName;

    @Schema(description = "User username", example = "jack_sparrow")
    private String username;

    @Schema(description = "User password", example = "test1234")
    private String password;

    @Schema(description = "User email", example = "jack.sparrow@gmail.com")
    private String email;

    @Schema(description = "User phone number", example = "901233459867")
    private String phoneNumber;
}
