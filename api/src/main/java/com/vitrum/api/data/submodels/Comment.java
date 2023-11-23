package com.vitrum.api.data.submodels;

import com.vitrum.api.data.models.Member;
import com.vitrum.api.data.models.Task;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "comments")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Comment {

    @Id
    private String id;

    private String text;

    @DBRef
    private Member author;

    private LocalDateTime creationTime;

    @DBRef
    private Task task;

    @DBRef
    private OldTask oldTask;
}
