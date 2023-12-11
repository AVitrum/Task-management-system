package com.vitrum.api.services.implementations;

import com.vitrum.api.data.enums.RoleInTeam;
import com.vitrum.api.data.models.Bundle;
import com.vitrum.api.data.models.Team;
import com.vitrum.api.data.models.User;
import com.vitrum.api.data.request.BundleRequest;
import com.vitrum.api.repositories.*;
import com.vitrum.api.data.response.BundleResponse;
import com.vitrum.api.data.models.Member;
import com.vitrum.api.services.interfaces.BundleService;
import com.vitrum.api.services.interfaces.OldTaskService;
import com.vitrum.api.util.Converter;
import com.vitrum.api.util.MessageUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BundleServiceImpl implements BundleService {

    private final BundleRepository repository;
    private final TeamRepository teamRepository;
    private final UserRepository userRepository;
    private final MemberRepository memberRepository;
    private final OldTaskService oldTaskService;
    private final TaskRepository taskRepository;
    private final MessageUtil messageUtil;
    private final Converter converter;

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public void create(String teamName, Principal connectedUser, BundleRequest request) {
        var creator = findCreator(connectedUser, teamName);

        if (creator.checkPermission())
            throw new IllegalArgumentException("You do not have permission to perform this action");

        if (repository.existsByTitleAndTeam(request.getTitle(), creator.getTeam()))
            throw new IllegalArgumentException("Bundle with the same name already exists");

        var bundle = Bundle.builder()
                .creator(creator)
                .title(request.getTitle().replaceAll("\\s", "_"))
                .performer(creator)
                .team(creator.getTeam())
                .assignmentDate(LocalDateTime.now())
                .changeTime(LocalDateTime.now())
                .dueDate(LocalDateTime.parse(request.getDueDate(), formatter))
                .build();
        repository.save(bundle);
    }

    @Override
    public void addPerformer(String teamName, String bundleTitle, Principal connectedUser, String performerName) {
        var performer = findMemberByUsernameAndTeam(performerName, teamName);

        if (repository.existsByPerformer(performer))
            throw new IllegalArgumentException(
                    String.format("This team member already has a task: %s", repository.findByPerformer(performer)
                            .orElseThrow(() -> new IllegalArgumentException("Bundle not found"))
                            .getTitle())
            );

        var bundle = Bundle.getBundleWithDateCheck(
                repository,
                teamRepository,
                taskRepository,
                teamName,
                bundleTitle
        );
        var actionPerformer = Member.getActionPerformer(memberRepository, connectedUser, bundle.getTeam());

        if (!actionPerformer.equals(bundle.getCreator())
                && actionPerformer.checkPermission()
        ) throw new IllegalStateException("You do not have permission for this action");

        bundle.setPerformer(performer);
        bundle.setAssignmentDate(LocalDateTime.now());
        repository.save(bundle);

        messageUtil.sendMessage(
                performer,
                "TMS Info!", String.format(
                        "Team: %s\n" +
                                "New tasks have been added to you by %s", teamName, actionPerformer.getUser().getEmail()
                )
        );
    }

    @Override
    public void update(String teamName, String bundleTitle, Principal connectedUser, String dueDate) {
        var bundle = Bundle.findBundle(
                repository,
                Team.findTeamByName(teamRepository, teamName),
                bundleTitle
        );
        var actionPerformer = Member.getActionPerformer(memberRepository, connectedUser, bundle.getTeam());

        if (!actionPerformer.equals(bundle.getCreator())
                && actionPerformer.checkPermission()
        ) throw new IllegalStateException("You do not have permission for this action");

        bundle.setDueDate(LocalDateTime.parse(dueDate, formatter));
        bundle.saveChangeDate(repository);
    }

    @Override
    public List<BundleResponse> findAll(String teamName, Principal connectedUser) {
        var actionPerformer = Member.getActionPerformer(
                memberRepository,
                connectedUser,
                Team.findTeamByName(teamRepository, teamName)
        );

        List<Bundle> bundles;

        if (!actionPerformer.getRole().equals(RoleInTeam.MEMBER))
            bundles = repository.findAllByTeam(Team.findTeamByName(teamRepository, teamName));
        else
            bundles = actionPerformer.getPerformerBundles();

        bundles.forEach(bundle -> bundle.checkDate(taskRepository));
        return bundles.stream().map(converter::mapBundleToBundleResponse).collect(Collectors.toList());
    }

    @Override
    public Bundle findByTitle(String teamName, String bundleTitle, Principal connectedUser) {
        Bundle bundle = Bundle.findBundle(
                repository,
                Team.findTeamByName(teamRepository, teamName),
                bundleTitle);

        bundle.checkDate(taskRepository);

        Member actionPerformer = Member.getActionPerformer(memberRepository, connectedUser, bundle.getTeam());

        if (!actionPerformer.equals(bundle.getCreator())
                && !actionPerformer.equals(bundle.getPerformer())
                && actionPerformer.checkPermission()
        ) throw new IllegalStateException("You cannot view other users' tasks");

        return bundle;
    }

    @Override
    public void deleteByTitle(String teamName, String bundleTitle, Principal connectedUser) {
        Bundle bundle = findByTitle(teamName, bundleTitle, connectedUser);

        bundle.getTasks().forEach(
                task -> oldTaskService.delete(
                        task.getTitle(), bundle.getTeam().getName(), bundle.getTitle(), connectedUser)
        );

        repository.delete(bundle);
    }

    private Member findMemberByUsernameAndTeam(String username, String teamName) {
        var team = teamRepository.findByName(teamName)
                .orElseThrow(() -> new IllegalArgumentException("Team not found"));
        var user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return memberRepository.findByUserAndTeam(user, team)
                .orElseThrow(() -> new UsernameNotFoundException("Member not found"));
    }

    private Member findCreator(Principal connectedUser, String teamName) {
        var user = User.getUserFromPrincipal(connectedUser);
        return findMemberByUsernameAndTeam(user.getTrueUsername(), teamName);
    }

}
