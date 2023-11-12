package com.vitrum.api.manager.bundle;

import com.vitrum.api.credentials.user.User;
import com.vitrum.api.credentials.user.UserRepository;
import com.vitrum.api.manager.member.Member;
import com.vitrum.api.manager.member.MemberRepository;
import com.vitrum.api.manager.team.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.security.Principal;

@Service
@RequiredArgsConstructor
public class BundleService {

    private final BundleRepository repository;
    private final TeamRepository teamRepository;
    private final UserRepository userRepository;
    private final MemberRepository memberRepository;


    public void create(String teamName, Principal connectedUser, String title) {
        var creator = findCreator(connectedUser, teamName);

        var bundle = Bundle.builder()
                .creator(creator)
                .title(title)
                .performer(creator)
                .build();
        repository.save(bundle);
    }

    public void addPerformer(String teamName, String bundleTitle, Principal connectedUser, String performerName) {
        var creator = findCreator(connectedUser, teamName);
        var bundle = findBundle(bundleTitle, creator);
        var performer = findPerformer(performerName, teamName);

        bundle.setPerformer(performer);
        repository.save(bundle);
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

    private Member findPerformer(String performer, String teamName) {
        return findMemberByUsernameAndTeam(performer, teamName);
    }

    private Bundle findBundle(String bundleName, Member creator) {
        return repository.findByCreatorAndTitle(creator, bundleName)
                .orElseThrow(() -> new IllegalArgumentException("Bundle not found"));
    }

}
