package com.vitrum.api.manager.member;

import com.vitrum.api.manager.team.Team;
import com.vitrum.api.credentials.user.User;
import com.vitrum.api.manager.team.TeamRepository;
import com.vitrum.api.credentials.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository repository;
    private final TeamRepository teamRepository;
    private final UserRepository userRepository;

    public String addToTeam(String username, String teamName) {
        var team = teamRepository.findByName(teamName)
                .orElseThrow(() -> new IllegalArgumentException("Can't find team by this name"));
        var user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Can't find user"));

        if (inTeam(user, team)) {
            return "The user is already in the team";
        }

        var member = team.addUser(user, RoleInTeam.MEMBER);
        repository.save(member);

        return "The user has been added to the team";
    }

    public void changeRole(Principal connectedUser, Map<String, String> request, String teamName) {
        List<Member> members = getPerformerAndTarget(connectedUser, request, teamName);
        var performer = members.get(0);
        var target = members.get(1);

        RoleInTeam role = RoleInTeam.valueOf(request.get("role").toUpperCase());
        if (!performer.getRole().canChangeTo(role)) {
            throw new IllegalArgumentException("You do not have permission to perform actions on this user");
        }

        if ((performer.getRole().equals(RoleInTeam.LEADER) && role.equals(RoleInTeam.LEADER))
                && (!performer.equals(target))
        ) {
            performer.setRole(RoleInTeam.CO_LEADER);
            repository.save(performer);
        }

        target.setRole(role);
        repository.save(target);
    }

    public void kick(Principal connectedUser, Map<String, String> request, String teamName) {
        List<Member> members = getPerformerAndTarget(connectedUser, request, teamName);
        var performer = members.get(0);
        var target = members.get(1);

        if (!performer.getRole().canChangeTo(target.getRole())) {
            throw new IllegalArgumentException("You do not have permission to perform actions on this user");
        }

        if (performer.equals(target) && performer.getRole().equals(RoleInTeam.LEADER)) {
            throw new IllegalArgumentException("First, put someone else in the leadership role");
        }

        repository.delete(target);
    }

    private List<Member> getPerformerAndTarget(Principal connectedUser, Map<String, String> request, String teamName) {
        var user = User.getUserFromPrincipal(connectedUser);
        var team = teamRepository.findByName(teamName)
                .orElseThrow(() -> new IllegalArgumentException("Team not found"));
        var performer = repository.findByUserAndTeam(user, team)
                .orElseThrow(() -> new UsernameNotFoundException("Admin not found"));

        user = userRepository.findByUsername(request.get("username"))
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        var target = repository.findByUserAndTeam(user, team)
                .orElseThrow(() -> new UsernameNotFoundException("Member not found"));

        List<Member> container = new ArrayList<>();
        container.add(performer);
        container.add(target);

        return container;
    }


    private Boolean inTeam(User user, Team team) {
        return team.getMembers().contains(repository.findByUser(user)
                .orElse(null));
    }
}
