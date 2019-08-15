/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.behavior.dto;

import com.google.common.collect.HashBiMap;
import java.util.HashMap;
import java.util.Map;
import lombok.Data;

/**
 *
 * @author thiag
 */
@Data
public class LineErrorDTO {

    private static int rowNum = 1;

    private String lineContent;
    private Map<String, String> comments = new HashMap<>();
    private int line;

    public LineErrorDTO(String content) {
        this();
        this.lineContent = content;
    }

    public LineErrorDTO() {
       
        this.line = ++LineErrorDTO.rowNum;        
    }
    
    public void put(String field, String message){
        
        String key = field + "" + this.line; 
        if(this.comments.containsKey(key)){
            String m = this.comments.get(field) + "\n" + message;
            this.comments.put(key, m);                      
        }else{
            this.comments.put(key, message);          
        }
    }

    public static void reset() {
        LineErrorDTO.rowNum = 1;
    }

}
