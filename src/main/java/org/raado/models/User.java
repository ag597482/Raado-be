package org.raado.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.vz.mongodb.jackson.Id;
import net.vz.mongodb.jackson.ObjectId;

import java.util.List;
import java.util.UUID;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@AllArgsConstructor
@NoArgsConstructor
public class User {

    @ObjectId
    @Id
    private String userId;
    private String name;
    private String phoneNo;
    private String password;
    private boolean admin;
    private List<Permission> permissions;

    public User(User user) {
        name = user.getName();
        phoneNo = user.getPhoneNo();
        password = user.getPassword();
        admin = user.isAdmin();
        permissions = user.getPermissions();
    }
}
