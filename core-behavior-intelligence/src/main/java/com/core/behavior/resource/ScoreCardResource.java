/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.behavior.resource;


import com.core.behavior.request.ScoreCardRequest;
import com.core.behavior.services.ScoreCardService;
import java.util.Arrays;
import java.util.logging.Level;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
@RequestMapping(value = "/scorecard/card")
public class ScoreCardResource {

    @Autowired
    private ScoreCardService scoreCardService;

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity get(
            @RequestParam(name = "agencys[]", required = true) Long[] agencys,
            @RequestParam(name = "calendar", required = false) Long calendar,
            @RequestParam(name = "approved", required = false) String approved,
            @RequestParam(name = "reviewed", required = false) String reviewed,
            @RequestParam(name = "sort", required = true) String sort,
            @RequestParam(name = "page", required = true) int page,
            @RequestParam(name = "size", required = true) int size) {

        try {
            return ResponseEntity.ok(this.scoreCardService.findByAgencyAndCalendar(Arrays.asList(agencys), calendar, approved, reviewed, PageRequest.of(page, size, Sort.by(sort).ascending())));
        } catch (Exception e) {
            java.util.logging.Logger.getLogger(CalendarResource.class.getName()).log(Level.SEVERE, "[ get ]", e);
            return ResponseEntity.status(HttpStatus.resolve(500)).build();
        }

    }

    @RequestMapping(method = RequestMethod.PUT)
    public ResponseEntity update(@RequestBody ScoreCardRequest request) {
        try {

            this.scoreCardService.update(request);
            return ResponseEntity.ok().build();

        } catch (Exception e) {
            java.util.logging.Logger.getLogger(CalendarResource.class.getName()).log(Level.SEVERE, "[ update ]", e);
            return ResponseEntity.status(HttpStatus.resolve(500)).build();
        }
    }

}
