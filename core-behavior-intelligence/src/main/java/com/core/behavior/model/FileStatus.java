package com.core.behavior.model;

import com.core.behavior.dto.FileStatusDTO;
import javax.persistence.Column;
import javax.persistence.ColumnResult;
import javax.persistence.ConstructorResult;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedNativeQuery;
import javax.persistence.SqlResultSetMapping;
import javax.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author Thiago H. Godoy <thiagodoy@hotmail.com>
 */

@SqlResultSetMapping(name = "FileStatus",
        classes = @ConstructorResult(
                targetClass = FileStatusDTO.class,
                columns = {
                    @ColumnResult(name = "field_name",  type = String.class),                    
                    @ColumnResult(name = "qtd_erro", type = Long.class),
                    @ColumnResult(name = "percentual_erro", type = Double.class),                    
                    @ColumnResult(name = "percentual_acerto", type = Double.class),                    
                    @ColumnResult(name = "qtd_total_lines", type = Long.class),
                }))

@NamedNativeQuery(name = "FileStatus.getProcessStatus", resultSetMapping = "FileStatus",
        query = "select field_name,\n" +
" count(1) as qtd_erro,\n" +
" (truncate((count(1)/b.qtd_total_lines)*100,2)) as percentual_erro,\n" +
" (100 - (truncate((count(1)/b.qtd_total_lines)*100,2))) as percentual_acerto, \n" +
" b.qtd_total_lines \n" +
" from behavior.log a \n" +
" left join behavior.file b on a.file_id = b.id   \n" +
" where file_id = :fileId \n" +
" group by field_name, b.qtd_total_lines")




@Entity
@Table(schema = "behavior", name = "file_status")
@Data
@NoArgsConstructor
public class FileStatus {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    
    @Column(name = "file_id", nullable = false)
    private Long fileId;
    
    @Column(name = "field_name")
    private String fieldName;
    
    @Column(name = "qtd_errors")
    private Long qtdErrors;
    
    @Column(name = "qtd_total_lines")
    private Long qtdTotalLines;
    
   @Column(name = "percentual_error")
   private Double percentualError;
   
   @Column(name = "percentual_hit")
   private Double percentualHit; 

    public FileStatus(Long fileId, String fieldName, Long qtdErrors, Long qtdTotalLines, Double percentualError, Double percentualHit) {
        this.fileId = fileId;
        this.fieldName = fieldName;
        this.qtdErrors = qtdErrors;
        this.qtdTotalLines = qtdTotalLines;
        this.percentualError = percentualError;
        this.percentualHit = percentualHit;
    }
   
   

}
