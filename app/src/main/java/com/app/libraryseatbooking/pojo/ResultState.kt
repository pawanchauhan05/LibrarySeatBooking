package com.app.libraryseatbooking.pojo

sealed class ResultState {
    //data class Success(var qrCodeData: QRCodeData) : ResultState()
    //data class Failure(val count : Int) : ResultState()

    data class NoSession(val message : String) : ResultState()
    data class StartSession(val qrCodeData: QRCodeData) : ResultState()
    data class Failure(val exception : Exception) : ResultState()
    data class Progress(val isShow : Boolean) : ResultState()
    data class Success(var serverResponse: ServerResponse) : ResultState()
}
