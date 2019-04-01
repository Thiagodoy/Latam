package com.core.behavior.model;

import com.core.behavior.util.TypeErrorEnum;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
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
@NoArgsConstructor
@AllArgsConstructor
public class Log implements Serializable {

    @Id
    @Column(name = "id")
    private Long id;
    
    @Column(name = "file_id")
    private Long fileId;    
    
    @Column(name = "file_line_id")
    private Long fileLine;
    
    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private TypeErrorEnum type;
    
    @Column(name = "field_name")
    private String fieldName;
            
    @Column(name = "message_error")
    private String messageError;
}
