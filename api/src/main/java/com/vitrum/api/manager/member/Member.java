package com.vitrum.api.manager.member;

import com.vitrum.api.manager.task.history.OldTask;
import com.vitrum.api.manager.task.main.Task;
import com.vitrum.api.manager.team.Team;
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private Team team;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    private RoleInTeam role;

    @OneToMany(mappedBy = "creator")
    private List<Task> creatorTasks;

    @OneToMany(mappedBy = "performer")
    private List<Task> performerTasks;

    @OneToMany(mappedBy = "creator")
    private List<OldTask> oldTasks;
}
