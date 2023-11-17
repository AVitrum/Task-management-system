package com.vitrum.api.services.implementation;

import com.vitrum.api.models.Member;
import com.vitrum.api.models.User;
import com.vitrum.api.models.enums.RoleInTeam;
import com.vitrum.api.repositories.MemberRepository;
import com.vitrum.api.repositories.TeamRepository;
import com.vitrum.api.repositories.UserRepository;
import com.vitrum.api.services.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final MemberRepository repository;
    private final TeamRepository teamRepository;
    private final UserRepository userRepository;

    @Override
    public void changeRole(Map<String, String> request, String teamName) {
        List<Member> members = getPerformerAndTarget(request, teamName);
        var performer = members.get(0);
        var target = members.get(1);

        RoleInTeam role = RoleInTeam.valueOf(request.get("role").toUpperCase());

        if (!performer.getRole().canChangeTo(role)) {
            throw new IllegalArgumentException("You do not have permission to perform actions on this user");
        }

        if (performer.getRole() == RoleInTeam.LEADER && role == RoleInTeam.LEADER && !performer.equals(target)) {
            performer.setRole(RoleInTeam.CO_LEADER);
            repository.save(performer);
        }

        target.setRole(role);
        repository.save(target);
    }

    @Override
    public void kick(Map<String, String> request, String teamName) {
        List<Member> members = getPerformerAndTarget(request, teamName);
        var performer = members.get(0);
        var target = members.get(1);

        if (!performer.getRole().canChangeTo(target.getRole())) {
            throw new IllegalArgumentException("You do not have permission to perform actions on this user");
        }

        if (performer.equals(target) && performer.getRole() == RoleInTeam.LEADER) {
            throw new IllegalArgumentException("First, put someone else in the leadership role");
        }

        repository.delete(target);
    }

    private List<Member> getPerformerAndTarget(Map<String, String> request, String teamName) {
        var user = User.getAuthUser(userRepository);
        var team = teamRepository.findByName(teamName)
                .orElseThrow(() -> new IllegalArgumentException("Team not found"));
        var performer = repository.findByUserAndTeam(user, team)
                .orElseThrow(() -> new UsernameNotFoundException("Admin not found"));

        var username = request.get("username");
        var target = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        var targetMember = repository.findByUserAndTeam(target, team)
                .orElseThrow(() -> new UsernameNotFoundException("Member not found"));

        List<Member> container = new ArrayList<>();
        container.add(performer);
        container.add(targetMember);

        return container;
    }
}
