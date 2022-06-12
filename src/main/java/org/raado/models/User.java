package org.raado.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@AllArgsConstructor
@NoArgsConstructor
public class User {

    @BsonProperty
    private String userId;
    @BsonProperty
    private String name;
    @BsonProperty
    private String phoneNo;
    @BsonProperty
    private String password;
    @BsonProperty
    private boolean admin;
    @BsonProperty
    private List<Permission> permissions;

    public User(User user) {
        userId = user.getUserId();
        name = user.getName();
        phoneNo = user.getPhoneNo();
        password = user.getPassword();
        admin = user.isAdmin();
        permissions = user.getPermissions();
    }
}
