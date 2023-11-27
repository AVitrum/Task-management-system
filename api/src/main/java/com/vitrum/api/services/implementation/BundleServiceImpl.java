package com.vitrum.api.services.implementation;

import com.vitrum.api.data.enums.RoleInTeam;
import com.vitrum.api.data.models.*;
import com.vitrum.api.data.response.BundleResponse;
import com.vitrum.api.data.submodels.Comment;
import com.vitrum.api.data.submodels.OldTask;
import com.vitrum.api.repositories.*;
import com.vitrum.api.services.interfaces.BundleService;
import com.vitrum.api.util.Converter;
import com.vitrum.api.util.MessageUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BundleServiceImpl implements BundleService {

    private final BundleRepository repository;
    private final TeamRepository teamRepository;
    private final UserRepository userRepository;
    private final MemberRepository memberRepository;
    private final TaskRepository taskRepository;
    private final OldTaskRepository oldTaskRepository;
    private final CommentRepository commentRepository;
    private final MessageUtil messageUtil;
    private final Converter converter;

    @Override
    public void create(String teamName, String title) {
        var creator = findCreator(teamName);

        if (creator.checkPermissionToCreate())
            throw new IllegalArgumentException("You do not have permission to perform this action");

        if (repository.existsByCreatorAndTitle(creator, title))
            throw new IllegalArgumentException("Bundle with the same title exists");

        var bundle = Bundle.builder()
                .creator(creator)
                .title(title)
                .team(findTeamByName(teamName))
                .performer(creator)
                .build();
        repository.save(bundle);
    }

    @Override
    public void addPerformer(String teamName, String bundleTitle, String performerName) {
        var performer = findPerformer(performerName, teamName);

        if (repository.existsByPerformer(performer))
            throw new IllegalArgumentException(
                    String.format("This team member already has a task: %s", repository.findByPerformer(performer)
                            .orElseThrow(() -> new IllegalArgumentException("Bundle not found"))
                            .getTitle())
            );

        var creator = findCreator(teamName);
        var bundle = findBundleByCreator(creator);

        bundle.setPerformer(performer);

        repository.save(bundle);
        messageUtil.sendMessage(
                performer,
                "TMS Info!", String.format(
                        "Team: %s\n" +
                        "New tasks have been added to you by %s", teamName, creator.getUser().getEmail()
                )
        );
    }

    @Override
    public BundleResponse findByUser(String teamName) {
        var user = User.getAuthUser(userRepository);
        var member = findMemberByUsernameAndTeam(user.getTrueUsername(), teamName);
        Bundle bundle;

        if (repository.existsByCreator(member)) {
            bundle = findBundleByCreator(member);
        } else if (repository.existsByPerformer(member)) {
            bundle = findBundleByPerformer(member);
        } else {
            throw new IllegalArgumentException("Bundle not found");
        }

        return converter.mapBundleToBundleResponse(bundle);
    }

    @Override
    public BundleResponse findByTitle(String teamName, String bundleTitle) {
        var member = findMemberByUsernameAndTeam(User.getAuthUser(userRepository).getTrueUsername(), teamName);
        var team = findTeamByName(teamName);
        Bundle bundle = repository.findByTeamAndTitle(team, bundleTitle)
                .orElseThrow(() -> new IllegalArgumentException("Bundle not found"));

        if (!member.equals(bundle.getCreator())
                && !member.equals(bundle.getPerformer())
                && member.getRole().equals(RoleInTeam.MEMBER)
        ) throw new IllegalStateException("You cannot view other users' tasks");

        return converter.mapBundleToBundleResponse(bundle);
    }

    @Override
    public void deleteByTitle(String teamName, String bundleTitle) {
        var member = findMemberByUsernameAndTeam(User.getAuthUser(userRepository).getTrueUsername(), teamName);
        var team = findTeamByName(teamName);
        Bundle bundle = repository.findByTeamAndTitle(team, bundleTitle)
                .orElseThrow(() -> new IllegalArgumentException("Bundle not found"));

        if (!member.equals(bundle.getCreator())
                && member.getRole().equals(RoleInTeam.MEMBER)
        ) throw new IllegalStateException("You cannot delete tasks");

        List<Task> tasks = taskRepository.findAllByBundle(bundle);
        List<List<OldTask>> oldTasks = tasks.stream().map(oldTaskRepository::findAllByTask).toList();
        List<List<Comment>> comments = tasks.stream().map(commentRepository::findAllByTask).toList();

        comments.forEach(commentRepository::deleteAll);
        oldTasks.forEach(oldTaskRepository::deleteAll);
        taskRepository.deleteAll(tasks);
        repository.delete(bundle);
    }

    @Override
    public List<BundleResponse> findAll(String teamName) {
        var team =  teamRepository.findByName(teamName)
                .orElseThrow(() -> new IllegalArgumentException("Team not found"));
        var user = User.getAuthUser(userRepository);

        if (!memberRepository.existsByUserAndTeam(user, team))
            throw new IllegalArgumentException("You are not a team member");

        var member = memberRepository.findByUserAndTeam(user, team).get();

        if (member.getRole().equals(RoleInTeam.MEMBER))
            throw new IllegalStateException("You cannot view other users' tasks");

        List<Bundle> bundles = repository.findAllByTeam(team);

        return bundles.stream()
                .map(converter::mapBundleToBundleResponse)
                .collect(Collectors.toList());
    }

    private Member findMemberByUsernameAndTeam(String username, String teamName) {
        var team = findTeamByName(teamName);
        var user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return memberRepository.findByUserAndTeam(user, team)
                .orElseThrow(() -> new UsernameNotFoundException("Member not found"));
    }

    private Team findTeamByName(String teamName) {
        return teamRepository.findByName(teamName)
                .orElseThrow(() -> new IllegalArgumentException("Team not found"));
    }

    private Member findCreator(String teamName) {
        var user = User.getAuthUser(userRepository);
        return findMemberByUsernameAndTeam(user.getTrueUsername(), teamName);
    }

    private Member findPerformer(String performer, String teamName) {
        return findMemberByUsernameAndTeam(performer, teamName);
    }

    private Bundle findBundleByCreator(Member creator) {
        return repository.findByCreator(creator)
                .orElseThrow(() -> new IllegalArgumentException("Bundle not found"));
    }

    private Bundle findBundleByPerformer(Member performer) {
        return repository.findByPerformer(performer)
                .orElseThrow(() -> new IllegalArgumentException("Bundle not found"));
    }
}
