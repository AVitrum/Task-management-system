package com.vitrum.api.manager.task.history;

import com.vitrum.api.manager.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OldTaskRepository extends JpaRepository<OldTask, Long> {

    Optional<OldTask> findByMember(Member member);
    Optional<OldTask> findByTitleAndMember(String title, Member member);

    Optional<List<OldTask>> findAllByTitleAndMember(String title, Member member);
}
