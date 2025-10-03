package com.cooperation.project.cooperationcenter.domain.agency.dto;

public class AgencyRequest {
    public record SearchCondition(
            String keyword,
            String region
    ){}
}
