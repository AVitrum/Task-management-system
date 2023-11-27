package com.vitrum.api.repositories;

import com.vitrum.api.data.submodels.Comment;
import com.vitrum.api.data.models.Task;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface CommentRepository extends MongoRepository<Comment, String> {

    List<Comment> findAllByTask(Task task);
}
