package com.vitrum.api.repository;

import com.vitrum.api.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamMembershipRepository extends JpaRepository<Member, Long> {

}
