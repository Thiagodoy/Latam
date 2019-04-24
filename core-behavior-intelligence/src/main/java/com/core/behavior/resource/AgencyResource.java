package com.core.behavior.resource;

import com.core.behavior.request.AgencyRequest;
import com.core.behavior.services.AgencyService;
import com.core.behavior.services.EmailService;
import com.core.behavior.util.EmailLayoutEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Thiago H. Godoy <thiagodoy@hotmail.com>
 */
@RestController
@RequestMapping(value = "/agency")
public class AgencyResource {

    @Autowired
    private EmailService emailService;
    
    @Autowired
    private AgencyService agencyService;

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity list() {

        try {
            return ResponseEntity.ok(agencyService.list());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.resolve(500)).body(e.getMessage());
        }

    }

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity save(@RequestBody AgencyRequest request) {
        try {
            agencyService.save(request);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.resolve(500)).body(e.getMessage());
        }
    }

    @RequestMapping(method = RequestMethod.PUT)
    public ResponseEntity update(@RequestBody AgencyRequest request) {
        try {
            agencyService.save(request);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.resolve(500)).body(e.getMessage());
        }
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity delete(@PathVariable Long id) {
        try {
             emailService.send(EmailLayoutEnum.CONGRATS,"Acesso", null, "thiagodoy@hotmail.com","aloysio.carvalho@bandtec.com.br","luis.maisnet@gmail.com ");
            //agencyService.delete(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.resolve(500)).body(e.getMessage());
        }
    }

}
