package com.example.callrecording.database.dao

import androidx.room.*
import com.example.callrecording.database.tables.PhoneCall
import kotlinx.coroutines.flow.Flow


@Dao
interface PhoneCallDAO {

    @Query("SELECT * FROM phone_call ORDER BY id DESC")
    fun getAllPhoneCalls(): Flow<List<PhoneCall>>


    @Query("SELECT * FROM phone_call where type ='Incoming Call' ORDER BY id DESC ")
    fun getIncomingCall(): Flow<List<PhoneCall>>

    @Query("SELECT * FROM phone_call where type ='Outgoing Call' ORDER BY id DESC ")
    fun getOutgoingCall(): Flow<List<PhoneCall>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertPhoneCall(phoneCall: PhoneCall)

    @Query("DELETE FROM phone_call")
    suspend fun deleteAllData()


    @Delete
    suspend fun deletePhoneCall(phoneCall: PhoneCall)
}