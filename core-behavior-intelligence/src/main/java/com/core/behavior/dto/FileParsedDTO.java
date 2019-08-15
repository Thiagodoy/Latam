package com.core.behavior.dto;

import com.core.behavior.model.File;
import java.util.List;
import lombok.Data;

/**
 *
 * @author Thiago H. Godoy <thiagodoy@hotmail.com>
 */
@Data
public class FileParsedDTO {
    private HeaderDTO header;
    private List<TicketDTO> ticket;
    private File file;    
    
}
