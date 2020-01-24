/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.behavior.resource;

import com.core.behavior.model.Calendar;
import com.core.behavior.services.CalendarService;
import java.util.logging.Level;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
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
 * @author thiag
 */
@RestController
@RequestMapping(value = "/scorecard/calendar")
public class CalendarResource {

    @Autowired
    private CalendarService calendarService;

    @RequestMapping(value = "", method = RequestMethod.GET)
    public ResponseEntity list(
            @RequestParam(required = true, name = "page") int page,
            @RequestParam(required = true, name = "size") int size,
            @RequestParam(required = true, name = "pagination") boolean pagination,
            @RequestParam(required = false, name = "period") String period) {

        try {
            PageRequest p = PageRequest.of(page, size);
            Object response = this.calendarService.list(period, pagination, p);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            java.util.logging.Logger.getLogger(CalendarResource.class.getName()).log(Level.SEVERE, "[ list ]", e);
            return ResponseEntity.status(HttpStatus.resolve(500)).build();
        }

    }

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity save(@RequestBody Calendar calendar) {

        try {
            calendarService.save(calendar);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            java.util.logging.Logger.getLogger(CalendarResource.class.getName()).log(Level.SEVERE, "[ save ]", e);
            return ResponseEntity.status(HttpStatus.resolve(500)).build();
        }

    }

    @RequestMapping(value = "/{id}",method = RequestMethod.DELETE)
    public ResponseEntity delete(@PathVariable("id")Long id) {

        try {
            calendarService.delete(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            java.util.logging.Logger.getLogger(CalendarResource.class.getName()).log(Level.SEVERE, "[ delete ]", e);
            return ResponseEntity.status(HttpStatus.resolve(500)).build();
        }
        
    }
    
    
    @RequestMapping(method = RequestMethod.PUT)
    public ResponseEntity update(@RequestBody Calendar calendar) {

        try {
            calendarService.update(calendar);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            java.util.logging.Logger.getLogger(CalendarResource.class.getName()).log(Level.SEVERE, "[ update ]", e);
            return ResponseEntity.status(HttpStatus.resolve(500)).build();
        }
        
    }

}
