package com.example.projekconsultant.admin

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.projekconsultant.adapter3sealedclass.ChatsUsers

class SharedViewModelAdmin : ViewModel() {

    private val _dataRiwayatSelesaiAdmin = MutableLiveData<ArrayList<DataRiwayatSelesaiAdmin>>()
    val dataRiwayatSelesaiAdmin: LiveData<ArrayList<DataRiwayatSelesaiAdmin>> = _dataRiwayatSelesaiAdmin
    fun updateDataRiwayatSelesaiAdmin(updatedData: ArrayList<DataRiwayatSelesaiAdmin>) {
        _dataRiwayatSelesaiAdmin.value = updatedData
    }



    private val _dataPendaftaran = MutableLiveData<ArrayList<DataPendaftaran>>()
    val dataPendaftaran: LiveData<ArrayList<DataPendaftaran>> = _dataPendaftaran
    fun updateDataPendaftaran(updatedData: ArrayList<DataPendaftaran>) {
        _dataPendaftaran.value = updatedData
    }


    private val _dataChat = MutableLiveData<ArrayList<ChatsUsers>>()
    val dataChat: LiveData<ArrayList<ChatsUsers>> = _dataChat
    fun updateDataChat(updatedData: ArrayList<ChatsUsers>) {
        _dataChat.value = updatedData
    }
}

