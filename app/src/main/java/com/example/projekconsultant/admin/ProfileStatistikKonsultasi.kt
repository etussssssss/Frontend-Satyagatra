package com.example.projekconsultant.admin

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
//import android.graphics.Canvas
//import android.graphics.Color
//import android.graphics.Paint
import android.graphics.Typeface
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.projekconsultant.BaseActivity
import com.example.projekconsultant.R
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.collections.ArrayList
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
//import android.widget.Toast
import androidx.annotation.RequiresApi
import java.io.File
import java.io.FileOutputStream
//import java.io.IOException
import java.io.OutputStream
import com.itextpdf.kernel.geom.PageSize
//import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.Image
import com.itextpdf.layout.element.Paragraph
import com.itextpdf.io.image.ImageDataFactory


class ProfileStatistikKonsultasi : BaseActivity() {
    private lateinit var spinnerBulan: Spinner
    private lateinit var spinnerTahun: Spinner
    private lateinit var pieChart: PieChart
    private lateinit var barChart: BarChart
    private lateinit var barChartUmur: BarChart
    private lateinit var pieChartIorE: PieChart
    private lateinit var pieChartnamakonselor: PieChart
    private lateinit var teksInformation:TextView
    private var receivedUserList: ArrayList<DataRiwayatSelesaiAdmin> = arrayListOf()
    // private lateinit var downloadpdf:View
    private lateinit var downloadpdf:Button

    private lateinit var progbar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_statistik_konsultasi)

        progbar = findViewById(R.id.loading_progbar)
        downloadpdf = findViewById(R.id.downloadpdf)
        barChart = findViewById(R.id.horizontalBarChart)
        barChartUmur = findViewById(R.id.horizontalBarChartUmur)
        pieChart = findViewById(R.id.pieChart)
        pieChartIorE = findViewById(R.id.pieChartIorE)
        pieChartnamakonselor = findViewById(R.id.pieChartnamakonselor)
        spinnerBulan = findViewById(R.id.spinnerBulan)
        spinnerTahun = findViewById(R.id.spinnerTahun)
        teksInformation = findViewById(R.id.textInformation)
        receivedUserList = intent.getParcelableArrayListExtra("RIWAYAT") ?: arrayListOf()
//        receivedUserList = dataArray

        // Setup Spinner Bulan
        val bulanArray = arrayOf("Pilih", "Januari", "Februari", "Maret", "April", "Mei", "Juni",
            "Juli", "Agustus", "September", "Oktober", "November", "Desember", "Semua")
        val adapterBulan = ArrayAdapter(this, android.R.layout.simple_spinner_item, bulanArray)
        adapterBulan.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerBulan.adapter = adapterBulan

        // Setup Spinner Tahun
        val tahunArray = listOf("Pilih") + getUniqueYears(receivedUserList)
        val adapterTahun = ArrayAdapter(this, android.R.layout.simple_spinner_item, tahunArray)
        adapterTahun.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerTahun.adapter = adapterTahun

        // Listener untuk filter otomatis
        spinnerBulan.onItemSelectedListener = createSpinnerListener()
        spinnerTahun.onItemSelectedListener = createSpinnerListener()

        // Menampilkan data awal
        pieChart1(receivedUserList)
        setupBarChart3(receivedUserList)
        pieChartIOrE(receivedUserList)
        setupBarChartUmur(receivedUserList)
        pieChartnamakonselor(receivedUserList)

        findViewById<ImageView>(R.id.backtohome).setOnClickListener {
            finish()
        }
    }

    // Fungsi Listener untuk Spinner
    private fun createSpinnerListener() = object : AdapterView.OnItemSelectedListener {
        @RequiresApi(Build.VERSION_CODES.Q)
        override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
            filterData(receivedUserList)
        }
        override fun onNothingSelected(parent: AdapterView<*>) {}
    }

    private fun pieChart1(receivedUserList: List<DataRiwayatSelesaiAdmin>?) {
        // List untuk menyimpan data PieChart
        val pieEntries = mutableListOf<PieEntry>()

        // Menghitung jumlah kemunculan setiap kategori layanan di dataArray
        val kategoriCount = receivedUserList?.groupBy { it.topikKonsultasi }?.mapValues { it.value.size }

        // Menambahkan data ke PieEntry berdasarkan kategoriCount
        kategoriCount?.forEach { (kategori, count) ->
            pieEntries.add(PieEntry(count.toFloat(), kategori))
        }

        // Membuat PieDataSet dengan data
        val pieDataSet = PieDataSet(pieEntries, "Kategori Konsultasi")
        val customColors = listOf(
            ContextCompat.getColor(this, R.color.c1),
            ContextCompat.getColor(this, R.color.c2),
            ContextCompat.getColor(this, R.color.c3),
            ContextCompat.getColor(this, R.color.c4),
            ContextCompat.getColor(this, R.color.c5),
            ContextCompat.getColor(this, R.color.c6),
            ContextCompat.getColor(this, R.color.c7),
            ContextCompat.getColor(this, R.color.c8),
            ContextCompat.getColor(this, R.color.c9),
            ContextCompat.getColor(this, R.color.c10),
            ContextCompat.getColor(this, R.color.c11),
            ContextCompat.getColor(this, R.color.c12),
            ContextCompat.getColor(this, R.color.c13)
        )
        pieDataSet.colors = customColors
        pieDataSet.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                // Format nilai sebagai persentase dengan dua angka desimal
                return String.format("%.1f%%", value)
            }
        }

        pieDataSet.valueTextSize = 16f
        pieDataSet.valueTextColor = ContextCompat.getColor(this, R.color.black) // Atur warna teks (opsional)
        pieDataSet.valueTypeface = Typeface.create("sans-serif-medium", Typeface.NORMAL)

        // Membuat PieData dari PieDataSet
        val pieData = PieData(pieDataSet)
        pieChart.data = pieData
        pieChart.setUsePercentValues(true)
        pieChart.setCenterTextSize(12F)
        pieChart.isVerticalScrollBarEnabled = true
        pieChart.isHorizontalScrollBarEnabled = false
        // val paddingSize = kategoriCount?.size ?: 0
