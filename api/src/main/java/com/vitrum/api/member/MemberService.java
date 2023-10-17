package com.vitrum.api.member;

import com.vitrum.api.team.Team;
import com.vitrum.api.user.User;
import com.vitrum.api.team.RoleInTeam;
import com.vitrum.api.team.TeamRepository;
import com.vitrum.api.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

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

    private Boolean inTeam(User user, Team team) {
        return team.getMembers().contains(repository.findByUser(user)
                .orElse(null));
    }
}
