/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.behavior.resource;


import com.core.behavior.dto.ConsolidateOrCompanyDTO;
import com.core.behavior.model.Calendar;
import com.core.behavior.model.WeekFrequency;
import com.core.behavior.model.WeekInformation;
import com.core.behavior.model.WeekQuality;
import com.core.behavior.response.WeekResponse;
import com.core.behavior.services.AgencyService;
import com.core.behavior.services.HolidayService;
import com.core.behavior.services.WeekFrequencyService;
import com.core.behavior.services.WeekInformationService;
import com.core.behavior.services.WeekQualityService;
import com.core.behavior.util.Constantes;


import io.swagger.annotations.ApiOperation;
import java.time.LocalDate;
import java.util.logging.Level;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author thiag
 */
@RestController
@RequestMapping(value = "/scorecard/week")
public class WeekResource {

    @Autowired
    private WeekFrequencyService weekFrequencyService;

    @Autowired
    private WeekQualityService weekQualityService;

    @Autowired
    private WeekInformationService weekInformationService;

    @Autowired
    private HolidayService holidayService;
    
    @Autowired
    private AgencyService agencyService;
    
    
    @RequestMapping(method = RequestMethod.GET,value = "/consolidate/company")
    @ApiOperation(value="", response = ConsolidateOrCompanyDTO.class)
    public ResponseEntity getStatus(@DateTimeFormat(pattern = "dd/MM/yyyy")
            @RequestParam(required = false, name = "start") LocalDate start,
            @DateTimeFormat(pattern = "dd/MM/yyyy")
            @RequestParam(required = false, name = "end") LocalDate end,
            @RequestParam(required = true, name = "agency") String code){
        try {            
            ConsolidateOrCompanyDTO response = this.agencyService.checkConsolidateOrCompanyStatus(start, end, code);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            java.util.logging.Logger.getLogger(CalendarResource.class.getName()).log(Level.SEVERE, "[ getStatus ]", e);
            return ResponseEntity.status(HttpStatus.resolve(500)).build();
        }
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity get(@RequestParam(required = true, name = "type") String type,
            @RequestParam(required = true, name = "agency") Long agency,
            @RequestParam(required = true, name = "calendar") Long calendar) {
        try {

            switch (type) {
                case Constantes.WEEK_INFORMATION:

                    WeekResponse<WeekInformation> response = new WeekResponse<>();
                    response.setWeeks(this.weekInformationService.find(calendar, agency));

                    Calendar calendar1 = response.getWeeks().stream().findFirst().get().getCalendar();
                    response.setHolidays(this.holidayService.getHolidaysBetween(calendar1.getDateInit(), calendar1.getDateEnd()));

                    return ResponseEntity.ok(response);
                case Constantes.WEEK_FREQUENCY:
                    WeekResponse<WeekFrequency> response1 = new WeekResponse<>();
                    response1.setWeeks(this.weekFrequencyService.find(calendar, agency));

                    Calendar calendar2 = response1.getWeeks().stream().findFirst().get().getCalendar();
                    response1.setHolidays(this.holidayService.getHolidaysBetween(calendar2.getDateInit(), calendar2.getDateEnd()));

                    return ResponseEntity.ok(response1);
                case Constantes.WEEK_QUALITY:
                    WeekResponse<WeekQuality> response2 = new WeekResponse<>();
                    response2.setWeeks(this.weekQualityService.find(calendar, agency));

                    Calendar calendar3 = response2.getWeeks().stream().findFirst().get().getCalendar();
                    response2.setHolidays(this.holidayService.getHolidaysBetween(calendar3.getDateInit(), calendar3.getDateEnd()));

                    return ResponseEntity.ok(response2);
                default:
                    throw new Exception("Type not found!");
            }

        } catch (Exception e) {
            java.util.logging.Logger.getLogger(CalendarResource.class.getName()).log(Level.SEVERE, "[ get ]", e);
            return ResponseEntity.status(HttpStatus.resolve(500)).build();
        }
    }

}
