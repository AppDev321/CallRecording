package com.example.callrecording.database.repository

import androidx.annotation.WorkerThread
import com.example.callrecording.database.dao.PhoneCallDAO
import com.example.callrecording.database.tables.PhoneCall
import kotlinx.coroutines.flow.Flow

class PhoneCallRepo(private val phoneCallDao: PhoneCallDAO) {

    val phoneCallFlowList: Flow<List<PhoneCall>> = phoneCallDao.getAllPhoneCalls()
    val incomingPhoneCallFlowList: Flow<List<PhoneCall>> = phoneCallDao.getIncomingCall()
    val outgoingPhoneCallFlowList: Flow<List<PhoneCall>> = phoneCallDao.getOutgoingCall()


    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(phoneCall: PhoneCall) {
        phoneCallDao.insertPhoneCall(phoneCall)
    }

    @WorkerThread
    suspend fun deleteAllRecords() {
        phoneCallDao.deleteAllData()
    }
}