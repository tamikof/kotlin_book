package net.numa08.kotlinbook.chapter2.repositories.impl

import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Handler
import android.os.Looper
import android.support.v7.graphics.Palette
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.async

import net.numa08.kotlinbook.chapter2.models.ApplicationInformation
import net.numa08.kotlinbook.chapter2.repositories.ApplicationInformationRepository

import java.util.ArrayList
import java.util.concurrent.Callable
import java.util.concurrent.ExecutionException
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.Future

class ApplicationInformationRepositoryImpl(private val packageManager: PackageManager) : ApplicationInformationRepository {

    override fun findAllApplications(cb: ((List<ApplicationInformation>) -> Unit)) {
        val callbackHandler = Handler(Looper.myLooper())
        Thread(Runnable {
            val applications = findAllApplications()
            callbackHandler.post { cb(applications) }
        }).start()

    }

    override fun findApplicationByPackageName(packageName: String, cb: (ApplicationInformation?) -> Unit) {
        val callbackHandler = Handler(Looper.myLooper())
        Thread(Runnable {
            val applicationInformation = findApplicationByPackageName(packageName)
            callbackHandler.post { cb(applicationInformation) }
        }).start()
    }

    private fun findApplicationByPackageName(packageName: String): ApplicationInformation? {
        return try {
            val applicationInfo = packageManager.getApplicationInfo(packageName, 0)
            convertApplicationInfo(applicationInfo)
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            null
        }
    }

    override fun findAllApplicationsAsync(): Deferred<List<ApplicationInformation>> = async {
        return@async findAllApplications()
    }

    override fun findApplicationByPackageNameAsync(packageName: String): Deferred<ApplicationInformation?> = async{
        return@async findApplicationByPackageName(packageName)
    }

    private fun findAllApplications(): List<ApplicationInformation> {
        val exec = Executors.newFixedThreadPool(5)
        val apps =
                packageManager.getInstalledApplications(0)
                        .filter { it.flags and ApplicationInfo.FLAG_SYSTEM == 0 }
                        .map { exec.submit(Callable { convertApplicationInfo(it) }) }
                        .mapNotNull { task ->
                            try {
                                task.get()
                            } catch (e: InterruptedException) {
                                e.printStackTrace()
                                null
                            } catch (e: ExecutionException) {
                                e.printStackTrace()
                                null
                            }
                        }
        exec.shutdown()
        return apps
    }

    private fun convertApplicationInfo(appInfo: ApplicationInfo): ApplicationInformation {
        val label = appInfo.loadLabel(packageManager)
        val icon = appInfo.loadIcon(packageManager)
        val description = appInfo.loadDescription(packageManager)
        val lightVibrantRGB: Int?
        val vibrantRGB: Int?
        val bodyTextColor: Int?
        val titleTextColor: Int?
        if (icon is BitmapDrawable) {
            val bitmap = icon.bitmap
            val palette = Palette.Builder(bitmap).generate()
            val lightVibrant = palette.lightVibrantSwatch
            lightVibrantRGB = lightVibrant?.rgb //(1)
            bodyTextColor = lightVibrant?.bodyTextColor
            titleTextColor = lightVibrant?.titleTextColor
            val vibrantSwatch = palette.vibrantSwatch
            vibrantRGB = vibrantSwatch?.rgb
        } else {
            titleTextColor = null
            bodyTextColor = titleTextColor
            vibrantRGB = bodyTextColor
            lightVibrantRGB = vibrantRGB
        }
        return ApplicationInformation(
                label,
                icon,
                description,
                lightVibrantRGB,
                vibrantRGB,
                bodyTextColor,
                titleTextColor,
                appInfo.packageName,
                appInfo)
    }
}
