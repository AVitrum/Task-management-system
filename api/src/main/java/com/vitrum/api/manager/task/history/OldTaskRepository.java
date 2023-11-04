package com.vitrum.api.manager.task.history;

import com.vitrum.api.manager.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OldTaskRepository extends JpaRepository<OldTask, Long> {

    Optional<OldTask> findByCreator(Member creator);
    Optional<OldTask> findByTitleAndCreator(String title, Member creator);

    Optional<List<OldTask>> findAllByTitleAndCreator(String title, Member creator);

    Optional<OldTask> findByTitleAndCreatorAndVersion(String title, Member creator, Long version);
}
