/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.behavior.resource;

import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.io.IOUtils;
import org.beanio.StreamFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author thiag
 */
@RestController
@RequestMapping(value = "/asset")
public class AssetResource {

    @GetMapping(value = "/download/image/{fileName}", produces = MediaType.IMAGE_PNG_VALUE)
    public @ResponseBody byte[] getFile(@PathVariable(name = "fileName") String fileName) throws IOException {
        InputStream in = getClass().getResourceAsStream("/static/" + fileName + ".png");
        return IOUtils.toByteArray(in);
    }

}
