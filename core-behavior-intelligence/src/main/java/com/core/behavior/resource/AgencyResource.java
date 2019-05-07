package com.core.behavior.resource;

import com.core.behavior.request.AgencyRequest;
import com.core.behavior.response.Response;
import com.core.behavior.services.AgencyService;
import com.core.behavior.services.UserInfoService;
import com.core.behavior.util.MessageCode;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
@RequestMapping(value = "/agency")
public class AgencyResource {

    @Autowired
    private AgencyService agencyService;
    
    @Autowired
    private UserInfoService infoService;

    @RequestMapping(value = "", method = RequestMethod.GET)
    public ResponseEntity list(
            @RequestParam(name = "name", required = false) String name,
            @RequestParam(name = "code", required = false) String code,
            @RequestParam(name = "page") int page,
            @RequestParam(name = "size") int size) {

        try {
            PageRequest pageRequest = PageRequest.of(page, size, Sort.by("name").ascending());
            return ResponseEntity.ok(agencyService.list(name, code, pageRequest));
        } catch (Exception e) {
            Logger.getLogger(AgencyResource.class.getName()).log(Level.SEVERE, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.resolve(500)).body(Response.build(e.getMessage(), 500l));
        }
    }

    @RequestMapping(value = "/users/{id}", method = RequestMethod.GET)
    public ResponseEntity getUsers(
            @PathVariable(name = "id") long id) {
        try {
            return ResponseEntity.ok(agencyService.getUserByAgency(id));
        } catch (Exception e) {
            Logger.getLogger(AgencyResource.class.getName()).log(Level.SEVERE, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.resolve(500)).body(Response.build(e.getMessage(), MessageCode.SERVER_ERROR));
        }
    }

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity save(@RequestBody AgencyRequest request) {
        try {
            agencyService.save(request);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            Logger.getLogger(AgencyResource.class.getName()).log(Level.SEVERE, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.resolve(500)).body(Response.build(e.getMessage(), 500l));
        }
    }

    @RequestMapping(method = RequestMethod.PUT)
    public ResponseEntity update(@RequestBody AgencyRequest request) {
        try {
            agencyService.save(request);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            Logger.getLogger(AgencyResource.class.getName()).log(Level.SEVERE, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.resolve(500)).body(Response.build(e.getMessage(), 500l));
        }
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity delete(@PathVariable Long id) {
        try {
            agencyService.delete(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            Logger.getLogger(AgencyResource.class.getName()).log(Level.SEVERE, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.resolve(500)).body(Response.build(e.getMessage(), 500l));
        }
    }

//    @RequestMapping(value = "/association/{id}", method = RequestMethod.post)
//    public ResponseEntity delete(@PathVariable Long id) {
//        try {
//            agencyService.delete(id);
//            return ResponseEntity.ok().build();
//        } catch (Exception e) {
//            Logger.getLogger(AgencyResource.class.getName()).log(Level.SEVERE, e.getMessage(), e);
//            return ResponseEntity.status(HttpStatus.resolve(500)).body(Response.build(e.getMessage(), 500l));
//        }
//    }
}
