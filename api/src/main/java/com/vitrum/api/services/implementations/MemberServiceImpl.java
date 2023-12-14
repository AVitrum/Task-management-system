package com.vitrum.api.services.implementations;

import com.vitrum.api.data.enums.RoleInTeam;
import com.vitrum.api.data.models.Member;
import com.vitrum.api.data.models.Team;
import com.vitrum.api.data.models.User;
import com.vitrum.api.repositories.MemberRepository;
import com.vitrum.api.repositories.UserRepository;
import com.vitrum.api.repositories.TeamRepository;
import com.vitrum.api.services.interfaces.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.security.Principal;
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
    public void addToTeam(String teamName, Map<String, String> request) {
        var team = Team.findTeamByName(teamRepository, teamName);
        var user = userRepository.findByUsername(request.get("username"))
                .orElseThrow(() -> new UsernameNotFoundException("Can't find user"));

        if (repository.existsByUserAndTeam(user, team))
            throw new IllegalArgumentException("The user is already in the team");

        Member.create(repository, user, team, "Member");
    }

    @Override
    public void changeRole(Principal connectedUser, Map<String, String> request, String teamName) {
        List<Member> members = getPerformerAndTarget(connectedUser, request, teamName);
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
    public void kick(Principal connectedUser, Map<String, String> request, String teamName) {
        List<Member> members = getPerformerAndTarget(connectedUser, request, teamName);
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

    @Override
    public void changeEmailsMessagingStatus(String teamName, Principal connectedUser) {
        var team = teamRepository.findByName(teamName)
                .orElseThrow(() -> new IllegalArgumentException("Team not found"));
        var member = repository.findByUserAndTeam(User.getUserFromPrincipal(connectedUser), team)
                .orElseThrow(() -> new IllegalArgumentException("Member not found"));

        member.setEmailsAllowed(!member.isEmailsAllowed());
        repository.save(member);
    }

    @Override
    public boolean getEmailsMessagingStatus(String teamName, Principal connectedUser) {
        var team = teamRepository.findByName(teamName)
                .orElseThrow(() -> new IllegalArgumentException("Team not found"));
        var member = repository.findByUserAndTeam(User.getUserFromPrincipal(connectedUser), team)
                .orElseThrow(() -> new IllegalArgumentException("Member not found"));

        return member.isEmailsAllowed();
    }

    private List<Member> getPerformerAndTarget(Principal connectedUser, Map<String, String> request, String teamName) {
        var user = User.getUserFromPrincipal(connectedUser);
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
