package com.vitrum.api.repositories;

import com.vitrum.api.models.User;
import com.vitrum.api.models.submodels.Recoverycode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RecoverycodeRepository extends JpaRepository<Recoverycode, Long> {

    Optional<Recoverycode> findByUser(User user);
}
