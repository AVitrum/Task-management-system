package com.vitrum.api.manager.task.main;

import com.vitrum.api.manager.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TaskRepository extends JpaRepository<Task, Long> {

    Optional<Task> findByMember(Member member);
    Optional<Task> findByTitleAndMember(String title, Member member);
}
