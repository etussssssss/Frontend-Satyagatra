package com.example.projekconsultant.methodsNService

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.projekconsultant.adapter.ReviewItem
import com.example.projekconsultant.adapter.SelesaiDetailReviewUser
import com.example.projekconsultant.adapter.SelesaiData
import com.example.projekconsultant.data.UserSelesai

class SharedViewModel : ViewModel() {
    val ruid = MutableLiveData<String>()

    val namaUser = MutableLiveData<String>()
    val nomorTelepon = MutableLiveData<String>()
    val umur = MutableLiveData<String>()
    val gender = MutableLiveData<String>()
    val profesi = MutableLiveData<String>()
    val fakultas = MutableLiveData<String>()

    val status = MutableLiveData<String>()
    val domisili = MutableLiveData<String>()

    val typeUserIORE = MutableLiveData<String>()
    val statusPendaftaran = MutableLiveData<Boolean>().apply { value = true }


    val riwayatNoPendaftaran = MutableLiveData<String>()
    val riwayatTopik = MutableLiveData<String>()
    val riwayatLayanan = MutableLiveData<String>()
    val riwayatTanggal = MutableLiveData<String>()
    val riwayatOffOrOn = MutableLiveData<String>()
    val riwayatDescTopik = MutableLiveData<String>()


    val riwayatStatusButuhPersetujuan = MutableLiveData<Boolean>().apply { value = false }
    val riwayatStatusDisetujui = MutableLiveData<Boolean>().apply { value = false }
    val riwayatStatusSelesai = MutableLiveData<Boolean>().apply { value = false }


    private val _datasUserSelesai = MutableLiveData<ArrayList<UserSelesai>>()
    fun updateDataUserSelesai(updatedData: ArrayList<UserSelesai>) {
        _datasUserSelesai.value = updatedData
    }
    val dataUserSelesai: LiveData<ArrayList<UserSelesai>> = _datasUserSelesai

    private val _datasReviewAllUser = MutableLiveData<ArrayList<ReviewItem>>()
    fun updateDataReviewAllUser(updatedData: ArrayList<ReviewItem>) {
        _datasReviewAllUser.value = updatedData
    }
    val dataReviewAllUser: LiveData<ArrayList<ReviewItem>> = _datasReviewAllUser


    private val _datasSelesai = MutableLiveData<ArrayList<SelesaiData>>()
    fun updateDataSelesai(updatedData: ArrayList<SelesaiData>) {
        _datasSelesai.value = updatedData
    }
    val dataSelesai: LiveData<ArrayList<SelesaiData>> = _datasSelesai
}








//private val _dataDetailReview = MutableLiveData<ArrayList<SelesaiDetailReviewUser>>()
//fun updateDataDetailReview(updatedData: ArrayList<SelesaiDetailReviewUser>) {
//    _dataDetailReview.value = updatedData
//}
//val dataDetailReview: LiveData<ArrayList<SelesaiDetailReviewUser>> = _dataDetailReview
//
//
//private val _NomorPendaftaranSelesai = MutableLiveData<ArrayList<String>>()
//fun updateRiwayatNomorPendaftaran(updatedData: ArrayList<String>) {
//    _NomorPendaftaranSelesai.value = updatedData
//}
//val nomorPendaftaranSelesai: LiveData<ArrayList<String>> = _NomorPendaftaranSelesai
