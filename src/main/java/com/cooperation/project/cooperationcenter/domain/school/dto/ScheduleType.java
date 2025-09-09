package com.cooperation.project.cooperationcenter.domain.school.dto;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum ScheduleType {
    ACADEMIC("ACADEMIC"),
    ADMISSION("ADMISSION"),
    SCHOLARSHIP("SCHOLARSHIP"),
    DORMITORY("DORMITORY"),
    INTERNATIONAL("INTERNATIONAL");

    private final String type;

    ScheduleType(String type){
        this.type = type;
    }

    public static ScheduleType from(String type) {
        return Arrays.stream(ScheduleType.values())
                .filter(t -> t.getType().equalsIgnoreCase(type))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid board type: " + type));
    }
}
