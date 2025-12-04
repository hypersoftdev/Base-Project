package com.hypersoft.baseproject.core.permission.model

import com.hypersoft.baseproject.core.permission.enums.MediaPermission
import com.hypersoft.baseproject.core.permission.result.PermissionResult

data class PermissionRequest(
    val type: MediaPermission,
    val callback: (PermissionResult) -> Unit
)