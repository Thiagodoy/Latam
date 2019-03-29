package com.core.behavior.activiti.request;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 *
 * @author Thiago H. Godoy <thiagodoy@hotmail.com>
 */
@Data
@AllArgsConstructor
public class GroupUserActivitiRequest implements Serializable{
    private String userId;
}
