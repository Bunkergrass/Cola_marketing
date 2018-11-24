package com.htmessage.cola_marketing.runtimepermissions;

/**
 * Enum class to handle the different states
 * of permissions since the PackageManager only
 * has a granted and denied state.
 */
public enum Permissions {
    GRANTED,
    DENIED,
    NOT_FOUND
}
