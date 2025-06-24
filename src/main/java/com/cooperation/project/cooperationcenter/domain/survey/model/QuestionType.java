package com.cooperation.project.cooperationcenter.domain.survey.model;

import com.cooperation.project.cooperationcenter.global.exception.BaseException;

public enum QuestionType {
    SHORT("SHORT"),
    PARAGRAPH("PARAGRAPH"),
    ESSAY("ESSAY"),
    MULTIPLE("MULTIPLE"),
    MULTIPLECHECK("multiple-checkbox"),
    DROPDOWN("DROPDOWN"),
    DATE("DATE"),
    FILE("FILE");

    private final String type;

    QuestionType(String type){
        this.type = type;
    }

    public String getType(){
        return this.type;
    }

    public static QuestionType fromType(String checkType){
        for(QuestionType type : values()){
            if(type.type.equalsIgnoreCase(checkType)){
                return type;
            }
        }
        return null;
    }

    public static boolean checkType(QuestionType type){
        if(type.equals(QuestionType.MULTIPLE) || type.equals(QuestionType.MULTIPLECHECK) || type.equals(QuestionType.DROPDOWN)) return true;
        return false;
    }
}
