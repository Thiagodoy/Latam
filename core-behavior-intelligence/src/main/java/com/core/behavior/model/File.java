package com.core.behavior.model;

import com.core.behavior.util.StatusEnum;
import java.time.LocalDateTime;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import lombok.Data;

/**
 *
 * @author Thiago H. Godoy <thiagodoy@hotmail.com>
 */
@Entity
@Table(schema = "behavior", name = "file")
@Data
public class File {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name", unique = true)
    private String name;

    @Column(name = "createdAt", nullable = false)
    private LocalDateTime createdDate;

    @Column(name = "userId", nullable = false)
    private String userId;
    
    @Column(name = "company")
    private String company;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private StatusEnum status;

    @OneToMany(mappedBy = "fileId", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @OrderBy(value = "lineNumber ASC")
    private List<FileLines> lines;

}
