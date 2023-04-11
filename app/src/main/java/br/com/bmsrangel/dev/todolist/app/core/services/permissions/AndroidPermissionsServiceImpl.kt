package br.com.bmsrangel.dev.todolist.app.core.services.permissions

import android.app.Activity
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import javax.inject.Inject

class AndroidPermissionsServiceImpl @Inject constructor(): PermissionsService {
    override fun requestPermission(context: Activity, permission: String, requestCode: Int) {
            ActivityCompat.requestPermissions(context, arrayOf(permission), requestCode)
    }

    override fun isPermissionGranted(context: Activity, permission: String): Boolean {
        return ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED
    }

}