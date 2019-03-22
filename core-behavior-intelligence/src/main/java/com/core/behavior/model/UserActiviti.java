package com.core.behavior.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;


/**
 *
 * @author Thiago H. Godoy <thiagodoy@hotmail.com>
 */
@Entity
@Table(name = "act_id_user")
@Data
public class UserActiviti {

    @Id
    @Column(name = "ID_")
    private String id;
    
    @Column(name = "REV_")
    private Long rev;
    
    @Column(name = "FIRST_")
    private String firstName;
    
    @Column(name = "LAST_")
    private String lastName;
    
    @Column(name = "EMAIL_")
    private String email;
    
    @Column(name = "PWD_")
    private String password;
    
    @Column(name = "PICTURE_ID_")
    private String picture;
    
    
}
