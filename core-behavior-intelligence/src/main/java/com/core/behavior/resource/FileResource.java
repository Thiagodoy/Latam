/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.behavior.resource;

import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.core.behavior.exception.ActivitiException;
import com.core.behavior.model.File;
import com.core.behavior.response.Response;
import com.core.behavior.services.FileService;
import com.core.behavior.util.MessageCode;
import static com.core.behavior.util.MessageCode.SERVER_ERROR_AWS;
import com.core.behavior.util.Utils;
import io.swagger.annotations.ApiOperation;
import java.io.FileInputStream;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author Thiago H. Godoy <thiagodoy@hotmail.com>
 */
@RestController
@RequestMapping("/file")
public class FileResource {

    @Autowired
    private FileService fileService;

    @RequestMapping(value = "/{company}/{userId}/{uploadAws}/{uploadFtp}/{processFile}", method = RequestMethod.POST)
    @ApiOperation(value = "Upload of files")
    public ResponseEntity uploadFile(
            @PathVariable(name = "company") Long company,
            @PathVariable(name = "userId") String userId,
            @PathVariable(name = "uploadAws") boolean uploadAws,
            @PathVariable(name = "uploadFtp") boolean uploadFtp,
            @PathVariable(name = "processFile") boolean processFile,            
            @RequestPart(value = "file") MultipartFile file) {

        try {
            fileService.persistFile(file, userId, company, uploadAws, uploadFtp, processFile);
            return ResponseEntity.ok(Response.build("Ok", MessageCode.FILE_UPLOADED_SUCCESS));
        }catch(ActivitiException ex){
            Logger.getLogger(FileResource.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
            return ResponseEntity.status(HttpStatus.resolve(500)).body(Response.build(ex.getMessage(), ex.getCodeMessage()));
        }catch (Exception e) {
            Logger.getLogger(FileResource.class.getName()).log(Level.SEVERE, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.resolve(500)).body(Response.build(e.getMessage(), 500l));
        }

    }

    @RequestMapping(value = "/download", method = RequestMethod.GET)
    @ApiOperation(value = "Download of files")
    public ResponseEntity downloadFile(
            @RequestParam(name = "fileName") String fileName,
            @RequestParam(name = "company") Long company,
            HttpServletResponse response) {

        try {

            java.io.File file = fileService.downloadFile(fileName, company);
            
            String mimeType = Utils.getMimeType(file);
            response.setContentType(mimeType);
            response.setHeader("Content-disposition", "attachment; filename=" + fileName);
            InputStream in = new FileInputStream(file);
            org.apache.commons.io.IOUtils.copy(in, response.getOutputStream());
            response.flushBuffer();
            file.delete();
            return ResponseEntity.ok().build();

        }catch(AmazonS3Exception ex){
            Logger.getLogger(FileResource.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
            return ResponseEntity.status(HttpStatus.resolve(500)).body(Response.build(ex.getMessage(), SERVER_ERROR_AWS));
        }catch (Exception ex) {
            Logger.getLogger(FileResource.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
            return ResponseEntity.status(HttpStatus.resolve(500)).body(Response.build(ex.getMessage(), 500l));
        }

    }

    @RequestMapping(value = "/errors/{id}/{type}", method = RequestMethod.GET)
    public ResponseEntity downloadErrors(
            @PathVariable long id,
            @PathVariable long type,
            HttpServletResponse response) {

        try {

            boolean isCsv = type == 1;
            String fileName = "error-" + LocalDate.now().format(DateTimeFormatter.ISO_DATE) + (isCsv ? ".csv" : ".txt");

            response.setContentType("text/plain");
            response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
            return ResponseEntity.ok(fileService.generateFileErrors(id, isCsv));

        } catch (Exception e) {
            Logger.getLogger(FileResource.class.getName()).log(Level.SEVERE, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.resolve(500)).body(Response.build(e.getMessage(), 500l));
        }
    }
    
     @RequestMapping(value = "/errors/sintetico/{id}/{field}", method = RequestMethod.GET)
    public ResponseEntity downloadErrorSintetico(
            @PathVariable Long id,
            @PathVariable String field) {

        try {
            
            return ResponseEntity.ok(fileService.generateLogStatusSintetico(id, field));

        } catch (Exception e) {
            Logger.getLogger(FileResource.class.getName()).log(Level.SEVERE, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.resolve(500)).body(Response.build(e.getMessage(), 500l));
        }
    }
    
    
    

    @RequestMapping(value = "", method = RequestMethod.GET)
    public ResponseEntity list(
            @RequestParam(name = "fileName", required = false) String fileName,
            @RequestParam(name = "userId", required = false) String userId,
            @RequestParam(name = "dateCreated", required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd") Date dateCreated,
            @RequestParam(name = "timeStart", required = false) Long timeStart,
            @RequestParam(name = "timeEnd", required = false) Long timeEnd,
            @RequestParam(name = "company[]", required = false) Long[] company,
            @RequestParam(name = "status[]", required = false) String[] status,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size) {

        try {
            PageRequest pageRequest = PageRequest.of(page, size,Sort.by("createdDate").ascending());

            LocalDateTime paam = dateCreated != null ? Utils.convertDateToLOcalDateTime(dateCreated) : null;

            Page<File> response = fileService.list(fileName, userId, company, paam, pageRequest, status, timeStart,timeEnd);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Logger.getLogger(FileResource.class.getName()).log(Level.SEVERE, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.resolve(500)).body(Response.build(e.getMessage(), 500l));
        }

    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity delete(@PathVariable Long id) {
        try {
            fileService.deleteFile(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            Logger.getLogger(FileResource.class.getName()).log(Level.SEVERE, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.resolve(500)).body(Response.build(e.getMessage(), 500l));
        }
    }
    
     @RequestMapping(value = "/status/files", method = RequestMethod.GET)
    public ResponseEntity listar(@RequestParam(name = "company") Long agencia,
            @RequestParam(name = "timeStart", required = false) Long timeStart,
            @RequestParam(name = "timeEnd", required = false) Long timeEnd) {
        try {
            return ResponseEntity.ok(fileService.statusFilesProcess(agencia,timeStart,timeEnd));
        } catch (Exception e) {
            Logger.getLogger(FileResource.class.getName()).log(Level.SEVERE, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.resolve(500)).body(Response.build(e.getMessage(), 500l));
        }
    }

}
