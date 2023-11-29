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
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
        var team = Team.findTeamByName(teamName, teamRepository);
        var creator = Member.findCreator(team, userRepository);

        if (creator.checkPermissionToCreate())
            throw new IllegalArgumentException("You do not have permission to perform this action");

        if (repository.existsByCreatorAndTitle(creator, title))
            throw new IllegalArgumentException("Bundle with the same title exists");

        var bundle = Bundle.builder()
                .creator(creator)
                .title(title)
                .team(team)
                .tasks(new ArrayList<>())
                .performer(creator)
                .build();
        repository.save(bundle);

        team.getBundles().add(bundle);
        teamRepository.save(team);

        creator.getCreatorBundles().add(bundle);
        creator.getPerformerBundles().add(bundle);
        memberRepository.save(creator);
    }

    @Override
    public void addPerformer(String teamName, String bundleTitle, String performerName) {
        var team = Team.findTeamByName(teamName, teamRepository);
        var performer = Member.findPerformer(performerName, team);

        if (repository.existsByPerformer(performer))
            throw new IllegalArgumentException(
                    String.format("This team member already has a task: %s", repository.findByPerformer(performer)
                            .orElseThrow(() -> new IllegalArgumentException("Bundle not found"))
                            .getTitle())
            );

        var creator = Member.findCreator(team, userRepository);
        Bundle bundle = Bundle.findBundleByCreator(creator, bundleTitle);

        bundle.setPerformer(performer);

        performer.getPerformerBundles().add(bundle);
        memberRepository.save(performer);

        creator.getPerformerBundles().remove(bundle);
        memberRepository.save(creator);

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
    public BundleResponse findByTitle(String teamName, String title) {
        var team = Team.findTeamByName(teamName, teamRepository);
        var member = Member.findMemberByKey(User.getUsername(userRepository), team.getMembers());
        Bundle bundle;

        if (repository.existsByCreator(member))
            bundle = Bundle.findBundleByCreator(member, title);
        else if (repository.existsByPerformer(member))
            bundle = Bundle.findBundleByPerformer(member, title);
        else throw new IllegalArgumentException("Bundle not found");

        return converter.mapBundleToBundleResponse(bundle);
    }

    @Override
    public List<BundleResponse> findAll(String teamName) {
        var team = Team.findTeamByName(teamName, teamRepository);
        var member = Member.findMemberByKey(User.getUsername(userRepository), team.getMembers());

        if (member.getRole().equals(RoleInTeam.MEMBER))
            throw new IllegalStateException("You cannot view other users' tasks");

        List<Bundle> bundles = team.getBundles();

        return bundles.stream()
                .map(converter::mapBundleToBundleResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteByTitle(String teamName, String bundleTitle) {
        var team = Team.findTeamByName(teamName, teamRepository);
        var member = Member.findMemberByKey(User.getUsername(userRepository), team.getMembers());
        Bundle bundle = Bundle.findBundleByTeam(team, bundleTitle);

        if (!member.equals(bundle.getCreator())
                && member.getRole().equals(RoleInTeam.MEMBER)
        ) throw new IllegalStateException("You cannot delete tasks");

        List<Task> tasks = bundle.getTasks();
        List<List<OldTask>> oldTasks = tasks.stream().map(Task::getOldTasks).toList();
        List<List<Comment>> comments = tasks.stream().map(Task::getComments).toList();

        comments.forEach(commentRepository::deleteAll);
        oldTasks.forEach(oldTaskRepository::deleteAll);
        taskRepository.deleteAll(tasks);

        var creator = bundle.getCreator();
        creator.getCreatorBundles().remove(bundle);
        creator.getPerformerBundles().remove(bundle);
        memberRepository.save(creator);

        var performer = bundle.getPerformer();
        performer.getPerformerBundles().remove(bundle);
        memberRepository.save(performer);

        repository.delete(bundle);
    }
}
