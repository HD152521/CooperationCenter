package com.cooperation.project.cooperationcenter.domain.member.dto;

import lombok.Getter;

@Getter
public enum AgencyRegion {
    BEIJING("北京市"),
    TIANJIN("天津市"),
    SHANGHAI("上海市"),
    CHONGQING("重庆市"),
    HEBEI("河北省"),
    SHANXI("山西省"),
    LIAONING("辽宁省"),
    JILIN("吉林省"),
    HEILONGJIANG("黑龙江省"),
    JIANGSU("江苏省"),
    ZHEJIANG("浙江省"),
    ANHUI("安徽省"),
    FUJIAN("福建省"),
    JIANGXI("江西省"),
    SHANDONG("山东省"),
    HENAN("河南省"),
    HUBEI("湖北省"),
    HUNAN("湖南省"),
    GUANGDONG("广东省"),
    HAINAN("海南省"),
    SICHUAN("四川省"),
    GUIZHOU("贵州省"),
    YUNNAN("云南省"),
    SHAANXI("陕西省"),
    GANSU("甘肃省"),
    QINGHAI("青海省"),
    NEIMENGGU("内蒙古自治区"),
    GUANGXI("广西壮族自治区"),
    XIZANG("西藏自治区"),
    NINGXIA("宁夏回族自治区"),
    XINJIANG("新疆维吾尔自治区"),
    HONGKONG("香港特别行政区"),
    MACAU("澳门特别行政区");

    private final String label;

    AgencyRegion(String label) {
        this.label = label;
    }

    public static AgencyRegion fromLabel(String label) {
        for (AgencyRegion region : values()) {
            if (region.getLabel().equals(label)) {
                return region;
            }
        }
        throw new IllegalArgumentException("Unknown region: " + label);
    }

}