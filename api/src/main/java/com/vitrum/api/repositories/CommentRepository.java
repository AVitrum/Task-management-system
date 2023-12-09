package com.vitrum.api.repositories;

import com.vitrum.api.data.models.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
}
