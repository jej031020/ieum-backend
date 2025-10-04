package com.ieum.backend.auth.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AuthResponseDto {

    private String userId;
    private String accessToken;

    public AuthResponseDto(String userId, String userName, String accessToken) {
        this.userId = userId;
        this.accessToken = accessToken;
    }

    public AuthResponseDto(String userId, String accessToken) {
        this.userId = userId;
        this.accessToken = accessToken;
    }
}
