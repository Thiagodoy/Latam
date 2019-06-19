/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.behavior.services;

import com.core.behavior.model.Notificacao;
import com.core.behavior.repository.NotificacaoRepository;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author thiag
 */
@Service
public class NotificacaoService {
    
    
    @Autowired
    private NotificacaoRepository repository;
    
    @Transactional
    public void save(Notificacao notificacao){        
        repository.save(notificacao);
    }
    
}
