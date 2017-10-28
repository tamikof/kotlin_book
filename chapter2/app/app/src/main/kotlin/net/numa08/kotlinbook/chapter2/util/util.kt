package net.numa08.kotlinbook.chapter2.util

import android.annotation.TargetApi
import android.app.AppOpsManager
import android.content.Context
import android.os.Build
import android.os.Process

/**
 * Created by fujinotamiko on 2017/10/28.
 */
object ContextExtensions {
    @JvmStatic
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    fun Context.isAllowedUsageStatsAccess(): Boolean {
        val manager = getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val uid = Process.myUid()
        val mode = manager.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, uid, packageName)
        return mode == AppOpsManager.MODE_ALLOWED
    }
}