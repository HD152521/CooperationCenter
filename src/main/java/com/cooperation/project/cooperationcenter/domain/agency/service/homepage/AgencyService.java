package com.cooperation.project.cooperationcenter.domain.agency.service.homepage;

import com.cooperation.project.cooperationcenter.domain.agency.dto.AgencyResponse;
import com.cooperation.project.cooperationcenter.domain.agency.model.Agency;
import com.cooperation.project.cooperationcenter.domain.agency.repository.AgencyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AgencyService {

    private final AgencyRepository agencyRepository;

    public List<AgencyResponse.ListDto> getAgencyList(){
        List<Agency> agencies = getAgencyAll();
        if(agencies!=null) return AgencyResponse.ListDto.from(agencies);
        else{
            return null;
        }
    }

    public Page<AgencyResponse.ListDto> getAgencyList(Pageable pageable,String keyword, String region){
        Page<Agency> agencies = getAgencyAllByPage(pageable);
        List<Agency> filtered = agencies.stream()
                .filter(agency -> (agency.getMember() == null || agency.getMember().isAccept()))
                .filter(agency -> keyword == null || agency.getAgencyName().toLowerCase().contains(keyword.toLowerCase()))
                .filter(agency -> region == null || agency.getAgencyAddress1().toLowerCase().contains(region.toLowerCase()))
                .toList();
        Page<Agency> filteredPage = new PageImpl<>(filtered, pageable, filtered.size());
        return AgencyResponse.ListDto.from(filteredPage);
    }

    public List<AgencyResponse.ListDto> getAgencyListForHome(){
        List<Agency> agencies = getAgencyAll();
        if(agencies==null){
            log.info("null임");
        }
        if(agencies.size()<=3) return AgencyResponse.ListDto.from(agencies);
        Collections.shuffle(agencies);
        return AgencyResponse.ListDto.from(agencies.stream()
                .limit(3)
                .toList());
    }

    public List<Agency> getAgencyAll() {
        try {
            return agencyRepository.findAll().stream()
                    .filter(agency -> (agency.getMember()==null||agency.getMember().isAccept()))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.warn(e.getMessage());
            return Collections.emptyList();
        }
    }

    public Page<Agency> getAgencyAllByPage(Pageable pageable) {
        try {
            Page<Agency> originalPage = agencyRepository.findAll(pageable);

            // 조건에 맞는 항목만 필터링
            List<Agency> filtered = originalPage
                    .stream()
                    .filter(agency -> agency.getMember() == null || agency.getMember().isAccept())
                    .toList();

            // 필터링된 데이터를 Page 객체로 다시 래핑
            return new PageImpl<>(filtered, pageable, filtered.size());

        } catch (Exception e) {
            log.warn(e.getMessage());
            return Page.empty(pageable);
        }
    }
}
