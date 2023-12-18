package com.vitrum.api.services.implementations;

import com.vitrum.api.data.enums.RoleInTeam;
import com.vitrum.api.data.models.Member;
import com.vitrum.api.data.models.Team;
import com.vitrum.api.data.models.User;
import com.vitrum.api.data.response.MemberResponse;
import com.vitrum.api.repositories.*;
import com.vitrum.api.services.interfaces.MemberService;
import com.vitrum.api.util.Converter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final MemberRepository repository;
    private final TeamRepository teamRepository;
    private final UserRepository userRepository;
    private final TaskRepository taskRepository;
    private final CommentRepository commentRepository;
    private final OldTaskRepository oldTaskRepository;
    private final Converter converter;

    @Override
    public void addToTeam(String teamName, Map<String, String> request) {
        Team team = Team.findTeamByName(teamRepository, teamName);
        String username = request.get("username");

        userRepository.findByUsername(username)
                .or(() -> userRepository.findByEmail(username))
                .ifPresentOrElse(
                        user -> {
                            if (repository.existsByUserAndTeam(user, team))
                                throw new IllegalArgumentException("The user is already in the team");
                            else
                                Member.create(repository, user, team, "Member");
                        },
                        () -> {
                            throw new UsernameNotFoundException("User not found");
                        }
                );
    }


    @Override
    public void changeRole(Principal connectedUser, Map<String, String> request, String teamName) {
        List<Member> members = getPerformerAndTarget(connectedUser, request, teamName);
        Member performer = members.get(0);
        Member target = members.get(1);

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
        Member performer = members.get(0);
        Member target = members.get(1);

        if (!performer.getRole().canChangeTo(target.getRole()))
            throw new IllegalArgumentException("You do not have permission to perform actions on this user");

        if (performer.equals(target) && performer.getRole() == RoleInTeam.LEADER)
            throw new IllegalArgumentException("First, put someone else in the leadership role");

        target.getPerformerTasks().forEach(task -> {
            if (task.getPerformer().equals(task.getCreator()))
                task.delete(taskRepository, commentRepository, oldTaskRepository);
            else {
                task.setPerformer(task.getCreator());
                taskRepository.save(task);
            }
        });

        repository.delete(target);
    }

    @Override
    public void changeEmailsMessagingStatus(String teamName, Principal connectedUser) {
        Member member = Member.getActionPerformer(
                repository,
                connectedUser,
                Team.findTeamByName(teamRepository, teamName)
        );
        member.setEmailsAllowed(!member.isEmailsAllowed());
        repository.save(member);
    }

    @Override
    public boolean getEmailsMessagingStatus(String teamName, Principal connectedUser) {
        return Member.getActionPerformer(
                repository,
                connectedUser,
                Team.findTeamByName(teamRepository, teamName)
        ).isEmailsAllowed();
    }

    @Override
    public List<MemberResponse> getAllByTeam(String team, Principal connectedUser) {
        return Team.findTeamByName(teamRepository, team).getMembers()
                .stream().map(converter::mapMemberToMemberResponse).collect(Collectors.toList());
    }

    private List<Member> getPerformerAndTarget(Principal connectedUser, Map<String, String> request, String teamName) {
        Member performer = Member.getActionPerformer(repository, connectedUser, Team.findTeamByName(teamRepository, teamName));

        String username = request.get("username");
        User target = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        var targetMember = repository.findByUserAndTeam(target, performer.getTeam())
                .orElseThrow(() -> new UsernameNotFoundException("Member not found"));

        List<Member> container = new ArrayList<>();
        container.add(performer);
        container.add(targetMember);

        return container;
    }
}
