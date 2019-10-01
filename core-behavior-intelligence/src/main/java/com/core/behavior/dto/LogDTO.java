package com.core.behavior.dto;

import java.io.Serializable;
import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 *
 * @author Thiago H. Godoy <thiagodoy@hotmail.com>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = {"record_content"})
public class LogDTO implements Serializable {

    public Long id;

    public Long file_id;

    public String field_name;

    public String message_error;

    public Timestamp created_at;

    public String record_content;

    public Long line_number;

    public Long ticket_id;

}
