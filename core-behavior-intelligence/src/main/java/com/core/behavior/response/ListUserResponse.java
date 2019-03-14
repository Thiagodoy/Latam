package com.core.behavior.response;

import java.util.List;
import lombok.Data;

/**
 *
 * @author Thiago H. Godoy <thiagodoy@hotmail.com>
 */
@Data
public class ListUserResponse {

    public List<UserResponse> data;
    
}
