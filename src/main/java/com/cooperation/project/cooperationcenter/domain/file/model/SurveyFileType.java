package com.cooperation.project.cooperationcenter.domain.file.model;

public enum SurveyFileType {
    IMAGE("image"),
    FILE("file");

    private final String fileType;

    SurveyFileType(String fileType){this.fileType = fileType;}

    public String getFileType() {
        return fileType;
    }

    public static SurveyFileType fromType(String type){
        for(SurveyFileType type1 : values()){
            if(type1.fileType.equalsIgnoreCase(type)) return type1;
        }
        return null;
    }
}
