package com.cooperation.project.cooperationcenter.domain.file.model;

public enum MemberFileType {
    PICTURE("picture"),
    CERTIFICATION("certicication");

    private final String fileType;

    MemberFileType(String fileType){this.fileType = fileType;}

    public String getFileType() {
        return fileType;
    }

    public static MemberFileType fromType(String type){
        for(MemberFileType type1 : values()){
            if(type1.fileType.equalsIgnoreCase(type)) return type1;
        }
        return null;
    }
}