//         pieChart.setExtraOffsets(paddingSize * 5f, 0f, 0f, paddingSize * 5f) // Menambah jarak di bagian bawah
        pieChart.setExtraOffsets(60f, 0f, 0f, 0f) // Menambah jarak di bagian bawah

        // Mengatur Legend agar label vertikal
        val legend = pieChart.legend
        legend.orientation = Legend.LegendOrientation.VERTICAL // Orientasi legend vertikal
        legend.verticalAlignment = Legend.LegendVerticalAlignment.TOP // Atur posisi vertikal
        legend.horizontalAlignment = Legend.LegendHorizontalAlignment.LEFT // Atur posisi horizontal
        legend.setDrawInside(false) // Pastikan legend berada di luar chart

        pieChart.animateY(1000)
        pieChart.invalidate()
    }

    private fun setupBarChart3(dataList: List<DataRiwayatSelesaiAdmin>) {
        val barEntries = mutableListOf<BarEntry>()
        val labels = mutableListOf<String>()

        // Mengelompokkan data berdasarkan bulan dan tahun
        val kategoriCount = dataList.groupBy { getMonthYear(it.tanggalSelesaiUser) }.mapValues { it.value.size }

        kategoriCount.entries.forEachIndexed { index, entry ->
            barEntries.add(BarEntry(index.toFloat(), entry.value.toFloat()))
            labels.add(entry.key)  // Menambahkan bulan dan tahun sebagai label
        }

        if(barEntries.isNotEmpty()){
            // Membuat dataset untuk BarChart
            val barDataSet = BarDataSet(barEntries, "Rekap Data Jumlah Konseling")
            barDataSet.valueTextSize = 14f
            barDataSet.color = ContextCompat.getColor(this, R.color.textbirufigma2)
            barDataSet.valueTextColor = ContextCompat.getColor(this, R.color.black)

            // Membuat objek BarData
            val barData = BarData(barDataSet)
            barData.barWidth = 0.7f

            // Menyusun data ke dalam chart
            barChart.data = barData
            barChart.setFitBars(true)
            barChart.description.isEnabled = false
            barChart.animateY(1000)

            // Mengatur sumbu X dengan label kategori
            barChart.xAxis.valueFormatter = IndexAxisValueFormatter(labels)
            barChart.xAxis.position = XAxis.XAxisPosition.BOTTOM
            barChart.xAxis.granularity = 1f
            barChart.xAxis.setDrawGridLines(false)

            // Mengatur sumbu Y
            barChart.axisLeft.setDrawGridLines(false)
            barChart.axisLeft.axisMinimum = 0f
            barChart.axisRight.isEnabled = false
        }else{
            barChart.clear()
        }

        barChart.notifyDataSetChanged()
        barChart.invalidate()
    }

    private fun pieChartIOrE(receivedUserList: List<DataRiwayatSelesaiAdmin>?) {
        // List untuk menyimpan data PieChart
        val pieEntries = mutableListOf<PieEntry>()

        // Menghitung jumlah kemunculan setiap kategori layanan di dataArray
        val kategoriCount = receivedUserList?.groupBy { it.typeUser }?.mapValues { it.value.size }

        // Menambahkan data ke PieEntry berdasarkan kategoriCount
        kategoriCount?.forEach { (kategori, count) ->
            pieEntries.add(PieEntry(count.toFloat(), kategori))
        }

        // Membuat PieDataSet dengan data
        val pieDataSet = PieDataSet(pieEntries, "Kategori Instansi")
        val customColors = listOf(
            ContextCompat.getColor(this, R.color.ijofigmapudar),
            ContextCompat.getColor(this, R.color.textbirufigma2),
        )
        pieDataSet.colors = customColors
        pieDataSet.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                // Format nilai sebagai persentase dengan dua angka desimal
                return String.format("%.1f%%", value)
            }
        }

        pieDataSet.valueTextSize = 16f
        pieDataSet.valueTextColor = ContextCompat.getColor(this, R.color.black) // Atur warna teks (opsional)
        pieDataSet.valueTypeface = Typeface.create("sans-serif-medium", Typeface.NORMAL)

        // Membuat PieData dari PieDataSet
        val pieData = PieData(pieDataSet)
        pieChartIorE.data = pieData
        pieChartIorE.setUsePercentValues(true)
        pieChartIorE.setCenterTextSize(12F)
        pieChartIorE.isVerticalScrollBarEnabled = true
        pieChartIorE.isHorizontalScrollBarEnabled = false
        pieChartIorE.setExtraOffsets(60f, 0f, 0f, 0f) // Menambah jarak di bagian bawah

        // Mengatur Legend agar label vertikal
        val legend = pieChartIorE.legend
        legend.orientation = Legend.LegendOrientation.VERTICAL // Orientasi legend vertikal
        legend.verticalAlignment = Legend.LegendVerticalAlignment.TOP // Atur posisi vertikal
        legend.horizontalAlignment = Legend.LegendHorizontalAlignment.LEFT // Atur posisi horizontal
        legend.setDrawInside(false) // Pastikan legend berada di luar chart

        pieChartIorE.animateY(1000)
        pieChartIorE.invalidate()
    }

    private fun setupBarChartUmur(dataList: List<DataRiwayatSelesaiAdmin>) {
        val barEntries = mutableListOf<BarEntry>()
        val labels = mutableListOf(
            "1-6 tahun", "7-12 tahun", "13-15 tahun", "16-18 tahun",
            "19-24 tahun", "25-30 tahun", "31-40 tahun",
            "41-50 tahun", "51-60 tahun", ">60 tahun"
        )

        // Menginisialisasi kategori dengan 0
        val kategoriCount = labels.associateWith { 0 }.toMutableMap()

        // Mengelompokkan data umur ke dalam kategori
        dataList.forEach { data ->
            val umur = data.umur
            when {
                umur in 1..6 -> kategoriCount["1-6 tahun"] = kategoriCount["1-6 tahun"]!! + 1
                umur in 7..12 -> kategoriCount["7-12 tahun"] = kategoriCount["7-12 tahun"]!! + 1
                umur in 13..15 -> kategoriCount["13-15 tahun"] = kategoriCount["13-15 tahun"]!! + 1
                umur in 16..18 -> kategoriCount["16-18 tahun"] = kategoriCount["16-18 tahun"]!! + 1
                umur in 19..24 -> kategoriCount["19-24 tahun"] = kategoriCount["19-24 tahun"]!! + 1
                umur in 25..30 -> kategoriCount["25-30 tahun"] = kategoriCount["25-30 tahun"]!! + 1
                umur in 31..40 -> kategoriCount["31-40 tahun"] = kategoriCount["31-40 tahun"]!! + 1
                umur in 41..50 -> kategoriCount["41-50 tahun"] = kategoriCount["41-50 tahun"]!! + 1
                umur in 51..60 -> kategoriCount["51-60 tahun"] = kategoriCount["51-60 tahun"]!! + 1
                umur > 60 -> kategoriCount[">60 tahun"] = kategoriCount[">60 tahun"]!! + 1
            }
        }

        // Menambahkan data ke BarChart
        kategoriCount.entries.forEachIndexed { index, entry ->
            barEntries.add(BarEntry(index.toFloat(), entry.value.toFloat()))
        }

        if (barEntries.isNotEmpty()) {
            val barDataSet = BarDataSet(barEntries, "Rekap Data Jumlah Konseling")
            barDataSet.valueTextSize = 14f
            barDataSet.color = ContextCompat.getColor(this, R.color.textbirufigma2)
            barDataSet.valueTextColor = ContextCompat.getColor(this, R.color.black)

            val barData = BarData(barDataSet)
            barData.barWidth = 0.7f

            barChartUmur.data = barData
            barChartUmur.setFitBars(true)
            barChartUmur.description.isEnabled = false
            barChartUmur.animateY(1000)

            // Mengatur sumbu X dengan label kategori
            barChartUmur.xAxis.apply {
                valueFormatter = IndexAxisValueFormatter(labels)
                position = XAxis.XAxisPosition.BOTTOM
                granularity = 1f
                labelCount = labels.size
                setDrawGridLines(false)
                textSize = 12f
                setAvoidFirstLastClipping(false)
            }

            // Mengatur sumbu Y
            barChartUmur.axisLeft.apply {
                setDrawGridLines(false)
                axisMinimum = 0f
            }
            barChartUmur.axisRight.isEnabled = false

        } else {
            barChartUmur.clear()
        }

        barChartUmur.notifyDataSetChanged()
        barChartUmur.invalidate()
    }

    private fun pieChartnamakonselor(receivedUserList: List<DataRiwayatSelesaiAdmin>?) {
        // List untuk menyimpan data PieChart
        val pieEntries = mutableListOf<PieEntry>()

        // Menghitung jumlah kemunculan setiap kategori layanan di dataArray
        val kategoriCount = receivedUserList?.groupBy { it.namaKonselor }?.mapValues { it.value.size }

        // Menambahkan data ke PieEntry berdasarkan kategoriCount
        kategoriCount?.forEach { (kategori, count) ->
            pieEntries.add(PieEntry(count.toFloat(), kategori))
        }

        // Membuat PieDataSet dengan data
        val pieDataSet = PieDataSet(pieEntries, "Kategori")
        val customColors = listOf(
            ContextCompat.getColor(this, R.color.c1),
            ContextCompat.getColor(this, R.color.c2),
            ContextCompat.getColor(this, R.color.c3),
            ContextCompat.getColor(this, R.color.c4),
            ContextCompat.getColor(this, R.color.c5),
            ContextCompat.getColor(this, R.color.c6),
            ContextCompat.getColor(this, R.color.c7),
            ContextCompat.getColor(this, R.color.c8),
            ContextCompat.getColor(this, R.color.c9),
            ContextCompat.getColor(this, R.color.c10),
            ContextCompat.getColor(this, R.color.c11),
            ContextCompat.getColor(this, R.color.c12),
            ContextCompat.getColor(this, R.color.c13),
            ContextCompat.getColor(this, R.color.ijo3),
            ContextCompat.getColor(this, R.color.red500),
            ContextCompat.getColor(this, R.color.teal_200),
            ContextCompat.getColor(this, R.color.textbirufigma2),
            ContextCompat.getColor(this, R.color.abuabuicon),
            ContextCompat.getColor(this, R.color.red900),
        )
        pieDataSet.colors = customColors
        pieDataSet.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                // Format nilai sebagai persentase dengan dua angka desimal
                return String.format("%.1f%%", value)
            }
        }

        pieDataSet.valueTextSize = 16f
        pieDataSet.valueTextColor = ContextCompat.getColor(this, R.color.black) // Atur warna teks (opsional)
        pieDataSet.valueTypeface = Typeface.create("sans-serif-medium", Typeface.NORMAL)

        // Membuat PieData dari PieDataSet
        val pieData = PieData(pieDataSet)
        pieChartnamakonselor.data = pieData
        pieChartnamakonselor.setUsePercentValues(true)
        pieChartnamakonselor.setCenterTextSize(12F)
        pieChartnamakonselor.isVerticalScrollBarEnabled = true
        pieChartnamakonselor.isHorizontalScrollBarEnabled = false
        pieChartnamakonselor.setExtraOffsets(60f, 0f, 0f, 0f) // Menambah jarak di bagian bawah

        // Mengatur Legend agar label vertikal
        val legend = pieChartnamakonselor.legend
        legend.orientation = Legend.LegendOrientation.VERTICAL // Orientasi legend vertikal
        legend.verticalAlignment = Legend.LegendVerticalAlignment.TOP // Atur posisi vertikal
        legend.horizontalAlignment = Legend.LegendHorizontalAlignment.LEFT // Atur posisi horizontal
        legend.setDrawInside(false) // Pastikan legend berada di luar chart

        pieChartnamakonselor.animateY(1000)
        pieChartnamakonselor.invalidate()
    }


    // Fungsi filter berdasarkan Spinner
    @RequiresApi(Build.VERSION_CODES.Q)
    private fun filterData(dataArray: List<DataRiwayatSelesaiAdmin>) {
        val bulanDipilih = spinnerBulan.selectedItem.toString()
        val tahunDipilih = spinnerTahun.selectedItem.toString()

        // Filter jika opsi "Pilih" belum diubah
        if (bulanDipilih == "Pilih" && tahunDipilih == "Pilih") {
            pieChart.clear()
            barChart.clear() // Bersihkan BarChart jika belum ada filter
            pieChartIorE.clear()
            teksInformation.text = "Pilih Terlebih Dahulu"
            return
        }

        // Filter jika hanya salah satu spinner yang dipilih
        if (bulanDipilih == "Pilih" || tahunDipilih == "Pilih") {
            pieChart.clear()
            barChart.clear() // Bersihkan BarChart jika filter belum lengkap
            pieChartIorE.clear()
            teksInformation.text = "Pilih Terlebih Dahulu"
            return
        }

        // Filter Data Tidak Bisa Fitur Semua
//        val filteredData = dataArray.filter { data ->
//            val tanggalParts = data.tanggalSelesaiUser.split(" ")
//            val bulan = tanggalParts.getOrNull(1)
//            val tahun = tanggalParts.getOrNull(2)
//            (bulan == bulanDipilih || bulanDipilih == "Pilih") && (tahun == tahunDipilih || tahunDipilih == "Pilih")
//        }

        // Filter Data berdasarkan kombinasi pilihan bulan dan tahun
        val filteredData = dataArray.filter { data ->
            val tanggalParts = data.tanggalSelesaiUser.split(" ")
            val bulan = tanggalParts.getOrNull(1)
            val tahun = tanggalParts.getOrNull(2)

            // Logika untuk "Semua" bulan
            val isBulanMatch = bulanDipilih == "Semua" || bulan == bulanDipilih
            val isTahunMatch = tahunDipilih == "Semua" || tahun == tahunDipilih

            isBulanMatch && isTahunMatch
        }

        pieChart1(filteredData)
        setupBarChart3(filteredData) // BarChart hanya muncul jika kedua spinner dipilih
        pieChartIOrE(filteredData)

        //Text
        getTextInformation(filteredData)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun getTextInformation(receivedUserList: List<DataRiwayatSelesaiAdmin>?){
        val kategoriCountnamaKonselor = receivedUserList?.groupBy { it.namaKonselor }?.mapValues { it.value.size }
        val kategoriCounttopikKonsultasi = receivedUserList?.groupBy { it.topikKonsultasi }?.mapValues { it.value.size }
        val kategoriCountlayananOnlineOrOfflineUser = receivedUserList?.groupBy { it.layananOnlineOrOfflineUser }?.mapValues { it.value.size }
        val kategoriCountlayananKonsultasi = receivedUserList?.groupBy { it.layananKonsultasi }?.mapValues { it.value.size }
        val kategoriCounttypeUser = receivedUserList?.groupBy { it.typeUser }?.mapValues { it.value.size }

        if(receivedUserList.isNullOrEmpty()){
            teksInformation.text = "Pilih Terlebih Dahulu"
        }else {
            val stringBuilder = StringBuilder()
            stringBuilder.appendLine("Data:")
            stringBuilder.appendLine("1. Nama Konselor/Peer Konselor:")
            kategoriCountnamaKonselor?.forEach { (key, value) ->
                stringBuilder.appendLine("   - $key: $value")
            }

            stringBuilder.appendLine("\n2. Topik Konsultasi:")
            kategoriCounttopikKonsultasi?.forEach { (key, value) ->
                stringBuilder.appendLine("   - $key: $value")
            }

            stringBuilder.appendLine("\n3. Layanan Online/Offline:")
            kategoriCountlayananOnlineOrOfflineUser?.forEach { (key, value) ->
                stringBuilder.appendLine("   - $key: $value")
            }

            stringBuilder.appendLine("\n4. Layanan Konsultasi:")
            kategoriCountlayananKonsultasi?.forEach { (key, value) ->
                if(key.isNotEmpty()){
                    stringBuilder.appendLine("   - $key: $value")
                }else{
                    stringBuilder.appendLine("   - Tatap Muka: $value")
                }
            }

            stringBuilder.appendLine("\n5. Tipe User:")
            kategoriCounttypeUser?.forEach { (key, value) ->
                stringBuilder.appendLine("   - $key: $value")
            }

            teksInformation.text = stringBuilder.toString()
        }

        downloadpdf.setOnClickListener {
            progbar.visibility = View.VISIBLE
            if (!receivedUserList.isNullOrEmpty()){
                Log.d("DOWNLOAD DATA", "${receivedUserList}")
                val chartList = listOf(pieChart, barChart, pieChartIorE, barChartUmur, pieChartnamakonselor) // Menggunakan 3 chart
                savePdfWithMultipleChartsAndText22(this, chartList,"${teksInformation.text.trim()}")
            }else{
                progbar.visibility = View.GONE
                Toast.makeText(this, "Gagal Download Data", Toast.LENGTH_SHORT).show()
            }
        }
    }

    //Download PDF Stay APK 2.0
    fun savePdfWithMultipleChartsAndText22(context: Context, chartList: List<com.github.mikephil.charting.charts.Chart<*>>, text: String) {
        progbar.visibility = View.GONE
        try {
            val pdfFileName = "rekapdatasatyagatra.pdf"
            val outputStream: OutputStream?

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                // Android 10 ke atas menggunakan MediaStore
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, pdfFileName)
                    put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
                }

                val resolver = context.contentResolver
                val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)

                if (uri == null) {
                    println("Gagal membuat URI untuk penyimpanan file.")
                    return
                }

                outputStream = resolver.openOutputStream(uri)
            } else {
                // Android 9 dan di bawahnya menggunakan penyimpanan eksternal langsung
                val pdfPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).absolutePath
                val file = File(pdfPath, pdfFileName)
                outputStream = FileOutputStream(file)
            }

            // Membuat file PDF
            if (outputStream != null) {
                Toast.makeText(this, "Berhasil", Toast.LENGTH_SHORT).show()
                val pdfWriter = PdfWriter(outputStream)
                val pdfDocument = com.itextpdf.kernel.pdf.PdfDocument(pdfWriter)
                val document = Document(pdfDocument, PageSize.A4)

                // Menambahkan teks ke PDF
                val paragraph = Paragraph(text).setFontSize(12f)
                document.add(paragraph)

                // Menambahkan setiap chart sebagai gambar ke PDF
                chartList.forEach { chart ->
                    val chartBitmap = chart.getChartBitmap()
                    val stream = java.io.ByteArrayOutputStream()
                    chartBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
                    val imageData = ImageDataFactory.create(stream.toByteArray())

                    val chartImage = Image(imageData).apply {
                        setWidth(400f)
                        setHeight(if (chart is PieChart) 400f else 300f)
                        setMarginTop(10f)
                        setMarginBottom(10f)
                    }
                    document.add(chartImage)
                }

                // Menutup dokumen PDF
                document.close()
                Log.d("DOWNLOAD DATA", "SUCCESS")
            }

        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("DOWNLOAD DATA", "${e.message}")
            Toast.makeText(this, "Gagal Download Data", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getUniqueYears(dataArray: List<DataRiwayatSelesaiAdmin>): List<String> {
        return dataArray.mapNotNull {
            val parts = it.tanggalSelesaiUser.split(" ")
            parts.getOrNull(2)
        }.distinct().sorted()
    }

    // Fungsi untuk mendapatkan bulan dan tahun dari tanggal
    private fun getMonthYear(tanggal: String): String {
        val format = SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID"))
        val date = format.parse(tanggal)
        return if (date != null) {
            val monthYearFormat = SimpleDateFormat("MMMM yyyy", Locale("id", "ID"))
            monthYearFormat.format(date)
        } else {
            ""
        }
    }

    //Download PDF Stay APK 1.0
    fun savePdfWithMultipleChartsAndText2(context: Context, chartList: List<com.github.mikephil.charting.charts.Chart<*>>, text: String) {
        try {
            val pdfFileName = "rekapdatasatyagatra.pdf"
            val outputStream: OutputStream?
            var savedUri: Uri? = null

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                // Untuk Android 10 ke atas dengan MediaStore
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, pdfFileName)
                    put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
                }

                val resolver = context.contentResolver
                savedUri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)

                if (savedUri == null) {
                    println("Gagal membuat URI untuk penyimpanan file.")
                    return
                }

                outputStream = resolver.openOutputStream(savedUri)
            } else {
                // Untuk Android 9 (Pie) dan sebelumnya
                val pdfPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).absolutePath
                val file = File(pdfPath, pdfFileName)
                outputStream = FileOutputStream(file)

                // Menambahkan file ke MediaScanner agar muncul di file manager
                MediaScannerConnection.scanFile(context, arrayOf(file.absolutePath), arrayOf("application/pdf")) { _, _ ->
                    println("PDF berhasil disimpan di folder Downloads.")
                }
            }

            // Membuat file PDF
            if (outputStream != null) {
                val pdfWriter = PdfWriter(outputStream)
                val pdfDocument = com.itextpdf.kernel.pdf.PdfDocument(pdfWriter)
                val document = Document(pdfDocument, PageSize.A4)

                // Menambahkan teks ke PDF
                val paragraph = Paragraph(text).setFontSize(12f)
                document.add(paragraph)

                // Menambahkan setiap chart ke dalam PDF sebagai gambar
                chartList.forEach { chart ->
                    val chartBitmap = chart.getChartBitmap()
                    val stream = java.io.ByteArrayOutputStream()
                    chartBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
                    val imageData = ImageDataFactory.create(stream.toByteArray())

                    val chartImage = Image(imageData).apply {
                        setWidth(400f)
                        setHeight(if (chart is PieChart) 400f else 300f)
                        setMarginTop(10f)
                        setMarginBottom(10f)
                    }
                    document.add(chartImage)
                }

                // Menutup dokumen PDF
                document.close()
                println("PDF berhasil disimpan.")
            }

        } catch (e: Exception) {
            e.printStackTrace()
            println("Terjadi kesalahan saat menyimpan PDF: ${e.message}")
        }
    }

    //Download PDF Ref
    fun savePdfWithMultipleChartsAndText(context: Context, chartList: List<com.github.mikephil.charting.charts.Chart<*>>, text: String) {
        try {
            val pdfFileName = "rekapdatasatyagatra.pdf"
            val outputStream: OutputStream?

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                // Android 10+ menggunakan MediaStore
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, pdfFileName)
                    put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
                }

                val resolver = context.contentResolver
                val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)

                if (uri == null) {
                    println("Gagal membuat URI untuk penyimpanan file.")
                    return
                }

                outputStream = resolver.openOutputStream(uri)
            } else {
                // Android 9 ke bawah menggunakan penyimpanan eksternal langsung
                val pdfPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).absolutePath
                val file = File(pdfPath, pdfFileName)
                outputStream = FileOutputStream(file)
            }

            if (outputStream != null) {
                // Membuat file PDF
                val pdfWriter = PdfWriter(outputStream)
                val pdfDocument = com.itextpdf.kernel.pdf.PdfDocument(pdfWriter)
                val document = Document(pdfDocument, PageSize.A4)

                // Menambahkan teks ke PDF
                val paragraph = Paragraph(text).setFontSize(12f)
                document.add(paragraph)

                // Menambahkan setiap chart sebagai gambar ke PDF
                chartList.forEach { chart ->
                    val chartBitmap = chart.getChartBitmap() // Ambil bitmap dari chart
                    val stream = java.io.ByteArrayOutputStream()
                    chartBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
                    val imageData = ImageDataFactory.create(stream.toByteArray())


                    when (chart) {
                        is PieChart -> {
                            val chartImage = Image(imageData).setWidth(400f).setHeight(400f).setMarginTop(10f).setMarginBottom(10f)
                            document.add(chartImage)
                        }
                        is BarChart -> {
                            val chartImage = Image(imageData).setWidth(400f).setHeight(300f).setMarginTop(10f).setMarginBottom(10f)
                            document.add(chartImage)
                        }
                        else -> println("Tipe chart tidak dikenal")
                    }
                }

                // Menutup dokumen
                document.close()
                println("PDF berhasil disimpan.")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            println("Terjadi kesalahan saat menyimpan PDF: ${e.message}")
        }
    }

    private val dataArray = arrayListOf(
        DataRiwayatSelesaiAdmin(
            "C-115",
            "Wajid",
            "Dr. Maya Trisilawati, MKM.",
            "Pos Sapa",
            "26 Januari 2024",
            "Online",
            "Via Chat",
            "Curhat saya Lorem Ipsum",
            "Internal",
            20
        ),
        DataRiwayatSelesaiAdmin(
            "C-115",
            "Aril",
            "Dr. Maya Trisilawati, MKM.",
            "Pos Sapa",
            "21 Januari 2024",
            "Offline",
            "Janji Tatap Muka/Offline",
            "Curhat saya Lorem 123",
            "Internal",
            21
        ),
        DataRiwayatSelesaiAdmin(
            "C-143",
            "Wilay",
            "Dr. Maya Trisilawati, MKM.",
            "Parenting",
            "26 Januari 2024",
            "Online",
            "Via Chat",
            "Curhat Lorem 123",
            "Internal",
            25
        ),
        DataRiwayatSelesaiAdmin(
            "C-413",
            "Isa",
            "Dr. Liko Maryudhiyanto, Sp.KJ., C.Ht",
            "Agama",
            "16 Januari 2024",
            "Online",
            "Via Chat",
            "Curhat saya 343",
            "Internal",
            27
        ),
        DataRiwayatSelesaiAdmin(
            "C-813",
            "Isa",
            "Dr. Lusy Liany, S.H., M.H.",
            "Agama",
            "6 Januari 2024",
            "Offline",
            "Janji Tatap Muka/Offline",
            "Curhat saya 124123",
            "Eksternal",
            27
        ),
        DataRiwayatSelesaiAdmin(
            "C-513",
            "Wajid",
            "Dr. Lusy Liany, S.H., M.H.",
            "Parenting",
            "2 Januari 2024",
            "Offline",
            "Janji Tatap Muka/Offline",
            "Curhat saya Lorem Dolor",
            "Eksternal",
            23
        ),
        DataRiwayatSelesaiAdmin(
            "C-513",
            "Wajid",
            "Dr. Lusy Liany, S.H., M.H.",
            "Parenting",
            "2 Februari 2024",
            "Offline",
            "Janji Tatap Muka/Offline",
            "Curhat saya Lorem Dolor",
            "Eksternal",
            22
        ),
        DataRiwayatSelesaiAdmin(
            "C-513",
            "Wajid",
            "Dr. Lusy Liany, S.H., M.H.",
            "Motivasi Belajar",
            "2 Maret 2024",
            "Offline",
            "Janji Tatap Muka/Offline",
            "Curhat saya Lorem Dolor",
            "Eksternal",
            22
        ),
        DataRiwayatSelesaiAdmin(
            "C-513",
            "Wajid",
            "Dr. Lusy Liany, S.H., M.H.",
            "Motivasi Belajar",
            "2 April 2024",
            "Offline",
            "Janji Tatap Muka/Offline",
            "Curhat saya Lorem Dolor",
            "Eksternal",
            22
        ),
        DataRiwayatSelesaiAdmin(
            "C-513",
            "Wajid",
            "Dr. Lusy Liany, S.H., M.H.",
            "Finansial",
            "2 Mei 2024",
            "Offline",
            "Janji Tatap Muka/Offline",
            "Curhat saya Lorem Dolor",
            "Eksternal",
            30
        ),
    )
    // refrensi
    fun savePdfWithChartAndText3(context: Context, chartBitmap: Bitmap, text: String) {
        try {
            val pdfFileName = "ChartWithText.pdf"
            val outputStream: OutputStream?

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                // Android 10+ menggunakan MediaStore
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, pdfFileName)
                    put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
                }

                val resolver = context.contentResolver
                val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)

                if (uri == null) {
                    println("Gagal membuat URI untuk penyimpanan file.")
                    return
                }

                outputStream = resolver.openOutputStream(uri)
            } else {
                // Android 9 ke bawah menggunakan penyimpanan eksternal langsung
                val pdfPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).absolutePath
                val file = File(pdfPath, pdfFileName)
                outputStream = FileOutputStream(file)
            }

            if (outputStream != null) {
                // Membuat file PDF
                val pdfWriter = PdfWriter(outputStream)
                val pdfDocument = com.itextpdf.kernel.pdf.PdfDocument(pdfWriter)
                val document = Document(pdfDocument, PageSize.A4)

                // Menambahkan teks ke PDF
                val paragraph = Paragraph(text).setFontSize(12f)
                document.add(paragraph)

                // Menambahkan chart sebagai gambar ke PDF
                val stream = java.io.ByteArrayOutputStream()
                chartBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
                val imageData = ImageDataFactory.create(stream.toByteArray())
                val chartImage = Image(imageData).setWidth(400f).setHeight(300f)
                document.add(chartImage)

                // Menutup dokumen
                document.close()
                println("PDF berhasil disimpan.")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            println("Terjadi kesalahan saat menyimpan PDF: ${e.message}")
        }
    }
    // Filter Data Tanpa Harus Pilih Bulan Dan tahun
    // Filter Muncul Semua Data di awal
    private fun filterDataAll(dataArray: List<DataRiwayatSelesaiAdmin>) {
        val bulanDipilih = spinnerBulan.selectedItem.toString()
        val tahunDipilih = spinnerTahun.selectedItem.toString()

        // Filter jika opsi "Pilih" belum diubah
        if (bulanDipilih == "Pilih" && tahunDipilih == "Pilih") {
            pieChart1(dataArray)
            setupBarChart3(dataArray)
            return
        }

        // Filter Data
        val filteredData = dataArray.filter { data ->
            val tanggalParts = data.tanggalSelesaiUser.split(" ")
            val bulan = tanggalParts.getOrNull(1)
            val tahun = tanggalParts.getOrNull(2)
            (bulan == bulanDipilih || bulanDipilih == "Pilih") && (tahun == tahunDipilih || tahunDipilih == "Pilih")
        }

        pieChart1(filteredData)
        setupBarChart3(filteredData)
    }
    // Fungsi PieChart
    private fun pieChart2(receivedUserList: List<DataRiwayatSelesaiAdmin>) {
        val pieEntries = mutableListOf<PieEntry>()

        val kategoriCount = receivedUserList.groupBy { it.topikKonsultasi }.mapValues { it.value.size }

        kategoriCount.forEach { (kategori, count) ->
            pieEntries.add(PieEntry(count.toFloat(), kategori))
        }

        // Mengatur PieDataSet hanya jika ada data
        if (pieEntries.isNotEmpty()) {
            val pieDataSet = PieDataSet(pieEntries, "Kategori Konsultasi")
            val customColors = listOf(
                ContextCompat.getColor(this, R.color.c1),
                ContextCompat.getColor(this, R.color.c2),
                ContextCompat.getColor(this, R.color.c3),
                ContextCompat.getColor(this, R.color.c4),
                ContextCompat.getColor(this, R.color.c5),
                ContextCompat.getColor(this, R.color.c6),
                ContextCompat.getColor(this, R.color.c7),
                ContextCompat.getColor(this, R.color.c8),
                ContextCompat.getColor(this, R.color.c9),
                ContextCompat.getColor(this, R.color.c10),
                ContextCompat.getColor(this, R.color.c11),
                ContextCompat.getColor(this, R.color.c12),
                ContextCompat.getColor(this, R.color.c13)
            )
            pieDataSet.colors = customColors
            pieDataSet.valueTextSize = 16f
            val pieData = PieData(pieDataSet)

            val legend = pieChart.legend
            legend.orientation = Legend.LegendOrientation.VERTICAL // Orientasi legend vertikal
            legend.verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM // Atur posisi vertikal
            legend.horizontalAlignment = Legend.LegendHorizontalAlignment.LEFT // Atur posisi horizontal

            pieChart.data = pieData
            pieChart.setUsePercentValues(true)
            pieChart.animateY(1000)
        } else {
            pieChart.clear()
        }

        pieChart.invalidate() // Refresh chart
    }

    private fun setupBarChart3Tes(dataList: List<DataRiwayatSelesaiAdmin>) {
        val barEntries = mutableListOf<BarEntry>()
        val labels = mutableListOf<String>()

        // Mengelompokkan data berdasarkan bulan dan tahun
        val kategoriCount = dataList.groupBy { getMonthYear(it.tanggalSelesaiUser) }.mapValues { it.value.size }

        kategoriCount.entries.forEachIndexed { index, entry ->
            barEntries.add(BarEntry(index.toFloat(), entry.value.toFloat()))
            labels.add(entry.key)  // Menambahkan bulan dan tahun sebagai label
        }

        // Tambahkan dummy data jika hanya ada 1 entri
//        if (barEntries.size == 1) {
//            barEntries.add(BarEntry(1f, 0f)) // Dummy data
//            labels.add("Dummy") // Label dummy
//        }

        if (barEntries.isNotEmpty()) {
            // Membuat dataset untuk BarChart
            val barDataSet = BarDataSet(barEntries, "Rekap Data")
            barDataSet.valueTextSize = 14f
            barDataSet.color = ContextCompat.getColor(this, R.color.textbirufigma2)
            barDataSet.valueTextColor = ContextCompat.getColor(this, R.color.black)

            // Membuat objek BarData
            val barData = BarData(barDataSet)
            barData.barWidth = 0.7f

            // Menyusun data ke dalam chart
            barChart.data = barData
            barChart.setFitBars(true)
            barChart.description.isEnabled = false
            barChart.animateY(1000)

            // Mengatur sumbu X dengan label kategori
            barChart.xAxis.valueFormatter = IndexAxisValueFormatter(labels)
            barChart.xAxis.position = XAxis.XAxisPosition.BOTTOM
            barChart.xAxis.granularity = 1f
            barChart.xAxis.setDrawGridLines(false)
            barChart.xAxis.axisMinimum = 0f
            barChart.xAxis.axisMaximum = barEntries.size.toFloat()

            // Mengatur sumbu Y
            barChart.axisLeft.setDrawGridLines(false)
            barChart.axisLeft.axisMinimum = 0f // Pastikan sumbu Y mulai dari 0
            barChart.axisRight.isEnabled = false
        } else {
            barChart.clear()
        }

        barChart.invalidate()
    }
}




