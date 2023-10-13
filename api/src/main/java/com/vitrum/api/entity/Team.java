package com.vitrum.api.entity;

import com.vitrum.api.entity.enums.RoleInTeam;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "team")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Team {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @OneToMany(mappedBy = "team")
    private List<Member> members = new ArrayList<>();

    public Member addUser(User user, RoleInTeam role) {
        var member = Member.builder()
                .user(user)
                .role(role)
                .team(this)
                .build();
        members.add(member);
        return member;
    }

}
