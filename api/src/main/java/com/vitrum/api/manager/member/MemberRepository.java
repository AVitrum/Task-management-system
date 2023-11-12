package com.vitrum.api.manager.member;

import com.vitrum.api.credentials.user.User;
import com.vitrum.api.manager.team.Team;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByUser(User user);
    Optional<Member> findByUserAndTeam(User user, Team team);
    Optional<List<Member>> findAllByUser(User user);
}
