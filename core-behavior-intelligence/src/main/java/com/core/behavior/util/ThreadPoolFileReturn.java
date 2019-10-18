/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.behavior.util;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import lombok.Data;

/**
 *
 * @author thiag
 */
@Data
public class ThreadPoolFileReturn {    
    private ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(1);
}
