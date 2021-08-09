package com.app.libraryseatbooking.ui.scannerDetails

import androidx.lifecycle.*
import com.app.libraryseatbooking.data.source.IDataRepository
import com.app.libraryseatbooking.pojo.QRCodeData
import com.app.libraryseatbooking.pojo.ResultState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class ScannerDetailsViewModel @Inject constructor(
    private val dataRepository: IDataRepository,
    private val dispatcher: CoroutineDispatcher
) : ViewModel() {

    private val _dataLoading = MutableLiveData<Boolean>()
    val dataLoading: LiveData<Boolean> = _dataLoading

    private val _onGoingSession = MutableLiveData<ResultState>()
    val onGoingSession: LiveData<ResultState> = _onGoingSession

    fun getOnGoingSession() {
        _dataLoading.postValue(true)
        viewModelScope.launch(dispatcher) {
            dataRepository.getOnGoingSessionQRCodeData().collect { resultState ->
                _onGoingSession.postValue(resultState)
            }
        }.invokeOnCompletion {
            _dataLoading.postValue(false)
        }
    }

    fun startOrStopSession(qrCodeData: QRCodeData) {
        _dataLoading.postValue(true)
        viewModelScope.launch(dispatcher) {
            dataRepository.startOrStopSession(qrCodeData, Calendar.getInstance().timeInMillis)
                .collect { resultState ->
                    _onGoingSession.postValue(resultState)
                }
        }.invokeOnCompletion {
            _dataLoading.postValue(false)
        }
    }

}

@Suppress("UNCHECKED_CAST")
class ScannerDetailsModelFactory @Inject constructor(
    private val dataRepository: IDataRepository,
    private val dispatcher: CoroutineDispatcher
) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>) =
        (ScannerDetailsViewModel(dataRepository, dispatcher) as T)
}