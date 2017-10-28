package net.numa08.kotlinbook.chapter2.viewmodels

import android.content.pm.ApplicationInfo
import android.graphics.drawable.ColorDrawable
import net.numa08.kotlinbook.chapter2.models.ApplicationInformation
import net.numa08.kotlinbook.chapter2.repositories.ApplicationInformationRepository
import org.hamcrest.core.Is.`is`
import org.junit.Assert.assertThat
import org.junit.Test
import org.junit.Assert.fail
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers
import org.robolectric.RobolectricTestRunner
import org.mockito.Mockito.*


/**
 * Created by fujinotamiko on 2017/10/28.
 */
@RunWith(RobolectricTestRunner::class)
class ApplicationInformationListViewModelTest {
    @Test
    fun isVisibleプロパティが遷移すること() {
        val viewModel = ApplicationInformationListViewModel(null)
        assertThat("初期状態では画面に表示されていない扱いとする",viewModel.isVisible, `is`(false))
        viewModel.onCreate() // (1)
        assertThat("画面表示は表示状態となる", viewModel.isVisible, `is`(true)) // (2)
        viewModel.onDestroy() // (3)
        assertThat("画面が終了した後は非表示状態となる", viewModel.isVisible, `is`(false)) // (4)
    }


    @Test
    fun isLoadingプロパティが遷移すること() {
        val viewModel = ApplicationInformationListViewModel(object : ApplicationInformationRepository { //(2)
            override fun findAllApplications(callback: ApplicationInformationRepository.FindAllApplicationsCallback?) { // (3)
                callback?.onFindAllApplications(emptyList())
            }

            override fun findApplicationByPackageName(packageName: String?, callback: ApplicationInformationRepository.FindApplicationCallback?) {
                fail("ここは呼ばれない") // (4)
            }
        })
        assertThat("初期状態ではロード中ではない", viewModel.isLoading, `is`(false)) // (5)
        viewModel.onCreate()
        assertThat("onCreateをするとロード状態となる", viewModel.isLoading, `is`(true)) // (6)
        viewModel.fetchApplication()
        assertThat("ロードが完了するとロード状態ではなくなる", viewModel.isLoading, `is`(false)) // (7)
    }

    @Test
    fun データが正しく読み込まれること() {
        val mock = mock(ApplicationInformationRepository::class.java) // (1)
        `when`(mock.findAllApplications(ArgumentMatchers.any())).then { // (2)
            val cb = it.arguments[0] as ApplicationInformationRepository.FindAllApplicationsCallback // (3)
            cb.onFindAllApplications(
                    listOf(
                            ApplicationInformation(
                                    "label 1",
                                    ColorDrawable(0),
                                    "description",
                                    0,
                                    0,
                                    0,
                                    0,
                                    "package1",
                                    ApplicationInfo()
                            ),
                            ApplicationInformation(
                                    "label 2",
                                    ColorDrawable(0),
                                    "description",
                                    0,
                                    0,
                                    0,
                                    0,
                                    "package2",
                                    ApplicationInfo()
                            ),
                            ApplicationInformation(
                                    "label 3",
                                    ColorDrawable(0),
                                    "description",
                                    0,
                                    0,
                                    0,
                                    0,
                                    "package3",
                                    ApplicationInfo()
                            )

                    )
            )
        }
        val viewModel = ApplicationInformationListViewModel(mock) // (4)
        viewModel.onCreate()
        viewModel.fetchApplication()
        assertThat("3件の情報が取得できていること", viewModel.applicationInformationList?.size, `is`(3)) // (5)
    }

}