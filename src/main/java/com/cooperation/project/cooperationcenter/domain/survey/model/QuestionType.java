package com.cooperation.project.cooperationcenter.domain.survey.model;

import com.cooperation.project.cooperationcenter.global.exception.BaseException;

import java.io.File;

public enum QuestionType {
    SHORT("SHORT"),
    ESSAY("ESSAY"),
    MULTIPLE("MULTIPLE"),
    MULTIPLECHECK("multiple-checkbox"),
    DROPDOWN("DROPDOWN"),
    DATE("DATE"),
    FILE("FILE"),
    IMAGE("IMAGE"),;

    private final String type;

    QuestionType(String type){
        this.type = type;
    }

    public String getType(){
        return this.type;
    }

    public static QuestionType fromType(String checkType){
        if(checkType.equals(("multiplecheck"))) return MULTIPLECHECK;
        for(QuestionType type : values()){
            if(type.type.equalsIgnoreCase(checkType)){
                return type;
            }
        }
        return null;
    }

    public static boolean checkType(QuestionType type){
        if(type.equals(MULTIPLE) || type.equals(MULTIPLECHECK) || type.equals(DROPDOWN)) return true;
        return false;
    }

    public static boolean isText(QuestionType type){
        if(type.equals(SHORT) || type.equals(ESSAY)){
            return true;
        }
        return false;
    }

    public static boolean isDate(QuestionType type){
        return type.equals(DATE);
    }

    public static boolean isDate(String type){
        return DATE.type.equalsIgnoreCase(type);
    }

    public static boolean isFile(QuestionType type){
        return type.equals(FILE) || type.equals(IMAGE) ;
    }

    public static boolean isFile(String type){
        return FILE.type.equalsIgnoreCase(type) || IMAGE.type.equalsIgnoreCase(type);
    }
}
