package com.cooperation.project.cooperationcenter.domain.agency.dto;

import com.cooperation.project.cooperationcenter.domain.agency.model.Agency;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

public class AgencyResponse {
    public record ListDto(
            String agencyName,
            String agencyAddress1,
            String agencyAddress2,
            String agencyPhone,
            String agencyOwner,
            String imgUrl
    ){
        public static ListDto from(Agency agency){
            String filePath = agency.getAgencyPicture()==null?
            "/api/v1/file/default/agency"
                    :"/api/v1/file/img/member/"+agency.getAgencyPicture().getFileId();
            return new ListDto(
                    agency.getAgencyName(),
                    agency.getAgencyAddress1(),
                    agency.getAgencyAddress2(),
                    agency.getAgencyPhone(),
                    agency.getAgencyOwner(),
                    filePath
            );
        }
        public static List<ListDto> from(List<Agency> agency){
            return agency.stream().map(ListDto::from).toList();
        }

        public static Page<ListDto> from(Page<Agency> page){
            return page.map(ListDto::from);
        }
    }
}
