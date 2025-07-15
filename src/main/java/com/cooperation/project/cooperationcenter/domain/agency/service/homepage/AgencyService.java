package com.cooperation.project.cooperationcenter.domain.agency.service.homepage;

import com.cooperation.project.cooperationcenter.domain.agency.dto.AgencyResponse;
import com.cooperation.project.cooperationcenter.domain.agency.model.Agency;
import com.cooperation.project.cooperationcenter.domain.agency.repository.AgencyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
}
