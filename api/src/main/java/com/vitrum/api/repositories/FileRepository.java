package com.vitrum.api.repositories;

import com.vitrum.api.data.models.File;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FileRepository extends JpaRepository<File, Long> {
    Optional<File> findByName(String name);

    Boolean existsByName(String name);
}