//private fun tesBarchart(dataArray: ArrayList<DataRiwayatSelesaiAdmin>) {
//    // Data untuk BarChart
//    // Data jumlah konseling per bulan
//    val jumlahKonseling = listOf(40f, 35f, 50f, 60f, 45f, 70f, 80f, 90f, 50f, 65f, 55f, 75f)
//    val bulan = listOf(
//        "Jan", "Feb", "Mar", "Apr", "Mei", "Jun",
//        "Jul", "Agu", "Sep", "Okt", "Nov", "Des"
//    )
//
//    // Menambahkan data ke BarEntry
//    val entries = ArrayList<BarEntry>()
//    for (i in jumlahKonseling.indices) {
//        entries.add(BarEntry(i.toFloat(), jumlahKonseling[i]))
//    }
//
//    // Konfigurasi dataset
//    val dataSet = BarDataSet(entries, "Jumlah Konseling")
//    dataSet.color = ContextCompat.getColor(this, R.color.textbirufigma2)
//    dataSet.valueTextColor = ContextCompat.getColor(this, R.color.black)
//    dataSet.valueTextSize = 14f
//
//    val barData = BarData(dataSet)
//    barChart.data = barData
//
//    // Konfigurasi sumbu X dengan nama bulan
//    val xAxis = barChart.xAxis
//    xAxis.position = XAxis.XAxisPosition.BOTTOM
//    xAxis.granularity = 1f
//    xAxis.valueFormatter = IndexAxisValueFormatter(bulan)
//
//    // Menonaktifkan grid background
//    barChart.setDrawGridBackground(false)
//    barChart.description.isEnabled = false
//
//    // Animasi chart
//    barChart.animateY(1000)
//    barChart.invalidate()
//}
// Fungsi BarChart
//private fun setupBarChart(dataList: List<DataRiwayatSelesaiAdmin>) {
//    val barEntries = mutableListOf<BarEntry>()
//    val labels = mutableListOf<String>()
//
//    val kategoriCount = dataList.groupBy { it.topikKonsultasi }.mapValues { it.value.size }
//
//    kategoriCount.entries.forEachIndexed { index, entry ->
//        barEntries.add(BarEntry(index.toFloat(), entry.value.toFloat()))
//        labels.add(entry.key)
//    }
//
//    // Jika data kosong
//    if (barEntries.isEmpty()) {
//        barChart.clear()
//        return
//    }
//
//    val barDataSet = BarDataSet(barEntries, "Kategori Konsultasi")
//    barDataSet.colors = listOf(
//        ContextCompat.getColor(this, R.color.c1),
//        ContextCompat.getColor(this, R.color.c2)
//    )
//    barDataSet.valueTextSize = 14f
//
//    val barData = BarData(barDataSet)
//    barData.barWidth = 0.7f
//
//    barChart.data = barData
//    barChart.setFitBars(true)
//    barChart.description.isEnabled = false
//    barChart.animateY(1000)
//
//    // Mengatur sumbu X dengan label
//    barChart.xAxis.valueFormatter = IndexAxisValueFormatter(labels)
//    barChart.xAxis.position = XAxis.XAxisPosition.BOTTOM
//    barChart.xAxis.granularity = 1f
//    barChart.xAxis.setDrawGridLines(false)
//
//    barChart.invalidate()
//}
















