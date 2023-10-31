package com.vitrum.api.task;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Status {
    IN_PROGRESS, COMPLETED, CANCELED, ON_HOLD, ASSIGNED, OVERDUE, DELAYED, PENDING
}
