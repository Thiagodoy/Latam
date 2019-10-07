/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.behavior.util;

/**
 *
 * @author thiag
 */
public enum StageEnum {
UPLOADED(0),VALIDATION_LAYOUT(1),VALIDATION_CONTENT(2),FINISHED(5);
    private long code;

    private StageEnum(long code) {
        this.code = code;
    }
    
    public long getCode(){
        return this.code;
    }
    
}
