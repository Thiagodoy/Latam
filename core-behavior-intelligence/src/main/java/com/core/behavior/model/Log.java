package com.core.behavior.model;

import com.core.behavior.annotations.PositionParameter;
import com.core.behavior.dto.LogStatusSinteticoDTO;
import com.core.behavior.util.TypeErrorEnum;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.io.Serializable;
import java.math.BigInteger;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Locale;
import javax.persistence.Column;
import javax.persistence.ColumnResult;
import javax.persistence.ConstructorResult;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedNativeQuery;
import javax.persistence.NamedQuery;
import javax.persistence.SqlResultSetMapping;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 *
 * @author Thiago H. Godoy <thiagodoy@hotmail.com>
 */
@SqlResultSetMapping(name = "LogResult", classes = @ConstructorResult(columns = {
    @ColumnResult(name = "msg", type = String.class),                    
    @ColumnResult(name = "qtd", type = Long.class),},
        targetClass = LogStatusSinteticoDTO.class))

@NamedNativeQuery(name = "Log.listErroSintetico", 
        query = "select  message_error as msg, count(1) as qtd "
        + "from behavior.log "
        + "where file_id = :fileId and field_name = :fieldName"
        + " group by message_error", resultSetMapping = "LogResult")



@Entity
@Table(schema = "behavior", name = "log")
@Data
@AllArgsConstructor
@EqualsAndHashCode(of = {"recordContent"})
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

    @PositionParameter(value = 8)
    @Column(name = "ticket_id")
    public Long ticketId;

    @Override
    public String toString() {
        MessageFormat formmater = new MessageFormat("[Line :{0}] -> Campo {2} -> {3};{4}\n", new Locale("pt", "BR"));        
        return formmater.format(new Object[]{this.lineNumber , this.type, this.fieldName, this.messageError, this.recordContent});
    }

    public String toStringCsv() {
        MessageFormat formmater = new MessageFormat("{0} {1} {2} \n", new Locale("pt", "BR"));
        return formmater.format(new Object[]{this.type, this.fieldName, this.messageError});
    }

    public Log() {
        this.createdAt = LocalDateTime.now();
    }

}
