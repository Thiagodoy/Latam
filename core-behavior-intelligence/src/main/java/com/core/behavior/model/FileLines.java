package com.core.behavior.model;

import com.core.behavior.util.StatusEnum;
import java.io.Serializable;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.Locale;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author Thiago H. Godoy <thiagodoy@hotmail.com>
 */
@Entity
@Table(schema = "behavior", name = "file_lines")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FileLines implements Serializable{

   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   @Column(name = "id")
   private Long id;
   
   @Column(name = "file_id", nullable = false)
   private Long fileId;
   
   @Column(name = "content")
   private String content;
   
   @Enumerated(EnumType.STRING)
   @Column(name = "status")
   private StatusEnum status;
   
   @Column(name = "line_number", nullable = false)
   private Long lineNumber;
   
   @Column(name = "created_at")
   private LocalDateTime createdAt;    
   
    @Override
    public String toString() {
       return MessageFormat.format("\n[LineNumber] -> {1} [content] -> {2} \n", this.id,this.lineNumber, this.content); 
    }   

    public String toStringCsv() {
       return MessageFormat.format("[LineNumber], [{1}] \n [{2}] \n", this.id,this.lineNumber, this.content); 
    }
   
   
   
    
            
   public FileLines(Long fileId, StatusEnum status, String content, Long lineNumber){
       this.fileId = fileId;
       this.status = status;
       this.content = content;       
       this.lineNumber = lineNumber;
       this.createdAt = LocalDateTime.now();
   } 
}
