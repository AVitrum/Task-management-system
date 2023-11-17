package com.vitrum.api.services.implementation;

import com.vitrum.api.dto.Response.BundleResponse;
import com.vitrum.api.models.Bundle;
import com.vitrum.api.models.Member;
import com.vitrum.api.models.User;
import com.vitrum.api.repositories.BundleRepository;
import com.vitrum.api.repositories.MemberRepository;
import com.vitrum.api.repositories.TeamRepository;
import com.vitrum.api.repositories.UserRepository;
import com.vitrum.api.services.BundleService;
import com.vitrum.api.util.Converter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BundleServiceImpl implements BundleService {

    private final BundleRepository repository;
    private final TeamRepository teamRepository;
    private final UserRepository userRepository;
    private final MemberRepository memberRepository;
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
                .performer(creator)
                .build();
        repository.save(bundle);
    }

    @Override
    public void addPerformer(String teamName, String bundleTitle, String performerName) {
        var creator = findCreator(teamName);
        var bundle = findBundleByCreator(bundleTitle, creator);
        var performer = findPerformer(performerName, teamName);

        bundle.setPerformer(performer);
        repository.save(bundle);
    }

    @Override
    public BundleResponse findByUser(String teamName, String bundleTitle) {
        var user = User.getAuthUser(userRepository);
        var member = findMemberByUsernameAndTeam(user.getTrueUsername(), teamName);

        Bundle bundle;
        if (repository.existsByCreatorAndTitle(member, bundleTitle)) {
            bundle = findBundleByCreator(bundleTitle, member);
        } else if (repository.existsByPerformerAndTitle(member, bundleTitle)) {
            bundle = findBundleByPerformer(bundleTitle, member);
        } else {
            throw new IllegalArgumentException("Bundle not found");
        }

        return converter.mapBundleToBundleResponse(bundle);

    }

    private Member findMemberByUsernameAndTeam(String username, String teamName) {
        var team = teamRepository.findByName(teamName)
                .orElseThrow(() -> new IllegalArgumentException("Team not found"));
        var user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return memberRepository.findByUserAndTeam(user, team)
                .orElseThrow(() -> new UsernameNotFoundException("Member not found"));
    }

    private Member findCreator(String teamName) {
        var user = User.getAuthUser(userRepository);
        return findMemberByUsernameAndTeam(user.getTrueUsername(), teamName);
    }

    private Member findPerformer(String performer, String teamName) {
        return findMemberByUsernameAndTeam(performer, teamName);
    }

    private Bundle findBundleByCreator(String bundleTitle, Member creator) {
        return repository.findByCreatorAndTitle(creator, bundleTitle)
                .orElseThrow(() -> new IllegalArgumentException("Bundle not found"));
    }

    private Bundle findBundleByPerformer(String bundleTitle, Member performer) {
        return repository.findByPerformerAndTitle(performer, bundleTitle)
                .orElseThrow(() -> new IllegalArgumentException("Bundle not found"));
    }
}