//class ProfileStatistikKonsultasi : BaseActivity() {
//    private lateinit var spinnerBulan: Spinner
//    private lateinit var spinnerTahun: Spinner
//    private lateinit var pieChart: PieChart
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_profile_statistik_konsultasi)
//        pieChart = findViewById(R.id.pieChart)
//        spinnerBulan = findViewById(R.id.spinnerBulan)
//        spinnerTahun = findViewById(R.id.spinnerTahun)
//
//        val receivedUserList = intent.getParcelableArrayListExtra<DataRiwayatSelesaiAdmin>("RIWAYAT")
//
//        val bulanArray = arrayOf("Pilih", "Januari", "Februari", "Maret", "April", "Mei","Juni", "Juli", "Agustus","September","Oktober","November","Desember")
//        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, bulanArray)
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) // Dropdown style
//        spinnerBulan.adapter = adapter
//
//        // Data Riwayat
//        val tahunArray = listOf("Pilih") + getUniqueYears(receivedUserList ?: emptyList())
//        val tahunAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, tahunArray)
//        tahunAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//        spinnerTahun.adapter = tahunAdapter
//
//        spinnerBulan.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
//            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
//                filterData2(receivedUserList ?: listOf())
//            }
//            override fun onNothingSelected(parent: AdapterView<*>) {}
//        }
//
//        spinnerTahun.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
//            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
//                filterData2(receivedUserList ?: listOf())
//            }
//            override fun onNothingSelected(parent: AdapterView<*>) {}
//        }
//
//
//
//        pieChart(receivedUserList)
//    }
//
//    private fun pieChart(receivedUserList: ArrayList<DataRiwayatSelesaiAdmin>?) {
//        // List untuk menyimpan data PieChart
//        val pieEntries = mutableListOf<PieEntry>()
//
//        Log.d("STATISTIK", "${receivedUserList}")
//        // Menghitung jumlah kemunculan setiap kategori layanan di dataArray
//        val kategoriCount = receivedUserList?.groupBy { it.topikKonsultasi }?.mapValues { it.value.size }
//
//        // Menambahkan data ke PieEntry berdasarkan kategoriCount
//        kategoriCount?.forEach { (kategori, count) ->
//            pieEntries.add(PieEntry(count.toFloat(), kategori))
//        }
//
//        // Membuat PieDataSet dengan data
//        val pieDataSet = PieDataSet(pieEntries, "Kategori Konsultasi")
//        val customColors = listOf(
//            ContextCompat.getColor(this, R.color.c1),
//            ContextCompat.getColor(this, R.color.c2),
//            ContextCompat.getColor(this, R.color.c3),
//            ContextCompat.getColor(this, R.color.c4),
//            ContextCompat.getColor(this, R.color.c5),
//            ContextCompat.getColor(this, R.color.c6),
//            ContextCompat.getColor(this, R.color.c7),
//            ContextCompat.getColor(this, R.color.c8),
//            ContextCompat.getColor(this, R.color.c9),
//            ContextCompat.getColor(this, R.color.c10),
//            ContextCompat.getColor(this, R.color.c11),
//            ContextCompat.getColor(this, R.color.c12),
//            ContextCompat.getColor(this, R.color.c13)
//        )
//        pieDataSet.colors = customColors
//        pieDataSet.valueFormatter = object : ValueFormatter() {
//            override fun getFormattedValue(value: Float): String {
//                // Format nilai sebagai persentase dengan dua angka desimal
//                return String.format("%.1f%%", value)
//            }
//        }
//
//        pieDataSet.valueTextSize = 16f
//        pieDataSet.valueTextColor = ContextCompat.getColor(this, R.color.black) // Atur warna teks (opsional)
//        pieDataSet.valueTypeface = Typeface.create("sans-serif-medium", Typeface.NORMAL)
//
//        // Membuat PieData dari PieDataSet
//        val pieData = PieData(pieDataSet)
//        pieChart.data = pieData
//        pieChart.setUsePercentValues(true)
//        pieChart.setCenterTextSize(12F)
//
//        // Mengatur Legend agar label vertikal
//        val legend = pieChart.legend
//        legend.orientation = Legend.LegendOrientation.VERTICAL // Orientasi legend vertikal
//        legend.verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM // Atur posisi vertikal
//        legend.horizontalAlignment = Legend.LegendHorizontalAlignment.LEFT // Atur posisi horizontal
//
//        pieChart.animateY(1000)
//        pieChart.invalidate()
//    }
//
//    private fun getUniqueYears(dataArray: List<DataRiwayatSelesaiAdmin>): List<String> {
//        return dataArray.mapNotNull { data ->
//            val tanggalParts = data.tanggalSelesaiUser.split(" ")
//            if (tanggalParts.size == 3) tanggalParts[2] else null
//        }.distinct().sorted()
//    }
//
//    private fun parseDate(tanggal: String): Date? {
//        return try {
//            val format = SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID"))
//            format.parse(tanggal)
//        } catch (e: ParseException) {
//            null
//        }
//    }
//
//    private fun filterData2(dataArray: List<DataRiwayatSelesaiAdmin>) {
//        val bulanDipilih = spinnerBulan.selectedItem.toString()
//        val tahunDipilih = spinnerTahun.selectedItem.toString()
//
//        if (bulanDipilih == "Pilih" || tahunDipilih == "Pilih") {
//
//        } else {
//            val filteredData = dataArray.filter { data ->
//                val tanggalParts = data.tanggalSelesaiUser.split(" ")
//                if (tanggalParts.size == 3) {
//                    val bulan = tanggalParts[1]
//                    val tahun = tanggalParts[2]
//                    bulan.equals(bulanDipilih, ignoreCase = true) && tahun == tahunDipilih
//                } else {
//                    false
//                }
//            }.sortedByDescending { parseDate(it.tanggalSelesaiUser) }
//
//            // Memanggil ulang pieChart dengan data yang sudah difilter
//            pieChart(filteredData as ArrayList<DataRiwayatSelesaiAdmin>)
//        }
//    }
//}





