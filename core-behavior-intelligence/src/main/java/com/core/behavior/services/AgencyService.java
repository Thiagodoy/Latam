package com.core.behavior.services;

import com.core.behavior.model.Agency;
import com.core.behavior.repository.AgencyRepository;
import com.core.behavior.request.AgencyRequest;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 *
 * @author Thiago H. Godoy <thiagodoy@hotmail.com>
 */
@Service
public class AgencyService {

    @Autowired
    private AgencyRepository agencyRepository;

    public Page<Agency> list(Pageable page) {
        return agencyRepository.findAll(page);
    }
    
    public Agency findById(Long id){
        return agencyRepository.findById(id).get();
    }

    @Transactional
    public void save(AgencyRequest request) {        
        Agency entity = new Agency(request);
        agencyRepository.save(entity);
    }

    @Transactional
    public void delete(Long id) {
        agencyRepository.deleteById(id);
    }

    @Transactional
    public void update(AgencyRequest request) {        
        Agency entity = new Agency(request);
        agencyRepository.save(entity);        
    }

}
