package com.vitrum.api.data.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Status {
    COMPLETED,
    UNCOMPLETED,
    PENDING,
    ASSIGNED,
    OVERDUE,
    APPROVED,
    IN_REVIEW,
    DELETED,
    NOW_UNAVAILABLE,
}
