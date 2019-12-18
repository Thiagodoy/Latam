/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.behavior.resource;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import scala.collection.parallel.ParIterableLike.Product;

/**
 *
 * @author thiag
 */
public class FileResorceTest extends ResourceAbstractTest {

    @Override
    @Before
    public void setUp() {
        super.setUp();
    }
    
     @Test
   public void getProductsList() throws Exception {
      String uri = "/scheduler/generateFileIntegration/0";
      MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.post(uri)
         .accept(MediaType.APPLICATION_JSON_VALUE)).andReturn();
      
      int status = mvcResult.getResponse().getStatus();
      assertEquals(200, status);
      String content = mvcResult.getResponse().getContentAsString();
      Product[] productlist = super.mapFromJson(content, Product[].class);
      assertTrue(productlist.length > 0);
   }

}
