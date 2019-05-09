package com.core.activiti.model;

import com.core.behavior.dto.UserDTO;
import com.core.behavior.request.UserRequest;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ColumnResult;
import javax.persistence.ConstructorResult;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.NamedNativeQuery;
import javax.persistence.OneToMany;
import javax.persistence.SqlResultSetMapping;
import javax.persistence.Table;
import lombok.Data;
import org.apache.commons.codec.digest.DigestUtils;

/**
 *
 * @author Thiago H. Godoy <thiagodoy@hotmail.com>
 */

@SqlResultSetMapping(name = "FileStatus",
        classes = @ConstructorResult(
                targetClass = UserDTO.class,
                columns = {
                    @ColumnResult(name = "user",  type = String.class)                                        
                }))

@NamedNativeQuery(name = "UserActiviti.getUserByAgencyAndProfile",resultSetMapping = "FileStatus",
        query = "select distinct u.id_  as user from activiti.act_id_user u \n" +
"	inner join activiti.act_id_membership b on b.user_id_ = u.id_ \n" +
"    inner join activiti.act_id_info f on f.user_id_ = u.id_ and f.key_ = 'agencia'\n" +
"    where f.value_ in :agencys and b.group_id_ in :profile")


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

    @Column(name = "USER_MASTER_ID_")
    private String userMasterId;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "created_at_")
    private LocalDateTime createdAt;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "userId", fetch = FetchType.EAGER)
    private Set<GroupMemberActiviti> groups;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "userId", fetch = FetchType.EAGER)
    private List<UserInfo> info;

    public UserActiviti(UserRequest request) {
        this.id = request.getId();
        this.firstName = request.getFirstName();
        this.lastName = request.getLastName();
        this.email = request.getEmail();
        this.password = request.getPassword();
        this.picture = request.getPhoto();
        this.userMasterId = request.getUserMaster();

        this.groups = new HashSet<>();
        request.getGroups().forEach(g -> {
            this.groups.add(new GroupMemberActiviti(this.id, g));
        });

        this.info = request.getInfo();
        this.info.forEach(e -> {
            e.setUserId(this.email);
        });

        this.createdAt = LocalDateTime.now();
    }

    public UserActiviti() {
    }

    public void merge(UserRequest user) {

        if (user.getFirstName() != null && !user.getFirstName().equals(this.getFirstName())) {
            this.firstName = user.getFirstName();
        }

        if (user.getLastName() != null && !user.getLastName().equals(this.getLastName())) {
            this.lastName = user.getLastName();
        }

        if (user.getPassword() != null && !user.getPassword().equals(this.getPassword())) {
            String p = DigestUtils.md5Hex(user.getPassword());
            this.password = p;
        }

        if (user.getPhoto() != null && !user.getPhoto().equals(this.getPicture())) {
            this.picture = user.getPhoto();
        }

        if (this.groups != null) {
            this.groups.clear();
        } else {
            this.groups = new HashSet<>();
        }

        user.getGroups().forEach(g -> {
            this.groups.add(new GroupMemberActiviti(this.id, g));
        });

        if (this.info != null) {
            this.info.clear();
        } else {
            this.info = new ArrayList<>();
        }

        user.getInfo().forEach(i -> {
            i.setUserId(this.email);
            this.info.add(i);
        });

    }

}
