package com.vitrum.api.models;

import com.vitrum.api.models.enums.RoleInTeam;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "members")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Member {

    @Id
    private String id;

    @DBRef
    private Team team;

    @DBRef
    private User user;

    private RoleInTeam role;

    @DBRef
    private List<Bundle> creatorBundles;

    @DBRef
    private List<Bundle> performerBundles;

    public boolean checkPermissionToCreate() {
        return this.getRole().equals(RoleInTeam.MEMBER);
    }
}
