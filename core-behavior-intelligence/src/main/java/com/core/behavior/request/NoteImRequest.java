/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.behavior.request;

import java.time.LocalDateTime;
import lombok.Data;

/**
 *
 * @author thiag
 */
@Data
public class NoteImRequest {

    private Long id;

    private String delivered;

    private String user;   

}
