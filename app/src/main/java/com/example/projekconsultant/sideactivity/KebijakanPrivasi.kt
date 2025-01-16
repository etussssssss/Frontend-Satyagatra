package com.example.projekconsultant.sideactivity

import android.os.Bundle
import android.text.Html
import android.webkit.WebView
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.projekconsultant.BaseActivity
import com.example.projekconsultant.R

class KebijakanPrivasi : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
        setContentView(R.layout.activity_kebijakan_privasi)
        overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_top)
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets
//        }

        val privacyPolicyHtml = """
            <html>
            <body>
                <p>Aplikasi Satyagatra adalah aplikasi penjadwalan konsultasi online berbasis di Universitas YARSI 
                yang memungkinkan pengguna untuk mengatur jadwal online atau janji tatap muka dengan konselor. 
                Kebijakan privasi ini menjelaskan bagaimana data pribadi Anda dikumpulkan, digunakan, dilindungi, dan hak-hak Anda terkait data tersebut.</p>
                
                
                <ul>
                    <li><p>Kami mengumpulkan informasi pribadi yang mencakup, nama, alamat email, nomor telepon,umur, dan pekerjaan. Informasi pribadi yang dikumpulkan digunakan untuk menyediakan layanan penjadwalan konsultasi, mengelola janji tatap muka dan online dengan konselor, serta untuk menghubungi Anda terkait pengingat jadwal atau notifikasi penting lainnya. Kami juga menggunakan data untuk meningkatkan kinerja dan pengalaman pengguna aplikasi.</p></li>
                    <li><p>Kami tidak membagikan data pribadi Anda kepada pihak ketiga kecuali dengan persetujuan Anda atau jika diwajibkan oleh hukum. Data Anda juga dapat diproses oleh penyedia layanan teknis kami untuk keperluan pemeliharaan dan pengembangan aplikasi.</p></li>
                    <li><p>Kami menerapkan langkah-langkah keamanan teknis dan organisasi untuk melindungi data pribadi Anda dari akses, pengungkapan, atau perubahan yang tidak sah. Namun, harap diingat bahwa tidak ada metode transmisi data melalui internet atau metode penyimpanan elektronik yang sepenuhnya aman.</p></li>
                    <li><p>Anda memiliki hak untuk mengakses, memperbarui, atau menghapus informasi pribadi Anda yang tersimpan di aplikasi Satyagatra. Jika Anda ingin melakukan perubahan pada data Anda atau memiliki pertanyaan terkait kebijakan privasi ini, Anda dapat menghubungi kami melalui informasi kontak yang disediakan.</p></li>
                    <li><p>Kebijakan privasi ini dapat diperbarui dari waktu ke waktu. Setiap perubahan signifikan akan diinformasikan melalui aplikasi ini.</p></li>
                    <li><p>Jika Anda memiliki pertanyaan, saran, atau keluhan terkait kebijakan privasi ini, Anda dapat menghubungi kami melalui <a href="mailto:ppks_yarsi@yarsi.ac.id">ppks_yarsi@yarsi.ac.id</a>.</p></li>
                </ul>
                
   
                   
            </body>
            
        
            <head>
                <style>
                    body {
                        font-family: Arial, sans-serif;
                        line-height: 1.6;
                        padding:0;
                        margin:0;
                    }
                    h2 {
                        font-size: 24px; /* Ukuran teks untuk heading 2 */
                    }
                    p {
                        font-size: 16px; /* Ukuran teks untuk paragraf */
                    }
                    ul {
                        font-size: 16px; /* Ukuran teks untuk list */
                
                        padding-left: 14px; /* Hapus padding kiri */
                        margin-left: 0px; /* Hapus margin kiri */
                    }
                    li {
                        margin-bottom: 8px; /* Jarak antara item list */
                    }
                    a {
                        color: blue; /* Warna link */
                        text-decoration: underline; /* Garis bawah pada link */
                    }
                </style>
            </head>
            </html>
        """.trimIndent()

        val webView: WebView = findViewById(R.id.webView)
        webView.loadData(privacyPolicyHtml, "text/html; charset=utf-8", "UTF-8")
        webView.settings.javaScriptEnabled = false

        findViewById<Button>(R.id.back).setOnClickListener {
            finish()
        }



    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.slide_in_top, R.anim.slide_out_bottom)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.slide_in_top, R.anim.slide_out_bottom)
    }

}