package com.cooperation.project.cooperationcenter.global.token.vo;

public record AccessToken(
        String token
) {
    public static AccessToken of(String token) {
        return new AccessToken(token);
    }
}