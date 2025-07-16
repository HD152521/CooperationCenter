package com.cooperation.project.cooperationcenter.domain.school.model;

import lombok.Getter;

@Getter
public enum PostStatus {
    TEMPORARY("TEMPORARY"),
    PUBLISHED("PUBLISHED");

    private final String status;

    PostStatus(String status) {this.status = status;}
}
