package com.core.behavior.model;

import com.core.behavior.annotations.PositionParameter;
import com.core.behavior.util.TypeErrorEnum;
import com.fasterxml.jackson.annotation.JsonFormat;
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
@Table(schema = "behavior", name = "log")
@Data

@AllArgsConstructor
public class Log implements Serializable {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @PositionParameter(value = 7)
    public Long id;

    @PositionParameter(value = 1)
    @Column(name = "file_id")
    public Long fileId;   

    @PositionParameter(value = 3)
    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    public TypeErrorEnum type;

    @PositionParameter(value = 0)
    @Column(name = "field_name")
    public String fieldName;

    @PositionParameter(value = 2)
    @Column(name = "message_error")
    public String messageError;
    
    @PositionParameter(value = 4)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "created_at")
    public LocalDateTime createdAt;
    
    @PositionParameter(value = 6)
    @Column(name = "record_content")
    public String recordContent;
    
    @PositionParameter(value = 5)
    @Column(name = "line_number")
    public Long lineNumber;

    @Override
    public String toString() {
        MessageFormat formmater =  new MessageFormat("[CONTENT LINE] -> {0}\n[{1} {2}] -> {3} \n", new Locale("pt", "BR"));
        return formmater.format(new Object[]{this.recordContent, this.type, this.fieldName, this.messageError});
    }   

    public String toStringCsv() {
        MessageFormat formmater =  new MessageFormat("[{0} {1}], {2} \n", new Locale("pt", "BR"));
        return formmater.format(new Object[]{this.type, this.fieldName, this.messageError});
    }
    
    public Log(){
        this.createdAt = LocalDateTime.now();
    }
    
}
