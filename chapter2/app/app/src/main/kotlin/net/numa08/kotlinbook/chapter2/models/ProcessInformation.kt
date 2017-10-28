package net.numa08.kotlinbook.chapter2.models

/**
 * Created by fujinotamiko on 2017/10/28.
 */
import android.annotation.TargetApi
import android.os.Build


sealed class ProcessInformation { // (1)

    abstract val packageName: String

    data class InActiveProcessInformation( // (2)
            override val packageName: String
    ) : ProcessInformation()

    data class ActiveProcessInformation(  // (3)
            override val packageName: String
    ) : ProcessInformation()

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    data class ActiveProcessInformationV21( // (4)
            override val packageName: String,
            val lastStartupTime: Long
    ) : ProcessInformation()
}
