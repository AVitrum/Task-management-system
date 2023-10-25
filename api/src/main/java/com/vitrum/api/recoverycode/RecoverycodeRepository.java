package com.vitrum.api.recoverycode;

import com.vitrum.api.credentials.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RecoverycodeRepository extends JpaRepository<Recoverycode, Long> {

    Optional<Recoverycode> findByUser(User user);
}
