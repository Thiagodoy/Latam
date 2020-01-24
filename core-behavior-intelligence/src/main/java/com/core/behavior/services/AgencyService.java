package com.core.behavior.services;

import com.core.behavior.dto.ConsolidateOrCompanyDTO;
import com.core.behavior.dto.DayStatusDTO;
import com.core.behavior.dto.FrequencyStatusDTO;
import com.core.behavior.dto.ResultRules2;
import com.core.behavior.model.Agency;
import com.core.behavior.model.Calendar;
import com.core.behavior.repository.AgencyRepository;
import com.core.behavior.request.AgencyRequest;
import com.core.behavior.response.UserResponse;
import com.core.behavior.specifications.AgenciaSpecification;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

/**
 *
 * @author Thiago H. Godoy <thiagodoy@hotmail.com>
 */
@Service
public class AgencyService {

    @Autowired
    private AgencyRepository agencyRepository;

    @Autowired
    private UserInfoService userInfoService;   
    
    @Autowired
    private UserActivitiService userActivitiService;
    
    
    
    public List<Agency>listAll(){
        return agencyRepository.findAll();
    }
    
    public Page<Agency> list(String name, String code, Pageable page) {
        List<Specification<Agency>> predicates = new ArrayList<>();

        if (Optional.ofNullable(name).isPresent()) {
            predicates.add(AgenciaSpecification.name(name.toUpperCase()));
        }

        if (Optional.ofNullable(code).isPresent()) {
            predicates.add(AgenciaSpecification.code(code.toUpperCase()));
        }

        Specification<Agency> specification = predicates.stream().reduce((a, b) -> a.and(b)).orElse(null);

        Page<Agency> pageResult = agencyRepository.findAll(specification, page);

//        pageResult.get().parallel().forEach(a->{
//        
//            List<String> listIds = userInfoService
//                    .listInfoByKeyAndValue("agencia", String.valueOf(a.getId()))
//                    .stream()
//                    .map(aa->aa.getUserId())
//                    .collect(Collectors.toList());
//            
//            if(listIds.size() > 0){               
//                List<UserResponse> userResponses = userActivitiService.getUsers(listIds);
//                a.setUsers(userResponses);     
//            }
//        });
        return pageResult;
    }

    public List<UserResponse> getUserByAgency(Long id) {

        List<String> listIds = userInfoService
                .listInfoByKeyAndValue("agencia", String.valueOf(id))
                .stream()
                .map(aa -> aa.getUserId())
                .collect(Collectors.toList());        
        
        return  userActivitiService.getUsers(listIds);
    }

    public Agency findById(Long id) {
        return agencyRepository.findById(id).get();
    }

    @Transactional
    public void save(AgencyRequest request) {
        Agency entity = new Agency(request);
        agencyRepository.save(entity);
    }

    @Transactional
    public void delete(Long id) {
        userInfoService.deleteByKeyAndValue("agencia", String.valueOf(id));
        agencyRepository.deleteById(id);
    }
    
    public ConsolidateOrCompanyDTO checkConsolidateOrCompanyStatus(LocalDate start, LocalDate end, String codeAgency){
        return agencyRepository.checkConsolidateOrCompanyStatus(start, end, codeAgency);
    }
    
     public DayStatusDTO checkDay(LocalDate date, Agency agency){
        return agencyRepository.checkDayStatus(date, agency.getAgencyCode(), agency.getId());
    }
    
    public ResultRules2 checkRules2(Calendar calendar, Agency agency){
        return agencyRepository.checkRules2(calendar.getDateInit(),calendar.getDateEnd(),agency.getId());
    }    
    
    
    public List<FrequencyStatusDTO> checkStatusFrequency(LocalDate start, LocalDate end, String code){
        return agencyRepository.checkStatusFrequency(start, end, code);
    } 

}
