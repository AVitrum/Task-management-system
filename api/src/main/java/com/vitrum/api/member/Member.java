package com.vitrum.api.member;

import com.vitrum.api.task.Task;
import com.vitrum.api.team.Team;
import com.vitrum.api.team.RoleInTeam;
import com.vitrum.api.credentials.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "member")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "team_id")
    private Team team;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    private RoleInTeam role;

    @OneToMany(mappedBy = "member")
    private List<Task> tasks;

//    private Date joinDate;

}