//private val layanan = listOf(
//    "Curhat", "Kesehatan Reproduksi",
//    "Hukum", "Agama",
//    "Kesehatan Mental Dan Psikologi", "Keluarga",
//    "Pos Sapa", "Motivasi Belajar",
//    "HIV", "Gizi",
//    "Parenting", "Disabilitas",
//    "Finansial"
//)





//    private val dataArray = arrayListOf(
//        DataRiwayatSelesaiAdmin(
//            "C-113","Wajid","Keluarga",
//            "26 Januari 2024","Online",
//            "Via Chat", "Curhat saya Lorem 123"),
//        DataRiwayatSelesaiAdmin(
//            "S-253","Nanas", "Curhat Remaja",
//            "21 Januari 2024","Online",
//            "Zoom", "Curhat saya Lorem 125"),
//        DataRiwayatSelesaiAdmin(
//            "S-213","Jaki", "Agama",
//            "26 Januari 2024","Online",
//            "Zoom", "Curhat saya Lorem 346"),
//        DataRiwayatSelesaiAdmin(
//            "S-243","Topik","Agama",
//            "26 Februari 2024","Online",
//            "Zoome", "Curhat saya Lorem 361"),
//        DataRiwayatSelesaiAdmin(
//            "B-263","Adon","Agama",
//            "26 Maret 2024","Online",
//            "Zoom", "Curhat saya Lorem 532"),
//        DataRiwayatSelesaiAdmin(
//            "C-253","Dani","Agama",
//            "26 Maret 2024","Online",
//            "Via Chat", "Curhat saya Lorem 20"),
//
//        DataRiwayatSelesaiAdmin(
//            "C-253","Jani","Finansial",
//            "26 Februari 2023","Online",
//            "Via Chat", "Curhat saya Lorem 20"),
//
//        DataRiwayatSelesaiAdmin(
//            "C-253","Pani","Kesehatan Mental dan Psikologi",
//            "26 Maret 2023","Online",
//            "Via Chat", "Curhat saya Lorem 20"),
//        DataRiwayatSelesaiAdmin(
//            "C-253","Jaja","Kesehatan Mental dan Psikologi",
//            "26 Maret 2023","Online",
//            "Via Chat", "Curhat saya Lorem 20"),
//        DataRiwayatSelesaiAdmin(
//            "W-253","Waka","Kesehatan Mental dan Psikologi",
//            "26 Maret 2023","Online",
//            "Via Chat", "Curhat saya Lorem 20"),
//    )