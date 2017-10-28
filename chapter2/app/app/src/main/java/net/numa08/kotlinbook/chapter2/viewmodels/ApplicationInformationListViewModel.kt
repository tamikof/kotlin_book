package net.numa08.kotlinbook.chapter2.viewmodels

import android.databinding.BaseObservable
import android.databinding.Bindable
import android.support.v4.util.Pair

import net.numa08.kotlinbook.chapter2.BR
import net.numa08.kotlinbook.chapter2.adapters.ApplicationInformationListAdapter
import net.numa08.kotlinbook.chapter2.databinding.ViewApplicationInformationListRowBinding
import net.numa08.kotlinbook.chapter2.models.ApplicationInformation
import net.numa08.kotlinbook.chapter2.models.ProcessInformation
import net.numa08.kotlinbook.chapter2.repositories.ApplicationInformationRepository
import net.numa08.kotlinbook.chapter2.repositories.ProcessInformationRepository

import java.util.*

typealias ApplicationInformationListViewModelOnClickListener = (ApplicationInformationListViewModel, Int, ViewApplicationInformationListRowBinding) -> Unit // typealias は class の中では利用できないのでパッケージ内で一意の名前をつける必要がある
class ApplicationInformationListViewModel(private val applicationInformationRepository: ApplicationInformationRepository, private val processInformationRepository: ProcessInformationRepository) : BaseObservable() {
    val adapter = ApplicationInformationListAdapter()
    private var onClickListener: OnClickListener? = null

    @get:Bindable
    var isLoading: Boolean = false
        private set(loading) {
            field = loading
            notifyPropertyChanged(BR.loading)
        }
    var isVisible: Boolean = false
        private set

    @get:Bindable
    var applicationInformationList: List<Pair<ApplicationInformation, ProcessInformation>>? = null
        private set(applicationInformationList) {
            val list = requireNotNull(applicationInformationList, { "リストにNullをセットしないでください" })
            field = list
            notifyPropertyChanged(BR.applicationInformationList)
            adapter.informationList.clear()
            adapter.informationList.addAll(list)
        }

    interface OnClickListener {
        fun onClickApplicationInformation(viewModel: ApplicationInformationListViewModel, index: Int, binding: ViewApplicationInformationListRowBinding)
    }

    fun setOnClickListener(onClickListener: (Any, Any, Any) -> Unit) {
        this.onClickListener = onClickListener
    }

    fun onCreate() {
        isVisible = true
        isLoading = true
        adapter.setOnItemClickListener { index, binding ->
            onClickListener?.invoke(this@ApplicationInformationListViewModel, index, binding)
        }
    }


    fun fetchApplication() {
        applicationInformationRepository.findAllApplications { list -> recursiveFetchProcessInformation(ArrayList(), list) }
    }

    private fun recursiveFetchProcessInformation(list: MutableList<Pair<ApplicationInformation, ProcessInformation>>, infoList: List<ApplicationInformation>) {
        if (infoList.isEmpty()) {
            isLoading = false
            applicationInformationList = list
            return
        }
        val info = infoList[0]
        processInformationRepository.findProcessInformationByName(info.packageName) { processInformation ->
            list.add(Pair.create(info, processInformation))
            val copy = ArrayList(infoList)
            copy.removeAt(0)
            recursiveFetchProcessInformation(list, copy)
            Unit
        }
    }

    fun onDestroy() {
        isVisible = false
    }
}