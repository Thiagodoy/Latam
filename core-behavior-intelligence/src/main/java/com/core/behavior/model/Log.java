package com.core.behavior.model;

import com.core.behavior.util.TypeErrorEnum;
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
import javax.persistence.PrePersist;
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
@NoArgsConstructor
@AllArgsConstructor
public class Log implements Serializable {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "file_id")
    private Long fileId;

    @Column(name = "file_line_id")
    private Long fileLineId;

    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private TypeErrorEnum type;

    @Column(name = "field_name")
    private String fieldName;

    @Column(name = "message_error")
    private String messageError;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Override
    public String toString() {
        MessageFormat formmater =  new MessageFormat("[{0} {1}] -> {2} \n", new Locale("pt", "BR"));
        return formmater.format(new Object[]{this.type, this.fieldName, this.messageError});
    }   

    public String toStringCsv() {
        MessageFormat formmater =  new MessageFormat("[{0} {1}], {2} \n", new Locale("pt", "BR"));
        return formmater.format(new Object[]{this.type, this.fieldName, this.messageError});
    }
    
    @PrePersist
    public void setCreatedAt(){
        this.createdAt = LocalDateTime.now();
    }
}
