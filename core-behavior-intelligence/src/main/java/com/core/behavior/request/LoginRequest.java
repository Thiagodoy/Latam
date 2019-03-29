package com.core.behavior.request;

import java.io.Serializable;
import lombok.Data;

/**
 *
 * @author Thiago H. Godoy <thiagodoy@hotmail.com>
 */
@Data
public class LoginRequest implements Serializable {
    private String email;
    private String password;
}
