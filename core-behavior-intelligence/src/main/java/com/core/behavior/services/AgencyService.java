package com.core.behavior.services;

import com.core.behavior.model.Agency;
import com.core.behavior.repository.AgencyRepository;
import com.core.behavior.request.AgencyRequest;
import java.util.List;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author Thiago H. Godoy <thiagodoy@hotmail.com>
 */
@Service
public class AgencyService {

    @Autowired
    private AgencyRepository agencyRepository;

    public List<Agency> list() {
        return agencyRepository.findAll();
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
