package com.cooperation.project.cooperationcenter.domain.student.dto;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum Gender {
    MALE("MALE"),
    FEMALE("FEMALE");

    private final String gender;

    Gender(String gender){this.gender = gender;}

    public static Gender from(String gender){
        return Arrays.stream(Gender.values())
                .filter(t -> t.getGender().equalsIgnoreCase(gender))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid board type: " + gender));
    }

}
