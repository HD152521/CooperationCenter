package com.cooperation.project.cooperationcenter.domain.file.model;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum FileTargetType {
    MEMBER("MEMBER","uploads/member/"),
    SCHOOL("SCHOOL","uploads/school/post/"),
    SURVEY("SURVEY","uploads/survey/");
    private final String fileType;
    private final String filePath;

    FileTargetType(String fileType,String filePath){
        this.fileType = fileType;
        this.filePath = filePath;
    }

    public static FileTargetType fromType(String fileType) {
        return Arrays.stream(FileTargetType.values())
                .filter(type -> type.getFileType().equalsIgnoreCase(fileType))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown fileType: " + fileType));
    }

}
