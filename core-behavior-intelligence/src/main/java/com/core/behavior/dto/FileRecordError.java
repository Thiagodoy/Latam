/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.behavior.dto;

import java.util.List;
import java.util.Map;
import lombok.Data;
import org.apache.poi.ss.usermodel.Comment;

/**
 *
 * @author thiag
 */
@Data
public class FileRecordError {
    
    Map<String,String>values;
    List<Comment>comments; 
    
}
