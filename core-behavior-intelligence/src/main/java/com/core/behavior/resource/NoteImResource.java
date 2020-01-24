package com.core.behavior.resource;

import com.core.behavior.request.NoteImRequest;
import com.core.behavior.services.NoteImService;
import java.util.Arrays;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
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
@RequestMapping(value = "/scorecard/noteim")
public class NoteImResource {

    @Autowired
    private NoteImService noteImService;

    @RequestMapping(value = "", method = RequestMethod.GET)
    public ResponseEntity get(
            @RequestParam(name = "agency[]") Long[] agency,
            @RequestParam(name = "calendar") Long calendar,
            @RequestParam(name = "page", required = true) int page,
            @RequestParam(name = "size", required = true) int size) {

        try {

            PageRequest pageRequest = PageRequest.of(page, size, Sort.by("delivered").ascending());

            if (Optional.ofNullable(agency).isPresent() && Optional.ofNullable(calendar).isPresent()) {
                return ResponseEntity.ok(this.noteImService.findByAgencyAndCalendar(Arrays.asList(agency), calendar, pageRequest));
            } else if (Optional.ofNullable(agency).isPresent()) {
                return ResponseEntity.ok(this.noteImService.findByAgency(Arrays.asList(agency), pageRequest));
            } else {
                return ResponseEntity.ok().build();
            }

        } catch (Exception e) {
            Logger.getLogger(NoteImResource.class.getName()).log(Level.SEVERE, "[get]", e);
            return ResponseEntity.status(HttpStatus.resolve(500)).build();
        }

    }
    
    @RequestMapping(method = RequestMethod.PUT)
    public ResponseEntity update(@RequestBody NoteImRequest request){
        try{           
            
            noteImService.update(request);            
            return ResponseEntity.ok().build();
            
        }catch(Exception e){
            Logger.getLogger(NoteImResource.class.getName()).log(Level.SEVERE, "[update]", e);
            return ResponseEntity.status(HttpStatus.resolve(500)).build();
        }
    }

}
