package com.vitrum.api.models.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Status {
    IN_PROGRESS,
    COMPLETED,
    ON_HOLD,
    PENDING,
    CANCELED,
    NOT_STARTED,
    ASSIGNED,
    OVERDUE,
    APPROVED,
    REJECTED,
    RESTORED,
    IN_REVIEW,
    IN_TESTING,
    ON_TRACK,
    DELAYED,
    DELETED
}
