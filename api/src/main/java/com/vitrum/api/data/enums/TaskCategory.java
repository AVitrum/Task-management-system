package com.vitrum.api.data.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TaskCategory {
    BACKEND,
    FRONTEND,
    MOBILE,
    HIGH_PRIORITY,
    BROWSER,
    DESIGN,
    MARKETING,
}
