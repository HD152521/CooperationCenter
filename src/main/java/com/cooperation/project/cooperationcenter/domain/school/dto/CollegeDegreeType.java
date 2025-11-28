package com.cooperation.project.cooperationcenter.domain.school.dto;

public enum CollegeDegreeType {
    UNDERGRADUATE("학사"),
    GRADUATE("석/박사");


    private final String label;

    CollegeDegreeType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
