package com.nabinbhandari.android.permissions

import android.app.Activity

/**
 * @author Dhruvaraj Nagarajan
 */
fun Activity.checkPermission(permissions: Array<String>, rationale: String?,
                             options: Permissions.Options?, handler: PermissionHandler) {
    Permissions.check(this, permissions, rationale, options, handler)
}


fun Activity.checkPermission(permissions: String, rationale: String?, handler: PermissionHandler) {
    Permissions.check(this, permissions, rationale, handler)
}