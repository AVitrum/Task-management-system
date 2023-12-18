package com.vitrum.api.data.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum StageType {
    REQUIREMENTS,
    PROJECTING,
    IMPLEMENTATION,
    REVIEW,
    FINAL
}
