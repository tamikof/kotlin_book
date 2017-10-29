package net.numa08.kotlinbook.chapter2.viewmodels

import android.content.Context
import android.databinding.BaseObservable
import android.databinding.Bindable
import android.graphics.drawable.Drawable
import android.widget.ArrayAdapter

import net.numa08.kotlinbook.chapter2.BR
import net.numa08.kotlinbook.chapter2.models.ApplicationInformation
import net.numa08.kotlinbook.chapter2.repositories.ApplicationInformationRepository

import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch

class ApplicationInformationViewModel(injector: Injector, private val applicationInformationRepository: ApplicationInformationRepository) : BaseObservable() {

    @get:Bindable
    var applicationInformation: ApplicationInformation? = null
        private set(applicationInformation) {
            field = applicationInformation
            notifyPropertyChanged(BR.icon)
            notifyPropertyChanged(BR.applicationInformation)
            val list = applicationInformation?.applicationInfo?.let {
                listOf(
                        it.className,
                        it.backupAgentName,
                        it.dataDir,
                        it.manageSpaceActivityName,
                        it.nativeLibraryDir,
                        it.permission,
                        it.publicSourceDir,
                        it.sourceDir,
                        it.taskAffinity
                ).filterNotNull()
            }
            if (list != null) {
                adapter.addAll(list)
            }
        }

    val adapter: ArrayAdapter<String> = ArrayAdapter(injector.context, android.R.layout.simple_list_item_1, android.R.id.text1)

    private var isVisible: Boolean = false

    val icon: Drawable?
        @Bindable get() = applicationInformation?.icon

    interface Injector {
        val context: Context
    }

    fun onCreate() {
        isVisible = true
    }

    var job: Job? = null
        private set

    fun fetchApplication(packageName: String) {
        job = launch(UI) { // (1)
            val info = applicationInformationRepository.findApplicationByPackageNameAsync(packageName).await() // (2)
            if (info != null) {
                applicationInformation = info
            }
        }
    }

    fun onDestroy() {
        isVisible = false
        job?.cancel() // (3)
    }
}
