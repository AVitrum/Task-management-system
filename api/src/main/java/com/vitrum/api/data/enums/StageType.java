package com.vitrum.api.data.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum StageType {
    REQUIREMENTS,
    DESIGN,
    IMPLEMENTATION,
    REVIEW,
    FINAL
}
