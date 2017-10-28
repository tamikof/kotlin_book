package net.numa08.kotlinbook.chapter2.repositories

/**
 * Created by fujinotamiko on 2017/10/28.
 */
import net.numa08.kotlinbook.chapter2.models.ProcessInformation

interface ProcessInformationRepository {

    fun findProcessInformationByName(name: String, cb: ((ProcessInformation) -> Unit)) // (1)

}
