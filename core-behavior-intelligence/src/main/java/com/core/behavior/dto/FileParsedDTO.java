package com.core.behavior.dto;

import com.core.behavior.model.File;
import com.core.behavior.model.Ticket;
import java.util.List;
import lombok.Data;

/**
 *
 * @author Thiago H. Godoy <thiagodoy@hotmail.com>
 */
@Data
public class FileParsedDTO {
    private Header header;
    private List<Ticket> ticket;
    private File file;    
    
}
