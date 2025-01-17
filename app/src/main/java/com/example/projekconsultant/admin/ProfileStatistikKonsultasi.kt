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
    private lateinit var pieChartlayanankonsultasi: PieChart

    private lateinit var pieChartprofesiinternal: PieChart
    private lateinit var pieChartfakultas: PieChart

    private lateinit var pieChartprofesieksternal: PieChart
    private lateinit var pieChartjeniskelamin: PieChart
    private lateinit var pieChartstatus: PieChart
    private lateinit var pieChartdomisili: PieChart

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
        pieChart = findViewById(R.id.pieChart)
        barChartUmur = findViewById(R.id.horizontalBarChartUmur)

        pieChartnamakonselor = findViewById(R.id.pieChartnamakonselor)
        pieChartlayanankonsultasi =  findViewById(R.id.pieChartlayanankonsultasi)
        pieChartIorE = findViewById(R.id.pieChartIorE)

        pieChartprofesiinternal = findViewById(R.id.pieChartprofesiinternal)
        pieChartfakultas = findViewById(R.id.pieChartfakultas)
        pieChartprofesieksternal =  findViewById(R.id.pieChartprofesieksternal)
        pieChartjeniskelamin = findViewById(R.id.pieChartjeniskelamin)
        pieChartstatus = findViewById(R.id.pieChartstatus)
        pieChartdomisili =  findViewById(R.id.pieChartdomisili)


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
        //
        setupBarChart3(receivedUserList)
        pieChartLayananKonsultasi(receivedUserList)
        setupBarChartUmur(receivedUserList)
        pieChart1(receivedUserList)
        pieChartIOrE(receivedUserList)
        pieChartnamakonselor(receivedUserList)
        pieChartProfesiEksternal(receivedUserList)
        pieChartProfesiInternal(receivedUserList)
        pieChartfakultas(receivedUserList)
        pieChartGender(receivedUserList)
        pieChartStatus(receivedUserList)
        pieChartDomisili(receivedUserList)

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

    private fun pieChartLayananKonsultasi(receivedUserList: List<DataRiwayatSelesaiAdmin>?) {
        // List untuk menyimpan data PieChart
        val pieEntries = mutableListOf<PieEntry>()

        // Menghitung jumlah kemunculan setiap kategori layanan di dataArray
        val kategoriCount = receivedUserList?.groupBy { it.layananKonsultasi }?.mapValues { it.value.size }

        // Menambahkan data ke PieEntry berdasarkan kategoriCount
        kategoriCount?.forEach { (kategori, count) ->
            if(kategori.isEmpty()){
                pieEntries.add(PieEntry(count.toFloat(), "Tatap Muka"))
            }else{
                pieEntries.add(PieEntry(count.toFloat(), kategori))
            }
        }

        // Membuat PieDataSet dengan data
        val pieDataSet = PieDataSet(pieEntries, "Kategori Layanan")
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
        pieChartlayanankonsultasi.data = pieData
        pieChartlayanankonsultasi.setUsePercentValues(true)
        pieChartlayanankonsultasi.setCenterTextSize(12F)
        pieChartlayanankonsultasi.isVerticalScrollBarEnabled = true
        pieChartlayanankonsultasi.isHorizontalScrollBarEnabled = false
        pieChartlayanankonsultasi.setExtraOffsets(60f, 0f, 0f, 0f) // Menambah jarak di bagian bawah

        // Mengatur Legend agar label vertikal
        val legend = pieChartlayanankonsultasi.legend
        legend.orientation = Legend.LegendOrientation.VERTICAL // Orientasi legend vertikal
        legend.verticalAlignment = Legend.LegendVerticalAlignment.TOP // Atur posisi vertikal
        legend.horizontalAlignment = Legend.LegendHorizontalAlignment.LEFT // Atur posisi horizontal
        legend.setDrawInside(false) // Pastikan legend berada di luar chart

        pieChartlayanankonsultasi.animateY(1000)
        pieChartlayanankonsultasi.invalidate()
    }

    private fun pieChartProfesiInternal(receivedUserList: List<DataRiwayatSelesaiAdmin>?) {
        // List untuk menyimpan data PieChart
        val pieEntries = mutableListOf<PieEntry>()

        // Menambahkan data berdasarkan kategori tertentu (contoh: Internal vs Eksternal)
        val filteredKategori = receivedUserList?.filter { it.typeUser == "Internal" }

        // Menambahkan data ke PieEntry berdasarkan kategoriCount
        filteredKategori?.groupBy { it.profesi }?.mapValues { it.value.size }?.forEach { (kategori, count) ->
            pieEntries.add(PieEntry(count.toFloat(), kategori))
        }

        // Membuat PieDataSet dengan data
        val pieDataSet = PieDataSet(pieEntries, "Kategori Profesi Internal")
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
        pieChartprofesiinternal.data = pieData
        pieChartprofesiinternal.setUsePercentValues(true)
        pieChartprofesiinternal.setCenterTextSize(12F)
        pieChartprofesiinternal.isVerticalScrollBarEnabled = true
        pieChartprofesiinternal.isHorizontalScrollBarEnabled = false
        pieChartprofesiinternal.setExtraOffsets(60f, 0f, 0f, 0f) // Menambah jarak di bagian bawah

        // Mengatur Legend agar label vertikal
        val legend = pieChartprofesiinternal.legend
        legend.orientation = Legend.LegendOrientation.VERTICAL // Orientasi legend vertikal
        legend.verticalAlignment = Legend.LegendVerticalAlignment.TOP // Atur posisi vertikal
        legend.horizontalAlignment = Legend.LegendHorizontalAlignment.LEFT // Atur posisi horizontal
        legend.setDrawInside(false) // Pastikan legend berada di luar chart

        pieChartprofesiinternal.animateY(1000)
        pieChartprofesiinternal.invalidate()
    }


    private fun pieChartfakultas(receivedUserList: List<DataRiwayatSelesaiAdmin>?) {
        // List untuk menyimpan data PieChart
        val pieEntries = mutableListOf<PieEntry>()

        // Menambahkan data berdasarkan kategori tertentu (contoh: Internal vs Eksternal)
        val filteredKategori = receivedUserList?.filter { it.typeUser == "Internal" }

        // Menambahkan data ke PieEntry berdasarkan kategoriCount
        filteredKategori?.groupBy { it.fakultas }?.mapValues { it.value.size }?.forEach { (kategori, count) ->
            pieEntries.add(PieEntry(count.toFloat(), kategori))
        }

        // Membuat PieDataSet dengan data
        val pieDataSet = PieDataSet(pieEntries, "Kategori Fakultas")
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
        pieChartfakultas.data = pieData
        pieChartfakultas.setUsePercentValues(true)
        pieChartfakultas.setCenterTextSize(12F)
        pieChartfakultas.isVerticalScrollBarEnabled = true
        pieChartfakultas.isHorizontalScrollBarEnabled = false
        pieChartfakultas.setExtraOffsets(60f, 0f, 0f, 0f) // Menambah jarak di bagian bawah

        // Mengatur Legend agar label vertikal
        val legend = pieChartfakultas.legend
        legend.orientation = Legend.LegendOrientation.VERTICAL // Orientasi legend vertikal
        legend.verticalAlignment = Legend.LegendVerticalAlignment.TOP // Atur posisi vertikal
        legend.horizontalAlignment = Legend.LegendHorizontalAlignment.LEFT // Atur posisi horizontal
        legend.setDrawInside(false) // Pastikan legend berada di luar chart

        pieChartfakultas.animateY(1000)
        pieChartfakultas.invalidate()
    }

    private fun pieChartProfesiEksternal(receivedUserList: List<DataRiwayatSelesaiAdmin>?) {
        // List untuk menyimpan data PieChart
        val pieEntries = mutableListOf<PieEntry>()

        // Menambahkan data berdasarkan kategori tertentu (contoh: Internal vs Eksternal)
        val filteredKategori = receivedUserList?.filter { it.typeUser == "Eksternal" }

        // Menambahkan data ke PieEntry berdasarkan kategoriCount
        filteredKategori?.groupBy { it.profesi }?.mapValues { it.value.size }?.forEach { (kategori, count) ->
            pieEntries.add(PieEntry(count.toFloat(), kategori))
        }

        // Membuat PieDataSet dengan data
        val pieDataSet = PieDataSet(pieEntries, "Kategori Profesi Eksternal")
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
        pieChartprofesieksternal.data = pieData
        pieChartprofesieksternal.setUsePercentValues(true)
        pieChartprofesieksternal.setCenterTextSize(12F)
        pieChartprofesieksternal.isVerticalScrollBarEnabled = true
        pieChartprofesieksternal.isHorizontalScrollBarEnabled = false
        pieChartprofesieksternal.setExtraOffsets(60f, 0f, 0f, 0f) // Menambah jarak di bagian bawah

        // Mengatur Legend agar label vertikal
        val legend = pieChartprofesieksternal.legend
        legend.orientation = Legend.LegendOrientation.VERTICAL // Orientasi legend vertikal
        legend.verticalAlignment = Legend.LegendVerticalAlignment.TOP // Atur posisi vertikal
        legend.horizontalAlignment = Legend.LegendHorizontalAlignment.LEFT // Atur posisi horizontal
        legend.setDrawInside(false) // Pastikan legend berada di luar chart

        pieChartprofesieksternal.animateY(1000)
        pieChartprofesieksternal.invalidate()
    }

    private fun pieChartGender(receivedUserList: List<DataRiwayatSelesaiAdmin>?) {
        // List untuk menyimpan data PieChart
        val pieEntries = mutableListOf<PieEntry>()

        // Menghitung jumlah kemunculan setiap kategori layanan di dataArray
        val kategoriCount = receivedUserList?.groupBy { it.gender }?.mapValues { it.value.size }

        // Menambahkan data ke PieEntry berdasarkan kategoriCount
        kategoriCount?.forEach { (kategori, count) ->
            pieEntries.add(PieEntry(count.toFloat(), kategori))
        }

        // Membuat PieDataSet dengan data
        val pieDataSet = PieDataSet(pieEntries, "Jenis Kelamin")
        val customColors = listOf(
            ContextCompat.getColor(this, R.color.ijofigmapudar),
            ContextCompat.getColor(this, R.color.textbirufigma2),
            ContextCompat.getColor(this, R.color.c1),
            ContextCompat.getColor(this, R.color.c2),
            ContextCompat.getColor(this, R.color.c3),
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
        pieChartjeniskelamin.data = pieData
        pieChartjeniskelamin.setUsePercentValues(true)
        pieChartjeniskelamin.setCenterTextSize(12F)
        pieChartjeniskelamin.isVerticalScrollBarEnabled = true
        pieChartjeniskelamin.isHorizontalScrollBarEnabled = false
        pieChartjeniskelamin.setExtraOffsets(60f, 0f, 0f, 0f) // Menambah jarak di bagian bawah

        // Mengatur Legend agar label vertikal
        val legend = pieChartjeniskelamin.legend
        legend.orientation = Legend.LegendOrientation.VERTICAL // Orientasi legend vertikal
        legend.verticalAlignment = Legend.LegendVerticalAlignment.TOP // Atur posisi vertikal
        legend.horizontalAlignment = Legend.LegendHorizontalAlignment.LEFT // Atur posisi horizontal
        legend.setDrawInside(false) // Pastikan legend berada di luar chart

        pieChartjeniskelamin.animateY(1000)
        pieChartjeniskelamin.invalidate()
    }

    private fun pieChartStatus(receivedUserList: List<DataRiwayatSelesaiAdmin>?) {
        // List untuk menyimpan data PieChart
        val pieEntries = mutableListOf<PieEntry>()

        // Menghitung jumlah kemunculan setiap kategori layanan di dataArray
        val kategoriCount = receivedUserList?.groupBy { it.status }?.mapValues { it.value.size }

        // Menambahkan data ke PieEntry berdasarkan kategoriCount
        kategoriCount?.forEach { (kategori, count) ->
            pieEntries.add(PieEntry(count.toFloat(), kategori))
        }

        // Membuat PieDataSet dengan data
        val pieDataSet = PieDataSet(pieEntries, "Status Klien")
        val customColors = listOf(
            ContextCompat.getColor(this, R.color.ijofigmapudar),
            ContextCompat.getColor(this, R.color.textbirufigma2),
            ContextCompat.getColor(this, R.color.c1),
            ContextCompat.getColor(this, R.color.c2),
            ContextCompat.getColor(this, R.color.c3),
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
        pieChartstatus.data = pieData
        pieChartstatus.setUsePercentValues(true)
        pieChartstatus.setCenterTextSize(12F)
        pieChartstatus.isVerticalScrollBarEnabled = true
        pieChartstatus.isHorizontalScrollBarEnabled = false
        pieChartstatus.setExtraOffsets(60f, 0f, 0f, 0f) // Menambah jarak di bagian bawah

        // Mengatur Legend agar label vertikal
        val legend = pieChartstatus.legend
        legend.orientation = Legend.LegendOrientation.VERTICAL // Orientasi legend vertikal
        legend.verticalAlignment = Legend.LegendVerticalAlignment.TOP // Atur posisi vertikal
        legend.horizontalAlignment = Legend.LegendHorizontalAlignment.LEFT // Atur posisi horizontal
        legend.setDrawInside(false) // Pastikan legend berada di luar chart

        pieChartstatus.animateY(1000)
        pieChartstatus.invalidate()
    }

    private fun pieChartDomisili(receivedUserList: List<DataRiwayatSelesaiAdmin>?) {
        // List untuk menyimpan data PieChart
        val pieEntries = mutableListOf<PieEntry>()

        // Menghitung jumlah kemunculan setiap kategori layanan di dataArray
        val kategoriCount = receivedUserList?.groupBy { it.domisili }?.mapValues { it.value.size }

        // Menambahkan data ke PieEntry berdasarkan kategoriCount
        kategoriCount?.forEach { (kategori, count) ->
            pieEntries.add(PieEntry(count.toFloat(), kategori))
        }

        // Membuat PieDataSet dengan data
        val pieDataSet = PieDataSet(pieEntries, "Domisili Klien")
        val customColors = listOf(
            ContextCompat.getColor(this, R.color.ijofigmapudar),
            ContextCompat.getColor(this, R.color.textbirufigma2),
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
        pieChartdomisili.data = pieData
        pieChartdomisili.setUsePercentValues(true)
        pieChartdomisili.setCenterTextSize(12F)
        pieChartdomisili.isVerticalScrollBarEnabled = true
        pieChartdomisili.isHorizontalScrollBarEnabled = false
        pieChartdomisili.setExtraOffsets(60f, 0f, 0f, 0f) // Menambah jarak di bagian bawah

        // Mengatur Legend agar label vertikal
        val legend = pieChartdomisili.legend
        legend.orientation = Legend.LegendOrientation.VERTICAL // Orientasi legend vertikal
        legend.verticalAlignment = Legend.LegendVerticalAlignment.TOP // Atur posisi vertikal
        legend.horizontalAlignment = Legend.LegendHorizontalAlignment.LEFT // Atur posisi horizontal
        legend.setDrawInside(false) // Pastikan legend berada di luar chart

        pieChartdomisili.animateY(1000)
        pieChartdomisili.invalidate()
    }


    // Fungsi filter berdasarkan Spinner
    @RequiresApi(Build.VERSION_CODES.Q)
    private fun filterData(dataArray: List<DataRiwayatSelesaiAdmin>) {
        val bulanDipilih = spinnerBulan.selectedItem.toString()
        val tahunDipilih = spinnerTahun.selectedItem.toString()

        // Filter jika opsi "Pilih" belum diubah
        if (bulanDipilih == "Pilih" && tahunDipilih == "Pilih") {
            // Bersihkan BarChart jika filter belum lengkap
            barChart.clear()
            pieChart.clear()
            barChartUmur.clear()

            pieChartnamakonselor.clear()
            pieChartlayanankonsultasi.clear()
            pieChartIorE.clear()

            pieChartprofesiinternal.clear()
            pieChartfakultas.clear()
            pieChartprofesieksternal.clear()
            pieChartjeniskelamin.clear()
            pieChartstatus.clear()
            pieChartdomisili.clear()

            teksInformation.text = "Pilih Terlebih Dahulu"
            return
        }

        // Filter jika hanya salah satu spinner yang dipilih
        if (bulanDipilih == "Pilih" || tahunDipilih == "Pilih") {
            // Bersihkan BarChart jika filter belum lengkap
            barChart.clear()
            pieChart.clear()
            barChartUmur.clear()

            pieChartnamakonselor.clear()
            pieChartlayanankonsultasi.clear()
            pieChartIorE.clear()

            pieChartprofesiinternal.clear()
            pieChartfakultas.clear()
            pieChartprofesieksternal.clear()
            pieChartjeniskelamin.clear()
            pieChartstatus.clear()
            pieChartdomisili.clear()

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

        // BarChart hanya muncul jika kedua spinner dipilih
        setupBarChart3(filteredData)
        pieChartLayananKonsultasi(filteredData)
        setupBarChartUmur(filteredData)
        pieChart1(filteredData)
        pieChartIOrE(filteredData)
        pieChartnamakonselor(filteredData)
        pieChartProfesiEksternal(filteredData)
        pieChartProfesiInternal(filteredData)
        pieChartfakultas(filteredData)
        pieChartGender(filteredData)
        pieChartStatus(filteredData)
        pieChartDomisili(filteredData)

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
        val kategoriCountUmur = receivedUserList?.groupBy { it.umur }?.mapValues { it.value.size }


        val kategoriCountProfesiInternal = receivedUserList?.filter { it.typeUser == "Internal" }?.groupBy { it.profesi }?.mapValues { it.value.size }
        val kategoriCountProfesiEksternal = receivedUserList?.filter { it.typeUser == "Eksternal" }?.groupBy { it.profesi }?.mapValues { it.value.size }


        val kategoriCountFakultas = receivedUserList?.groupBy { it.fakultas }?.mapValues { it.value.size }
        val kategoriCountGender = receivedUserList?.groupBy { it.gender }?.mapValues { it.value.size }
        val kategoriCountStatus = receivedUserList?.groupBy { it.status }?.mapValues { it.value.size }
        val kategoriCountDomisili = receivedUserList?.groupBy { it.domisili }?.mapValues { it.value.size }



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

            stringBuilder.appendLine("\n6. Tipe Umur:")
            kategoriCountUmur?.forEach { (key, value) ->
                stringBuilder.appendLine("   - $key: $value")
            }

            stringBuilder.appendLine("\n7. Tipe Profesi Internal:")
            kategoriCountProfesiInternal?.forEach { (key, value) ->
                if(key.isNotEmpty()){
                    stringBuilder.appendLine("   - $key: $value")
                }
            }

            stringBuilder.appendLine("\n7.1. Tipe Fakultas:")
            kategoriCountFakultas?.forEach { (key, value) ->
                if(key.isNotEmpty()){
                    stringBuilder.appendLine("   - $key: $value")
                }
            }

            stringBuilder.appendLine("\n8. Tipe Profesi Eksternal:")
            kategoriCountProfesiEksternal?.forEach { (key, value) ->
                if (key.isNotEmpty()){
                    stringBuilder.appendLine("   - $key: $value")
                }
            }

            stringBuilder.appendLine("\n9. Jenis Kelamin:")
            kategoriCountGender?.forEach { (key, value) ->
                if (key.isNotEmpty()){
                    stringBuilder.appendLine("   - $key: $value")
                }
            }


            stringBuilder.appendLine("\n10. Status:")
            kategoriCountStatus?.forEach { (key, value) ->
                if (key.isNotEmpty()){
                    stringBuilder.appendLine("   - $key: $value")
                }
            }

            stringBuilder.appendLine("\n11. Domisili:")
            kategoriCountDomisili?.forEach { (key, value) ->
                if (key.isNotEmpty()){
                    stringBuilder.appendLine("   - $key: $value")
                }
            }

            teksInformation.text = stringBuilder.toString()
        }

        downloadpdf.setOnClickListener {
            progbar.visibility = View.VISIBLE
            if (!receivedUserList.isNullOrEmpty()){
                Log.d("DOWNLOAD DATA", "${receivedUserList}")
                val chartList = listOf(barChart, pieChart, barChartUmur,
                    pieChartnamakonselor, pieChartlayanankonsultasi, pieChartIorE,
                    pieChartprofesiinternal, pieChartfakultas, pieChartprofesieksternal,
                    pieChartjeniskelamin, pieChartstatus, pieChartdomisili
                )
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
            nomorPendaftaranUser = "C-115",
            namaUser = "Dea",
            namaKonselor = "Dr. Maya Trisilawati, MKM.",
            topikKonsultasi = "Pos Sapa",
            tanggalSelesaiUser = "26 Januari 2024",
            layananOnlineOrOfflineUser = "Online",
            layananKonsultasi = "Via Chat",
            curhatanUser = "Curhat saya Lorem Ipsum",
            typeUser = "Internal",
            umur = 20,
            gender = "Perempuan",
            profesi = "Mahasiswa",
            fakultas = "FK",
            status = "Single",
            domisili = "DKI Jakarta"
        ),
        DataRiwayatSelesaiAdmin(
            nomorPendaftaranUser = "C-115",
            namaUser = "Caca",
            namaKonselor = "Dr. Maya Trisilawati, MKM.",
            topikKonsultasi = "Pos Sapa",
            tanggalSelesaiUser = "21 Januari 2024",
            layananOnlineOrOfflineUser = "Offline",
            layananKonsultasi = "Janji Tatap Muka/Offline",
            curhatanUser = "Curhat saya Lorem 123",
            typeUser = "Internal",
            umur = 21,
            gender = "Perempuan",
            profesi = "Mahasiswa",
            fakultas = "FTI",
            status = "Single",
            domisili = "DKI Jakarta"
        ),
        DataRiwayatSelesaiAdmin(
            nomorPendaftaranUser = "C-143",
            namaUser = "Wilay",
            namaKonselor = "Dr. Maya Trisilawati, MKM.",
            topikKonsultasi = "Parenting",
            tanggalSelesaiUser = "26 Januari 2024",
            layananOnlineOrOfflineUser = "Online",
            layananKonsultasi = "Via Chat",
            curhatanUser = "Curhat Lorem 123",
            typeUser = "Internal",
            umur = 25,
            gender = "Laki-laki",
            profesi = "Mahasiswa",
            fakultas = "FTI",
            status = "Single",
            domisili = "DKI Jakarta"
        ),
        DataRiwayatSelesaiAdmin(
            nomorPendaftaranUser = "C-413",
            namaUser = "Isa",
            namaKonselor = "Dr. Liko Maryudhiyanto, Sp.KJ., C.Ht",
            topikKonsultasi = "Agama",
            tanggalSelesaiUser = "16 Januari 2024",
            layananOnlineOrOfflineUser = "Online",
            layananKonsultasi = "Via Chat",
            curhatanUser = "Curhat saya 343",
            typeUser = "Internal",
            umur = 27,
            gender = "Laki-laki",
            profesi = "Tendik",
            fakultas = "FEB",
            status = "Single",
            domisili = "DKI Jakarta"
        ),
        DataRiwayatSelesaiAdmin(
            nomorPendaftaranUser = "C-813",
            namaUser = "Isa",
            namaKonselor = "Dr. Lusy Liany, S.H., M.H.",
            topikKonsultasi = "Agama",
            tanggalSelesaiUser = "6 Januari 2024",
            layananOnlineOrOfflineUser = "Offline",
            layananKonsultasi = "Janji Tatap Muka/Offline",
            curhatanUser = "Curhat saya 124123",
            typeUser = "Eksternal",
            umur = 27,
            gender = "Laki-laki",
            profesi = "Pegawai",
            status = "Menikah",
            domisili = "DKI Jakarta"
        ),
        DataRiwayatSelesaiAdmin(
            nomorPendaftaranUser = "C-813",
            namaUser = "Isa",
            namaKonselor = "Dr. Lusy Liany, S.H., M.H.",
            topikKonsultasi = "Agama",
            tanggalSelesaiUser = "6 Januari 2024",
            layananOnlineOrOfflineUser = "Offline",
            layananKonsultasi = "Janji Tatap Muka/Offline",
            curhatanUser = "Curhat saya 124123",
            typeUser = "Eksternal",
            umur = 27,
            gender = "Laki-laki",
            profesi = "Pengusaha",
            status = "Menikah",
            domisili = "Maluku Utara"
        )
    )
}