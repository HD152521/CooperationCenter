package com.cooperation.project.cooperationcenter.domain.agency.controller.homepage;

import com.cooperation.project.cooperationcenter.domain.member.dto.AgencyRegion;
import com.cooperation.project.cooperationcenter.global.exception.BaseResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/agency")
@Slf4j
public class AgencyRestController {
    @GetMapping("/region")
    public BaseResponse<?> getRegionList(){
        List<String> regions = Arrays.stream(AgencyRegion.values())
                .map(AgencyRegion::getLabel)
                .toList();
        log.info("{}", regions.toString());
        return BaseResponse.onSuccess(regions);
    }
}
