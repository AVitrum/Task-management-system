package com.vitrum.api.data.submodels;

import com.vitrum.api.data.models.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalTime;

@Document(collection = "recoverycodes")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Recoverycode {

    @Id
    private String id;

    private Long code;
    private LocalTime creationTime;

    @DBRef
    private User user;

    public boolean isExpired() {
        LocalTime creationTime = this.getCreationTime();
        LocalTime currentTime = LocalTime.now();
        return creationTime.plusMinutes(2).isBefore(currentTime);
    }
}
