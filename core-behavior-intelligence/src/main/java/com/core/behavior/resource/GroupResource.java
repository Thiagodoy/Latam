package com.core.behavior.resource;

import com.core.behavior.activiti.response.PageResponse;
import com.core.behavior.request.GroupRequest;
import com.core.behavior.response.GroupResponse;
import com.core.behavior.response.Response;
import com.core.behavior.services.GroupActivitiService;
import com.core.behavior.util.MessageCode;
import io.swagger.annotations.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Thiago H. Godoy <thiagodoy@hotmail.com>
 */
@RestController
@RequestMapping(value = "/group")
public class GroupResource {

    
    @Autowired
    private GroupActivitiService service;
    
    @CrossOrigin(origins = "http://localhost:8002") 
    @RequestMapping(method = RequestMethod.GET)
    @ApiResponse(response = Page.class,code = 200,message = "Ok")    
    public ResponseEntity getGroup(
            @RequestParam(required = false) String id,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String type,
            @RequestParam(name = "page", defaultValue = "1",required = true) int page,
            @RequestParam(name = "size", defaultValue = "10",required = true ) int size){        
        
        
        try {            
            PageRequest pageRequest = PageRequest.of(page, size, Sort.by("id"));             
            return ResponseEntity.ok(service.getGroup(id, name, type, pageRequest));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.resolve(500)).body(Response.build(null, MessageCode.SERVER_ERROR));
        }       
    }
    
    @CrossOrigin(origins = "http://localhost:8002") 
    @RequestMapping(method = RequestMethod.POST)
    @ApiResponse(code = 200,message = "Ok")    
    public ResponseEntity save(@RequestBody GroupRequest request){
        try {                                  
            service.saveGroup(request);
            return ResponseEntity.ok().build();                        
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.resolve(500)).body(Response.build(null, MessageCode.SERVER_ERROR));
        }  
    }
    
    @CrossOrigin(origins = "http://localhost:8002") 
    @RequestMapping(method = RequestMethod.PUT)
    @ApiResponse(code = 200,message = "Ok")    
    public ResponseEntity update(@RequestBody GroupRequest request){
        try {                                  
            service.updateGroup(request);
            return ResponseEntity.ok().build();                        
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.resolve(500)).body(Response.build(null, MessageCode.SERVER_ERROR));
        }  
    }
    
    @CrossOrigin(origins = "http://localhost:8002") 
    @RequestMapping(method = RequestMethod.DELETE, value = "/{id}")
    @ApiResponse(code = 200,message = "Ok")    
    public ResponseEntity delete(@PathVariable("id")String id){
        try {                                  
            service.deleteGroup(id);
            return ResponseEntity.ok().build();                        
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.resolve(500)).body(Response.<String>build(e.getMessage(), MessageCode.SERVER_ERROR));
        }  
    }
    
  
    
    
}
