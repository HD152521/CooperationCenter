package com.cooperation.project.cooperationcenter.domain.agency.dto;

import com.cooperation.project.cooperationcenter.domain.agency.model.Agency;
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
            String filePath = "/api/v1/file/img/member/"+agency.getAgencyPicture().getFileId();
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
            List<ListDto> dto = new ArrayList<>();
            for(Agency a : agency) dto.add(ListDto.from(a));
            return dto;
        }
    }
}
