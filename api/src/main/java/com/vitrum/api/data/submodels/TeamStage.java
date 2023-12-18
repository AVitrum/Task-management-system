package com.vitrum.api.data.submodels;

import com.vitrum.api.data.enums.StageType;
import com.vitrum.api.data.models.Team;
import com.vitrum.api.repositories.TeamStageRepository;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Entity
@Table(name = "stage")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TeamStage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private StageType type;
    private LocalDateTime dueDate;
    private Boolean isCurrent;
    private Long number;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id")
    private Team team;

    public static TeamStage create(
            TeamStageRepository repository,
            Team team,
            StageType type,
            String dueDateString,
            Boolean isCurrent,
            Long number
    ) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime dueDate = LocalDateTime.now().plusDays(1);

        if (dueDateString != null)
            dueDate = LocalDateTime.parse(dueDateString, formatter);

        TeamStage stage = TeamStage.builder()
                .team(team)
                .dueDate(dueDate)
                .type(type)
                .isCurrent(isCurrent)
                .number(number)
                .build();
        repository.save(stage);
        return stage;
    }
}
