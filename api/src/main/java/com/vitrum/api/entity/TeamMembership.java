package com.vitrum.api.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "team_membership")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TeamMembership {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Team team;

    @ManyToOne
    private User user;

    @Enumerated(EnumType.STRING)
    private RoleInTeam role;

//    private Date joinDate;

}
