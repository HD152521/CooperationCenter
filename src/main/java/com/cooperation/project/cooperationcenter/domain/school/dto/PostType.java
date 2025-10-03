package com.cooperation.project.cooperationcenter.domain.school.dto;

import com.cooperation.project.cooperationcenter.domain.school.model.SchoolPost;
import lombok.Getter;

import java.util.Arrays;

@Getter
public enum PostType {
    NORMAL("NORMAL"),
    NOTICE("NOTICE");
    private final String type;
    PostType(String type) {this.type = type;}

    public static PostType from(String type) {
        return Arrays.stream(PostType.values())
                .filter(t -> t.getType().equalsIgnoreCase(type))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid board type: " + type));
    }
}