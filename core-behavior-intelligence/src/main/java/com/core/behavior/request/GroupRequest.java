package com.core.behavior.request;

import java.io.Serializable;
import lombok.Data;

/**
 *
 * @author Thiago H. Godoy <thiagodoy@hotmail.com>
 */
@Data
public class GroupRequest implements  Serializable{

    private String id;
    private String name;
    private String type;
    private String rev;
}
