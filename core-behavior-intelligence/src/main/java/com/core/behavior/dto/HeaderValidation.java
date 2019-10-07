/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.behavior.dto;

import java.io.File;
import lombok.Data;

/**
 *
 * @author thiag
 */
@Data
public class HeaderValidation {    
    private boolean isValid;
    private File fileError;
}
