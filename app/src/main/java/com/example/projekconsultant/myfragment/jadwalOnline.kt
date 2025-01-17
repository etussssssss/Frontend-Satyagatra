package com.example.projekconsultant.myfragment

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ScrollView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.projekconsultant.R
import com.example.projekconsultant.adapter.JadwalTanggal
import com.example.projekconsultant.adapter.JadwalTanggalAdapter
import com.example.projekconsultant.adapter2.MessageReminder
import com.example.projekconsultant.data.UserDataAjuinJadwalEksternal
import com.example.projekconsultant.data.UserDataAjuinJadwalInternal
import com.example.projekconsultant.methodsNService.SharedViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Calendar
import java.util.Locale
import kotlin.random.Random


class jadwalOnline : Fragment() {
    private var pilihLayananKonsultasi:String = ""
    private var pilihTopikKonsultasi:String = ""
    private var pilihTanggal:String = ""

    private var statusPendaftaran: Boolean? = null
    private var myName:String? = ""
    private var myUmur:String? = ""
    private var myGender:String? = ""
    private var myProfesi:String? = ""
    private var myFakultas:String? = ""

    private var myStatus:String? = ""
    private var myDomisili:String? = ""

    private var myNomortelepon:String? = ""
    private var myTypeIOrE:String? = ""
    private var myID:String? = ""

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    companion object {
        @JvmStatic
        fun newInstance(name: String) = jadwalOnline().apply {
                arguments = Bundle().apply {
                    putString("myName", name)
                }
            }

        fun inputUIDS(id : String) = jadwalOnline().apply {
            arguments = Bundle().apply {
                putString("myID", id)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            myName = it.getString("myName").toString()
            myID   = it.getString("myID").toString()
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_jadwal_online, container, false)
        addCloseKeyboard(view)
        addCloseKeyboard(view.findViewById<ScrollView>(R.id.sc))

        auth = Firebase.auth
        db = Firebase.firestore
        val userId = auth.currentUser?.uid

        view.findViewById<ImageView>(R.id.backtohome).setOnClickListener {
            // Ganti fragment dengan Home
            requireActivity().supportFragmentManager.beginTransaction().replace(R.id.nav_host_fragment, Penjadwalan()).commit()
//            // Pilih item Home di BottomNavigationView
            requireActivity().findViewById<BottomNavigationView>(R.id.bottomnav).selectedItemId = R.id.checked
        }

        val r = view.findViewById<RecyclerView>(R.id.recyclerViewJadwal1)
        r.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        val formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy") // Format tanggal
        val locale = Locale("id", "ID") // Bahasa Indonesia
        var currentDate = LocalDate.now() // Tanggal hari ini

        // Buat list mutable untuk data jadwal
        val datas = mutableListOf<JadwalTanggal>()

        for (i in 1 until 21) { // 21 hari (3 minggu)
            currentDate = currentDate.plusDays(1) // Tambahkan 1 hari (Di Mulai Besok Hari)

            val dayName = currentDate.dayOfWeek.getDisplayName(TextStyle.FULL, locale) // Nama hari
            val formattedDate = currentDate.format(formatter) // Format tanggal

            datas.add( JadwalTanggal(dayName, formattedDate) )

            //currentDate = currentDate.plusDays(1) // Tambahkan 1 hari (Di Mulai Hari Ini)
        }
        val adapter = JadwalTanggalAdapter(datas){
                item ->
            if(item != null){
                pilihTanggal = "${item.tanngalHari}"
            }else {
                pilihTanggal = ""
            }
        }
        r.adapter = adapter


        val spinner: Spinner = view.findViewById(R.id.spinner)
        val items = listOf("Video Call", "Via Chat", "Zoom Meeting", "Pilih")

        val adapter1 = object : ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_dropdown_item, items) {
            override fun getCount(): Int {
                return super.getCount() // Mengurangi satu agar "Pilih status" tidak muncul di dropdown
            }

            override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup?): View {
                val view = super.getDropDownView(position, convertView, parent)
                val tv = view as TextView
                val layoutParams = view.layoutParams
                layoutParams.height = 100 // set the height in pixels
                view.layoutParams = layoutParams
                view.setPadding(16, 16, 16, 16) // Set padding (left, top, right, bottom)
                view.maxHeight = 100

                if (position == items.size - 1) {
                    tv.setTextColor(Color.GRAY)
                } else {
                    tv.setTextColor(Color.BLACK)
                }

                return view
            }

            override fun isEnabled(position: Int): Boolean {
                if (position == items.size - 1) {
                    return false;
                } else {
                    return true;
                }
            }
        }

