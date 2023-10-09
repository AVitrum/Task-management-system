package com.vitrum.api.repository;

import com.vitrum.api.entity.TeamMembership;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamMembershipRepository extends JpaRepository<TeamMembership, Long> {

}
