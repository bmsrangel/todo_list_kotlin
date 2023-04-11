package br.com.bmsrangel.dev.todolist.app.core.services.permissions

import android.app.Activity

interface PermissionsService {
    fun requestPermission(context: Activity, permission: String, requestCode: Int)
    fun isPermissionGranted(context: Activity, permission: String): Boolean
}