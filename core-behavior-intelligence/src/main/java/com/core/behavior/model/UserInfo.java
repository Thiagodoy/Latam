package com.core.behavior.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 *
 * @author Thiago H. Godoy <thiagodoy@hotmail.com>
 */
@Entity
@Table(name = "act_id_info")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"userId","key","value"})
public class UserInfo {

    @Id
    @Column(name = "ID_")        
    private String id ;
    
    @Column(name = "REV_")
    private long rev;
    
    @Column(name = "USER_ID_")
    private String userId;
    
    @Column(name = "TYPE_")
    private String type;
    
    @Column(name = "KEY_")
    private String key;
    
    @Column(name = "VALUE_")
    private String value;
            
    public UserInfo(String userId, String key, String value){
        this.rev = 1;
        this.type = "userinfo";
        this.key = key;
        this.value = value;
        this.userId = userId;    
        this.id = String.valueOf(hashCode());
        
                
    }        
}