        spinner.adapter = adapter1
        spinner.setSelection(items.size - 1)
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                if (position != items.size - 1) {
                    pilihLayananKonsultasi = "${items[position]}"
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }


        val spinner2: Spinner = view.findViewById(R.id.spinner2)
        val items2 = arrayOf("Curhat Remaja", "Kesehatan Reproduksi", "Hukum", "Agama",
            "Kesehatan Mental Dan Psikologi", "Keluarga", "Pos Sapa", "Motivasi Belajar",
            "HIV", "Gizi", "Parenting", "Disabilitas", "Finansial", "Lainnya", "Topik Konseling")

        val adapter22 = object : ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_dropdown_item, items2) {
            override fun getCount(): Int {
                return super.getCount() // Mengurangi satu agar "Pilih status" tidak muncul di dropdown
            }

            override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup?): View {
                val view = super.getDropDownView(position, convertView, parent)
                val tv = view as TextView
                val layoutParams = view.layoutParams
                layoutParams.height = 100 // set the height in pixels
                view.layoutParams = layoutParams
                view.setPadding(16, 16, 16, 16) // Set padding (left, top, right, bottom)
                view.maxHeight = 100

                if (position == items2.size - 1) {
                    tv.setTextColor(Color.GRAY)
                } else {
                    tv.setTextColor(Color.BLACK)
                }

                return view
            }

            override fun isEnabled(position: Int): Boolean {
                if (position == items2.size - 1) {
                    return false;
                } else {
                    return true;
                }
            }
        }

        spinner2.adapter = adapter22
        spinner2.setSelection(items2.size - 1)
        spinner2.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                if (position != items2.size - 1) {
                    pilihTopikKonsultasi = "${items2[position]}"
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }


        //Dialog 1
        val builder = AlertDialog.Builder(requireContext()).setView(R.layout.item_dialog_ajukanjadwal)
        val dialog = builder.create()

        //Dialog 2
        val builder2 = AlertDialog.Builder(requireContext()).setView(R.layout.item_dialog_ajukanjadwal_tersimpan_riwayat)
        val dialog2 = builder2.create()
        dialog2.window?.setBackgroundDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.borderadius_dialog_white))
        dialog2.setCancelable(false)

        val sharedViewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)
        val isiCurhatan = view.findViewById<EditText>(R.id.etDescription).text

        sharedViewModel.namaUser.observe(viewLifecycleOwner) { nama ->
            myName = nama
        }

        sharedViewModel.umur.observe(viewLifecycleOwner) { umur ->
            myUmur = calculateAgeAllVersions(umur.toString())
        }

        sharedViewModel.gender.observe(viewLifecycleOwner) { gender ->
            myGender = gender
        }

        sharedViewModel.nomorTelepon.observe(viewLifecycleOwner) { nomorTelepon ->
            myNomortelepon = nomorTelepon
        }

        sharedViewModel.typeUserIORE.observe(viewLifecycleOwner) { typeIOrE ->
            myTypeIOrE = typeIOrE
        }

        sharedViewModel.statusPendaftaran.observe(viewLifecycleOwner) { status ->
            statusPendaftaran = status
        }

        sharedViewModel.profesi.observe(viewLifecycleOwner) { profesi ->
            myProfesi = profesi
        }

        sharedViewModel.fakultas.observe(viewLifecycleOwner) { fakultas ->
            myFakultas = fakultas
        }

        sharedViewModel.status.observe(viewLifecycleOwner) { status ->
            myStatus = status
        }

        sharedViewModel.domisili.observe(viewLifecycleOwner) { domisili ->
            myDomisili = domisili
        }

        Log.d("JADWAL ON", "scaalog${myID}chahalogs")
        view.findViewById<Button>(R.id.Enter).setOnClickListener {
            if(pilihTanggal.isNotEmpty() && pilihLayananKonsultasi.isNotEmpty() && pilihTopikKonsultasi.isNotEmpty() && "${isiCurhatan}".trim().isNotEmpty()) {
                if(statusPendaftaran ==  true){
                    dialog.show()
                    val params = dialog.window?.attributes
                    val displayMetrics = resources.displayMetrics
                    val screenWidth = displayMetrics.widthPixels
                    params?.width = (screenWidth * 0.6).toInt()
                    dialog.window?.attributes = params
                    dialog.window?.setBackgroundDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.borderadius_dialog_white))
                    dialog.setCancelable(true)

                    dialog.findViewById<TextView>(R.id.tanggalKonseling)?.text = "Jadwal konseling pada:\n${pilihTanggal}"
                    dialog.findViewById<TextView>(R.id.LayananKonseling)?.text = "Layanan konseling:\n${pilihLayananKonsultasi}"
                    dialog.findViewById<TextView>(R.id.descMasalah)?.text = "${isiCurhatan}"

                    dialog.findViewById<Button>(R.id.btn_simpanjadwal)?.setOnClickListener {
                        dialog.dismiss()
                        dialog2.show()

                        val nomorDaftar = "${myName!!.substring(0, 2)}-${Random.nextInt(1000, 100000)}"

                        sharedViewModel.riwayatStatusButuhPersetujuan.value = true
                        sharedViewModel.riwayatTopik.value = pilihTopikKonsultasi
                        sharedViewModel.riwayatOffOrOn.value = "Online"
                        sharedViewModel.riwayatDescTopik.value = "${isiCurhatan}"
                        sharedViewModel.riwayatNoPendaftaran.value = "${nomorDaftar}"

                        if (userId != null) {
                            if(myTypeIOrE.toString() == "I"){
                                daftarInternal(isiCurhatan.toString(), nomorDaftar, userId)
                            } else if (myTypeIOrE.toString() == "E"){
                                daftarEksternal(isiCurhatan.toString(), nomorDaftar, userId)
                            }
                        }

                        dialog2.findViewById<Button>(R.id.btn_oke)?.setOnClickListener {
                            dialog2.dismiss()
                            requireActivity().supportFragmentManager.beginTransaction().replace(R.id.nav_host_fragment, Home()).commit()
                            requireActivity().findViewById<BottomNavigationView>(R.id.bottomnav).selectedItemId = R.id.home
                        }

                        dialog2.findViewById<Button>(R.id.btn_to_riwayat)?.setOnClickListener {
                            dialog2.dismiss()

                            requireActivity().supportFragmentManager.beginTransaction().replace(R.id.nav_host_fragment, Riwayat()).commit()
                            requireActivity().findViewById<BottomNavigationView>(R.id.bottomnav).selectedItemId = R.id.history
                        }
                    }
                }else{
                    requireActivity().supportFragmentManager.beginTransaction().replace(R.id.nav_host_fragment, Riwayat()).commit()
                    requireActivity().findViewById<BottomNavigationView>(R.id.bottomnav).selectedItemId = R.id.history
                }
            }
        }

        return view
    }

    private fun daftarInternal(isiCurhatan:String, nomorDaftar:String, userId:String){

        val dataDaftar = UserDataAjuinJadwalInternal(
            nomorPendaftaran = nomorDaftar,

            nama = "${myName}",
            nomortelepon = "${myNomortelepon}",
            umur = "${myUmur}",
            gender = "${myGender}",
            profesi = "${myProfesi}",
            fakultas = "${myFakultas}",

            uid = "${userId}",

            PilihTanggal = pilihTanggal,
            PilihLayananKonsultasi = pilihLayananKonsultasi,
            PilihTopikKonsultasi = pilihTopikKonsultasi,

            IsiCurhatan = isiCurhatan.trim(),
            ButuhPersetujuan = true,
            onoff = "Online",
            typeIOrE = "${myTypeIOrE}",

            domisili = "${myDomisili}",
            status = "${myStatus}"
        )


        db.collection("daftarKonseling").document(userId).set(dataDaftar).addOnSuccessListener {
        }.addOnFailureListener { e ->
            Toast.makeText(requireContext(), "Failed to save user data: ${e.message}", Toast.LENGTH_SHORT).show()
        }

        val firstChat = MessageReminder(
            "user",
            "${pilihTanggal}",
            "${pilihLayananKonsultasi}",
            System.currentTimeMillis(),
            false,
            "needApproved"
        )

        // db.collection("chats").document(nomorDaftar).collection("messages").add(firstChat).addOnSuccessListener {
        db.collection("chats").document(userId).collection("messages").add(firstChat).addOnSuccessListener {

        }.addOnFailureListener { e ->
            Toast.makeText(requireContext(), "Failed to save user data: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun daftarEksternal(isiCurhatan:String, nomorDaftar:String, userId:String){

        val dataDaftar = UserDataAjuinJadwalEksternal(
            nomorPendaftaran = nomorDaftar,

            nama = "${myName}",
            nomortelepon = "${myNomortelepon}",
            umur = "${myUmur}",
            gender = "${myGender}",
            profesi = "${myProfesi}",

            uid = "${userId}",

            PilihTanggal = pilihTanggal,
            PilihLayananKonsultasi = pilihLayananKonsultasi,
            PilihTopikKonsultasi = pilihTopikKonsultasi,

            IsiCurhatan = isiCurhatan.trim(),
            ButuhPersetujuan = true,
            onoff = "Online",
            typeIOrE = "${myTypeIOrE}",

            domisili = "${myDomisili}",
            status = "${myStatus}"
        )


        db.collection("daftarKonseling").document(userId).set(dataDaftar).addOnSuccessListener {
        }.addOnFailureListener { e ->
            Toast.makeText(requireContext(), "Failed to save user data: ${e.message}", Toast.LENGTH_SHORT).show()
        }

        val firstChat = MessageReminder(
            "user",
            "${pilihTanggal}",
            "${pilihLayananKonsultasi}",
            System.currentTimeMillis(),
            false,
            "needApproved"
        )

        // db.collection("chats").document(nomorDaftar).collection("messages").add(firstChat).addOnSuccessListener {
        db.collection("chats").document(userId).collection("messages").add(firstChat).addOnSuccessListener {

        }.addOnFailureListener { e ->
            Toast.makeText(requireContext(), "Failed to save user data: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun calculateAgeAllVersions(birthDateString: String): String {
        val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        val birthDate = dateFormat.parse(birthDateString)

        // Dapatkan tanggal hari ini
        val today = Calendar.getInstance()
        val birthCalendar = Calendar.getInstance()
        if (birthDate != null) {
            birthCalendar.time = birthDate
        }

        // Hitung selisih tahun
        var age = today.get(Calendar.YEAR) - birthCalendar.get(Calendar.YEAR)

        // Cek apakah sudah melewati ulang tahun tahun ini atau belum
        if (today.get(Calendar.DAY_OF_YEAR) < birthCalendar.get(Calendar.DAY_OF_YEAR)) {
            age--
        }

        return "${age}"
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun addCloseKeyboard(rootView: View) {
        rootView.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                hideKeyboard()
            }
            false
        }
    }

    private fun hideKeyboard() {
        val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val view = requireActivity().currentFocus
        if (view != null) {
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }
}




//Log.d("Jadwal Online 1", "${pilihTanggal} ${pilihLayananKonsultasi} ${pilihTopikKonsultasi} ${isiCurhatan}")
//Log.d("Jadwal Online 2", "${dataDaftar} ${statusPendaftaran} ${updates["statusPendaftaran"]}")

//
//        val maxLines = 3
//        val editText = view.findViewById<EditText>(R.id.etDescription)
//
//        // Tambahkan TextWatcher untuk memonitor input
//        editText.addTextChangedListener(object : TextWatcher {
//            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
//
//            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
//                // Periksa apakah jumlah baris sudah melebihi batas
//                if (editText.lineCount > maxLines) {
//                    // Batasi input
//                    editText.error = "Maksimal $maxLines baris"
//                    val maxLength = editText.layout.getLineEnd(maxLines - 1) // Ambil indeks karakter terakhir dari baris ke-3
//                    editText.setText(s?.subSequence(0, maxLength)) // Potong teks hingga batas
//                    editText.setSelection(editText.text.length) // Pindahkan kursor ke akhir teks
//                }
//            }
//
//            override fun afterTextChanged(s: Editable?) {}
//        })