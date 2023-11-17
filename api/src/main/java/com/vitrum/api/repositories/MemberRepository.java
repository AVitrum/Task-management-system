package com.vitrum.api.repositories;

import com.vitrum.api.models.Member;
import com.vitrum.api.models.User;
import com.vitrum.api.models.Team;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends MongoRepository<Member, String> {
    Optional<Member> findByUser(User user);
    Optional<Member> findByUserAndTeam(User user, Team team);

    Boolean existsByUserAndTeam(User user, Team team);
    List<Member> findAllByUser(User user);
    List<Member> findAllByTeam(Team team);
}
