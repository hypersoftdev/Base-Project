package com.hypersoft.baseproject.core.permission.result

/**
 * Result of a permission request.
 */
sealed class PermissionResult {
    object GrantedFull : PermissionResult()
    object GrantedLimited : PermissionResult() // used for Android 14+ selected-photos grants (best-effort)
    object Denied : PermissionResult()
}