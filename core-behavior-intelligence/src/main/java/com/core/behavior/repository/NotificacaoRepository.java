/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.behavior.repository;

import com.core.behavior.model.Agency;
import com.core.behavior.model.Notificacao;
import com.core.behavior.util.NotificacaoStatusEnum;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 *
 * @author thiag
 */
@Repository
public interface NotificacaoRepository extends JpaRepository<Notificacao, Long> , JpaSpecificationExecutor<Agency> {
    
    
    List<Notificacao> findByStatus(NotificacaoStatusEnum status, Pageable pageable);
    
    
}
