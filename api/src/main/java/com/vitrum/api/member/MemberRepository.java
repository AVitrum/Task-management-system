package com.vitrum.api.member;

import com.vitrum.api.credentials.user.User;
import com.vitrum.api.team.Team;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByUser(User user);
    Optional<Member> findByUserAndTeam(User user, Team team);
}
