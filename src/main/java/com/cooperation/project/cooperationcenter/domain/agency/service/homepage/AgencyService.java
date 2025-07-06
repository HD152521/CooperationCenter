package com.cooperation.project.cooperationcenter.domain.agency.service.homepage;

import com.cooperation.project.cooperationcenter.domain.agency.model.Agency;
import com.cooperation.project.cooperationcenter.domain.agency.repository.AgencyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AgencyService {

    private final AgencyRepository agencyRepository;

    public List<Agency> getAgencyAll(){
        try{
            return agencyRepository.findAll();
        }catch (Exception e){
            log.warn(e.getMessage());
            return null;
        }
    }
}
