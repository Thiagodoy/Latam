package com.core.behavior.model;

import com.core.behavior.request.GroupRequest;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author Thiago H. Godoy <thiagodoy@hotmail.com>
 */
@Entity
@Table(name="act_id_group")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GroupActiviti {

    @Id
    @Column(name = "ID_")
    private String id;
    
    @Column(name = "REV_")
    private String rev;
    
    @Column(name = "NAME_")
    private String name;
    
    @Column(name = "TYPE_")
    private String type;
    
    public GroupActiviti(GroupRequest request){
        this.id = request.getId();
        this.name = request.getName();
        this.type = request.getType();
        this.rev = request.getRev();
    }
            
}
