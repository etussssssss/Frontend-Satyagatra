package com.example.projekconsultant.data

data class UserDataAjuinJadwalInternal (
    val nomorPendaftaran:String = "",
    val uid:String = "",

    val nama:String = "",
    val nomortelepon:String = "",
    val umur:String = "",
    val gender:String = "",
    val profesi:String = "",
    val fakultas:String = "",

    val PilihTanggal:String = "",
    val PilihLayananKonsultasi:String = "",
    val PilihTopikKonsultasi:String = "",
    val IsiCurhatan:String = "",

    var ButuhPersetujuan:Boolean? = false,
    var DiSetujui:Boolean? = false,

    var onoff:String = "",
    var konselor:String? = "",
    var typeIOrE:String = "",

    var domisili:String = "",
    var status:String = "",

)