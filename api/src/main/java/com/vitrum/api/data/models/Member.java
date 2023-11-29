package com.vitrum.api.data.models;

import com.vitrum.api.data.enums.RoleInTeam;
import com.vitrum.api.repositories.UserRepository;
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

    private boolean isEmailsAllowed;

    @DBRef
    private List<Bundle> creatorBundles;

    @DBRef
    private List<Bundle> performerBundles;

    public boolean checkPermissionToCreate() {
        return this.getRole().equals(RoleInTeam.MEMBER);
    }

    public static Member findMemberByKey(String key, List<Member> members) {
        for (var member : members)
            if (member != null && member.getUser().getTrueUsername().equals(key))
                return member;
        throw new IllegalArgumentException("Member not found");
    }

    public static Member findCreator(Team team, UserRepository userRepository) {
        return findMemberByKey(User.getUsername(userRepository), team.getMembers());
    }

    public static Member findPerformer(String performer, Team team) {
        return findMemberByKey(performer, team.getMembers());
    }
}
