package com.core.activiti.model;

import com.core.activiti.model.GroupMemberActiviti;
import com.core.behavior.request.UserRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.Data;


/**
 *
 * @author Thiago H. Godoy <thiagodoy@hotmail.com>
 */
@Entity
@Table(schema = "activiti", name = "act_id_user")
@Data
public class UserActiviti {

    @Id
    @Column(name = "ID_")
    private String id;
    
    @Column(name = "REV_")
    private Long rev;
    
    @Column(name = "FIRST_")
    private String firstName;
    
    @Column(name = "LAST_")
    private String lastName;
    
    @Column(name = "EMAIL_")
    private String email;
    
    @Column(name = "PWD_")
    private String password;
    
    @Column(name = "PICTURE_ID_")
    private String picture;
    
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true,mappedBy = "userId")    
    private List<GroupMemberActiviti>groups;
    
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "userId")
    private List<UserInfo>info;
    
    public UserActiviti(UserRequest request){
        this.id = request.getId();
        this.firstName = request.getFirstName();
        this.lastName = request.getLastName();
        this.email = request.getEmail();
        this.password = request.getPassword();
        this.picture = request.getPhoto();
        
        this.groups = new ArrayList<>();
        request.getGroups().forEach(g->{            
            this.groups.add(new GroupMemberActiviti(this.id,g));        
        });
        
        this.info = Arrays.asList(new UserInfo(request.getEmail(), "agencia", request.getCompany()));
        
    }
    
    public UserActiviti(){}
    
    
    
}
