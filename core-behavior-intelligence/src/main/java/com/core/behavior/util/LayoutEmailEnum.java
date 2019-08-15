/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.behavior.util;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author thiag
 */
public enum LayoutEmailEnum {

    ATIVO_EXECUTIVO("static/ATIVO_EXECUTIVO.html", "Alerta preventivo | Upload LATAM","identifier5","identifier6","identifier1"),
    ATIVO("static/ATIVO.html", "Alerta preventivo | Upload LATAM","identifier5","identifier6","identifier1"),
    CONGRATS("static/CONGRAT-EMAIL.html", "Acesso", "identifier1","identifier2","identifier3","identifier4","identifier5","identifier6"),
    FORGOT("static/FORGOT-ACESS.html", "Acesso", "identifier1","identifier2","identifier3","identifier4","identifier5","identifier6"),
    NOTIFICACAO_UPLOAD("static/UPLOAD_FILE_NOTIFICATION.html", "Upload","identifier1","identifier2","identifier3","identifier4","identifier5","identifier6");

    private final String path;
    private final String subject;
    private final String[] images;
    private final static Map<String, String> map = new HashMap();

    static {
        map.put("identifier1", Constantes.IMAGE_CHANFRO);
        map.put("identifier2", Constantes.IMAGE_FUNDO);
        map.put("identifier3", Constantes.IMAGE_LOGO_001);
        map.put("identifier4", Constantes.IMAGE_LOGO_002);
        map.put("identifier5", Constantes.IMAGE_LOGO_LATAM);
        map.put("identifier6", Constantes.IMAGE_LOGO_ONE);
    }

    LayoutEmailEnum(String path, String subject, String... images) {
        this.path = path;
        this.subject = subject;
        this.images = images;
    }
    
    public String getSubject(){
        return this.subject;
    }
    
    public String getPath(){
        return path;
    }
    
    public Map<String,String>getResouces(){
        
        Map<String,String>resources = new HashMap<>();
        
        for (String image : images) {            
            resources.put(image, map.get(image));            
        }
        
        return resources;
    }

}
