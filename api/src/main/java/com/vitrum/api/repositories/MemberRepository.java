package com.vitrum.api.repositories;

import com.vitrum.api.data.models.Member;
import com.vitrum.api.data.models.User;
import com.vitrum.api.data.models.Team;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByUserAndTeam(User user, Team team);

    Optional<List<Member>> findAllByUser(User user);

    Boolean existsByUserAndTeam(User user, Team team);
}
