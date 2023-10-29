package com.vitrum.api.recoverycode;

import com.vitrum.api.credentials.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Entity
@Table(name = "recoverycode")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Recoverycode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long code;
    private LocalTime creationTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    public User user;

    public boolean isExpired() {
        LocalTime creationTime = this.getCreationTime();
        LocalTime currentTime = LocalTime.now();
        return creationTime.plusMinutes(2).isBefore(currentTime);
    }
}
