package com.core.behavior.response;

import com.core.activiti.model.GroupActiviti;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author Thiago H. Godoy <thiagodoy@hotmail.com>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GroupResponse {
    private String id;    
    private String name;
    private String type;  
    
    public GroupResponse(GroupActiviti entity){
        this.id = entity.getId();        
        this.name = entity.getName();
        this.type = entity.getType();
    }   
    
}
