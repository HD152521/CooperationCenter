package com.cooperation.project.cooperationcenter.domain.file.model;

public enum FileType {
    IMAGE("image"),
    FILE("file");

    private final String fileType;

    FileType(String fileType){this.fileType = fileType;}

    public String getFileType() {
        return fileType;
    }

    public static  FileType fromType(String type){
        for(FileType type1 : values()){
            if(type1.fileType.equalsIgnoreCase(type)) return type1;
        }
        return null;
    }
}
