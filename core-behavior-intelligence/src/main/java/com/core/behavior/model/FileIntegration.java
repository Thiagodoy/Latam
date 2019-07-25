/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.behavior.model;

import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import lombok.Data;

/**
 *
 * @author thiag
 */
@Entity

@Table(schema = "behavior", name = "file_integration")
@Data
public class FileIntegration {

    @Id
    @Column(name = "ETAG")
    private String etag;
    
    @Column(name = "FILE_NAME")
    private String fileName;
    
    @Column(name = "CREATED_AT")    
    private LocalDateTime createdAt;
    
    public FileIntegration(String etag, String fileName){
        this.etag = etag;
        this.fileName = fileName;
    }
    
    
    @PrePersist
    public void create(){
        this.createdAt = LocalDateTime.now(); 
    }
    
    




    
}
