package com.cooperation.project.cooperationcenter.domain.agency.controller.homepage;

import com.cooperation.project.cooperationcenter.domain.agency.dto.AgencyRequest;
import com.cooperation.project.cooperationcenter.domain.agency.service.homepage.AgencyService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
@RequestMapping("/agency")
@Slf4j
public class AgengyController {

    private final AgencyService agencyService;

    private final String agencyPath = "homepage/user/agency";

    @RequestMapping("/list")
    @Operation(summary = "해당 지역과 키워드 값을 가진 유학원 반환")
    public String agencyList(
            Model model,
            @PageableDefault(size = 12, sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String region){
        model.addAttribute("agencyDto",agencyService.getAgencyList(pageable,keyword,region));
        model.addAttribute("keyword",keyword);
        model.addAttribute("region",region);
        return agencyPath+"/agency-introduction";
    }
}

