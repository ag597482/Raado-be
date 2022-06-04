package org.raado.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.vz.mongodb.jackson.Id;
import net.vz.mongodb.jackson.ObjectId;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;


import java.util.List;
import java.util.UUID;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@AllArgsConstructor
@NoArgsConstructor
public class User {


    @JsonIgnore
    @JsonProperty("_id")
    private final String userId = UUID.randomUUID().toString();

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
