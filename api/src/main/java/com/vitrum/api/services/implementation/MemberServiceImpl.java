package com.vitrum.api.services.implementation;

import com.vitrum.api.data.models.Member;
import com.vitrum.api.data.models.Team;
import com.vitrum.api.data.models.User;
import com.vitrum.api.data.enums.RoleInTeam;
import com.vitrum.api.repositories.BundleRepository;
import com.vitrum.api.repositories.MemberRepository;
import com.vitrum.api.repositories.TeamRepository;
import com.vitrum.api.repositories.UserRepository;
import com.vitrum.api.services.interfaces.MemberService;
import com.vitrum.api.util.MessageUtil;
import lombok.RequiredArgsConstructor;
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
    private final BundleRepository bundleRepository;
    private final MemberRepository memberRepository;
    private final MessageUtil messageUtil;

    @Override
    public void changeRole(Map<String, String> request, String teamName) {
        List<Member> members = getPerformerAndTarget(request, teamName);
        var performer = members.get(0);
        var target = members.get(1);

        RoleInTeam role = RoleInTeam.valueOf(request.get("role").toUpperCase());

        if (!performer.getRole().canChangeTo(role))
            throw new IllegalArgumentException("You do not have permission to perform actions on this user");

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

        if (!performer.getRole().canChangeTo(target.getRole()))
            throw new IllegalArgumentException("You do not have permission to perform actions on this user");

        if (performer.equals(target) && performer.getRole() == RoleInTeam.LEADER)
            throw new IllegalArgumentException("First, put someone else in the leadership role");

        if (bundleRepository.existsByCreatorAndPerformer(performer, target)) {
            var bundle = bundleRepository.findByCreatorAndPerformer(performer, target)
                    .orElseThrow(() -> new IllegalArgumentException("Bundle not found"));
            var creator = bundle.getCreator();

            bundle.setPerformer(creator);
            bundleRepository.save(bundle);

            creator.getPerformerBundles().add(bundle);
            memberRepository.save(creator);

            messageUtil.sendMessage(
                    creator,
                    teamName + " Info!",
                    String.format("The performer of your task (%s) has been removed from the team," +
                            " you are now the performer", target.getUser().getEmail())
            );
        }

        repository.delete(target);
    }

    @Override
    public void changeEmailsMessagingStatus(String teamName) {
        var team = teamRepository.findByName(teamName)
                .orElseThrow(() -> new IllegalArgumentException("Team not found"));
        var member = repository.findByUserAndTeam(User.getAuthUser(userRepository), team)
                .orElseThrow(() -> new IllegalArgumentException("Member not found"));

        member.setEmailsAllowed(!member.isEmailsAllowed());
        repository.save(member);
    }

    @Override
    public boolean getEmailsMessagingStatus(String teamName) {
        var team = teamRepository.findByName(teamName)
                .orElseThrow(() -> new IllegalArgumentException("Team not found"));
        var member = repository.findByUserAndTeam(User.getAuthUser(userRepository), team)
                .orElseThrow(() -> new IllegalArgumentException("Member not found"));

        return member.isEmailsAllowed();
    }

    private List<Member> getPerformerAndTarget(Map<String, String> request, String teamName) {
        var team = Team.findTeamByName(teamName, teamRepository);
        var members = team.getMembers();

        Member performer = findByKey(User.getUsername(userRepository), members);
        Member target = findByKey(request.get("username"), members);

        List<Member> container = new ArrayList<>();
        container.add(performer);
        container.add(target);

        return container;
    }

    private Member findByKey(String key, List<Member> members) {
        for (var member : members)
            if (member.getUser().getTrueUsername().equals(key))
                return member;
        throw new IllegalArgumentException("Member not found");
    }
}
