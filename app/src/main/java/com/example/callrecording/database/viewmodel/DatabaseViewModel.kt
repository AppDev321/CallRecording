package com.example.callrecording.database.viewmodel

import androidx.lifecycle.*
import com.example.callrecording.database.repository.PhoneCallRepo
import com.example.callrecording.database.tables.PhoneCall
import kotlinx.coroutines.launch


class DatabaseViewModelFactory(private val repository: PhoneCallRepo) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DatabaseViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DatabaseViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}


class DatabaseViewModel (private val repository: PhoneCallRepo) : ViewModel() {


    val getAllPhoneCallList: LiveData<List<PhoneCall>> = repository.phoneCallFlowList.asLiveData()


    fun insertPhoneCallData(call: PhoneCall) = viewModelScope.launch {
        repository.insert(call)
    }


}
