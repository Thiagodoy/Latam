package com.core.behavior.activiti.response;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

/**
 *
 * @author Thiago H. Godoy <thiagodoy@hotmail.com>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageResponse<T> {

    private List<T> data;
    private Long total;
    private Long start;
    private String sort;
    private String order;
    private Integer size;
    private Integer pageCurrent;
    private Integer totalPages; 
    
    public  PageResponse( Page page){        
        this.data = page.getContent();
        this.total = page.getTotalElements();
        this.pageCurrent = page.getNumber();
        this.totalPages = page.getTotalPages();
    }
}
