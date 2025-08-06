package com.cooperation.project.cooperationcenter.domain.school.dto;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum PostStatus {
    TEMPORARY("TEMPORARY"),
    PUBLISHED("PUBLISHED");

    private final String status;

    PostStatus(String status) {this.status = status;}

    public static PostStatus from(String status) {
        return Arrays.stream(PostStatus.values())
                .filter(t -> t.getStatus().equalsIgnoreCase(status))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid board type: " + status));
    }
}
