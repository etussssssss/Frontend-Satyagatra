package com.example.projekconsultant.admin

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.projekconsultant.BaseActivity
import com.example.projekconsultant.R
import com.example.projekconsultant.adapter.ProfileKonselorHome
import com.example.projekconsultant.adapter2.AdapterPilihKonselor

class PilihKonselor : BaseActivity() {
    private lateinit var recy:RecyclerView
    private lateinit var nomorPendaftaran:String
    private lateinit var tanggalKonseling:String
    private lateinit var layananKonseling:String
    private lateinit var ruid:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pilih_konselor)

        recy = findViewById(R.id.recyclerViewPilih1)
        nomorPendaftaran = intent.getStringExtra("NOMORPENDAFTARAN").toString()
        tanggalKonseling = intent.getStringExtra("TANGGALKONSELING").toString()
        layananKonseling = intent.getStringExtra("LAYANANKONSELING").toString()
        ruid = intent.getStringExtra("RUID").toString()


        // Gunakan GridLayoutManager dengan 3 kolom
        val gridLayoutManager = GridLayoutManager(this, 3)
        recy.layoutManager = gridLayoutManager

        val adapter = AdapterPilihKonselor(profileList, nomorPendaftaran, tanggalKonseling, layananKonseling, ruid){
            finish()
        }
        recy.adapter = adapter

        findViewById<ImageView>(R.id.backPendaftaran).setOnClickListener {
            finish()
        }
    }

    private val profileList = listOf(
        ProfileKonselorHome("dr. Maya Trisiswati, MKM.", R.drawable.s_img_drmaya, listOf("HIV", "Pos Sapa/Kekerasan", "Kesehatan Reproduksi"), R.string.deskripDrMaya),
        ProfileKonselorHome("dr. Liko Maryudhiyanto, Sp.KJ., C.Ht", R.drawable.s_img_drliko, listOf("Motivasi Belajar", "Kesehatan Mental dan Psikologi"), R.string.deskripDrLiko),
        ProfileKonselorHome("dr. Melok Roro Kinanthi, M.Psi., Psikolog", R.drawable.s_img_drmelok, listOf("Keluarga", "Pos Sapa/Kekerasan", "Parenting", "Kesehatan Mental dan Psikologi"), R.string.deskripDrMelok),
        ProfileKonselorHome("dr. Octaviani Indrasari Ranakusuma, M.Si., Psi.", R.drawable.s_img_droktaviani, listOf("Keluarga", "Parenting", "Kesehatan Mental dan psikologi"), R.string.deskripDrOctaviani),
        ProfileKonselorHome("Dr. Miwa Patnani, M.Si., Psikolog", R.drawable.s_img_drmiwa, listOf("Kesehatan Mental dan Psikologi"), R.string.deskripDrMiwa),
        ProfileKonselorHome("Fitri Arlinkasari, Ph.D., Psikolog", R.drawable.s_img_drfitri, listOf("Agama"), R.string.deskripDrFitriArlinkasari),
        ProfileKonselorHome("Aya Yahya Maulana, Lc, M.H.", R.drawable.s_img_draya, listOf("Gizi"), R.string.deskripDrayaYahya),
        ProfileKonselorHome("dr. Siti Maulidya Sari, M.Epid, Dipl.DK", R.drawable.s_img_drsiti, listOf("Kesehatan Mental dan Psikologi"), R.string.deskripDrSitiMaulidya),
        ProfileKonselorHome("Dilfa Juniar, M.Psi., Psikolog", R.drawable.s_img_drdiffa, listOf("Motivasi Belajar"), R.string.deskripDrDilfa),
        ProfileKonselorHome("dr. Aan Royhan, MSc, C,Ht", R.drawable.s_img_draan, listOf("Pos Sapa/Kekerasan", "Hukum"), R.string.deskripDrAan),
        ProfileKonselorHome("DR. Yusuf Shofie, S.H., M.H.", R.drawable.s_img_dryusuf, listOf("Pos Sapa/Kekerasan", "Kesehatan Mental dan Psikologi"), R.string.deskripDrYusuf),
        ProfileKonselorHome("Chandradewi Kusristanti, M.Psi., Psikolog", R.drawable.s_img_chandra, listOf("Motivasi Belajar"), R.string.deskripDrChandradewi),
        ProfileKonselorHome("Sari Zakiah Akmal, Ph.D., Psikolog", R.drawable.s_img_drsari, listOf("Motivasi Belajar", "Disabilitas", "Kesehatan Mental dan Psikologi"), R.string.deskripDrSariZakiah),
        ProfileKonselorHome("Alabanyo Brebahama, M.Psi., Psikolog ", R.drawable.s_img_dralabanyo, listOf("Kesehatan Mental dan Psikologi"), R.string.deskripDrAlabanyo),
        ProfileKonselorHome("dr. Citra Fitri Agustina, Sp.KJ", R.drawable.s_img_drcitra, listOf("Hukum", "Disabilitas"), R.string.deskripDrCitra),
        ProfileKonselorHome("DR Lusy Liany, S.H., M.H.", R.drawable.s_img_drlusy, listOf("Finansial"), R.string.deskripDrLusy),
        ProfileKonselorHome("Ely Nurhayati, S.Pd, M.Si", R.drawable.s_img_drely, listOf("Kesehatan Mental dan Psikologi"), R.string.deskripDrEly),
        ProfileKonselorHome("Qurrata A’yyun, M.Psi., Psikolog", R.drawable.s_qurrataayyun, listOf("Kesehatan Mental dan Psikologi"), R.string.deskripDrAyyun),
        ProfileKonselorHome("dr. Yusnita., MKes.,Sp.KKLP.,Subsp.COPC", R.drawable.s_img_dryusnita, listOf("Gizi"), R.string.deskripDrYusnita),

        //Peer Konselor
        ProfileKonselorHome("Peer Konselor/Konselor Baru", R.drawable.blank_pp_img, listOf("Lainnya"), 1),
    )
}