package com.vitrum.api.manager.member;

import com.vitrum.api.manager.team.Team;
import com.vitrum.api.credentials.user.User;
import com.vitrum.api.manager.team.TeamRepository;
import com.vitrum.api.credentials.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.security.Principal;
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
        var user = (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();
        var team = teamRepository.findByName(teamName)
                .orElseThrow(() -> new IllegalArgumentException("Team not found"));
        var admin = repository.findByUserAndTeam(user, team)
                .orElseThrow(() -> new UsernameNotFoundException("Admin not found"));
        user = userRepository.findByUsername(request.get("username"))
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        var member = repository.findByUserAndTeam(user, team)
                .orElseThrow(() -> new UsernameNotFoundException("Member not found"));

        RoleInTeam role = RoleInTeam.valueOf(request.get("role").toUpperCase());
        if (!admin.getRole().canChangeTo(role)) {
            throw new IllegalArgumentException("You do not have permission to change to this role");
        }

        if ((admin.getRole().equals(RoleInTeam.LEADER) && role.equals(RoleInTeam.LEADER))
                && (!admin.equals(member))
        ) {
            admin.setRole(RoleInTeam.CO_LEADER);
            repository.save(admin);
        }

        member.setRole(role);
        repository.save(member);
    }

    private Boolean inTeam(User user, Team team) {
        return team.getMembers().contains(repository.findByUser(user)
                .orElse(null));
    }

}
