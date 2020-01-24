/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.behavior.resource;


import com.core.behavior.model.Holiday;
import com.core.behavior.services.HolidayService;
import java.time.LocalDate;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
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
@RequestMapping(value = "/scorecard/holiday")
public class HolidayResource {

    @Autowired
    private HolidayService holidayService;

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity get(
            @RequestParam(required = false, name = "page", defaultValue = "0") int page,
            @RequestParam(required = false, name = "size", defaultValue = "10") int size,
            @DateTimeFormat(pattern = "dd/MM/yyyy")
            @RequestParam(required = false, name = "date") LocalDate date,
            @RequestParam(required = false, name = "year") Long year,
            @RequestParam(required = false, name = "description") String description,
            @RequestParam(required = false, name = "month") Long month) {

        try {

            if (Optional.ofNullable(date).isPresent()) {
                return ResponseEntity.ok(this.holidayService.getByDate(date));
            } else {
                PageRequest pageRequest = PageRequest.of(page, size, Sort.by("date").ascending());
                Page<Holiday> response = this.holidayService.list(pageRequest, month, year, description);
                return ResponseEntity.ok(response);
            }
        } catch (Exception e) {
            Logger.getLogger(HolidayResource.class.getName()).log(Level.SEVERE, "[ get ]", e);
            return ResponseEntity.status(HttpStatus.resolve(500)).build();
        }

    }

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity post(@RequestBody() Holiday holiday) {

        try {

            this.holidayService.save(holiday);
            return ResponseEntity.ok().build();

        } catch (Exception e) {
            Logger.getLogger(HolidayResource.class.getName()).log(Level.SEVERE, "[ post ]", e);
            return ResponseEntity.status(HttpStatus.resolve(500)).build();
        }

    }
    
    
     @RequestMapping(method = RequestMethod.PUT)
    public ResponseEntity update(@RequestBody() Holiday holiday) {

        try {

            this.holidayService.update(holiday);
            return ResponseEntity.ok().build();

        } catch (Exception e) {
            Logger.getLogger(HolidayResource.class.getName()).log(Level.SEVERE, "[ update ]", e);
            return ResponseEntity.status(HttpStatus.resolve(500)).build();
        }

    }
    
    @RequestMapping(value = "/{id}",method = RequestMethod.DELETE)
    public ResponseEntity post(@PathVariable("id") Long id) {
    
        
        try {
            this.holidayService.delete(id);
            return ResponseEntity.ok().build();

        } catch (Exception e) {
            Logger.getLogger(HolidayResource.class.getName()).log(Level.SEVERE, "[ post ]", e);
            return ResponseEntity.status(HttpStatus.resolve(500)).build();
        }

    }

}
