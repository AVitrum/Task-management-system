package com.vitrum.api.repositories;

import com.vitrum.api.data.models.File;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileRepository extends JpaRepository<File, Long> {

}
