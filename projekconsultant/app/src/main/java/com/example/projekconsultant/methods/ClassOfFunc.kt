package com.example.projekconsultant.methods

import android.annotation.SuppressLint
import android.text.InputType
import android.view.MotionEvent
import android.widget.EditText
import com.example.projekconsultant.R

class ClassOfFunc {
    private var isPasswordVisible = false

    @SuppressLint("ClickableViewAccessibility")
    //Func Hide N Show Password
     fun setupPasswordToggle(editText: EditText) {
        var isPasswordVisible = false

        // Simpan gaya teks dan font family sebelum mengubah inputType
        val typeface = editText.typeface

        editText.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                // Cek apakah klik pada drawableEnd (kanan)
                if (event.rawX >= (editText.right - editText.compoundDrawables[2].bounds.width())) {
                    // Toggle visibilitas password
                    isPasswordVisible = !isPasswordVisible
                    if (isPasswordVisible) {
                        // Tampilkan password
                        editText.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                        editText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_eye_showpass, 0) // Ganti ikon ke showpass
                    } else {
                        // Sembunyikan password
                        editText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                        editText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_eye_hidepass, 0) // Ganti ikon ke hidepass
                    }
                    // Set cursor kembali ke akhir teks
                    editText.setSelection(editText.text.length)

                    // Setel ulang typeface agar font tidak berubah
                    editText.typeface = typeface

                    true
                } else {
                    false
                }
            } else {
                false
            }
        }
    }

    fun getPilihanStatusHubungan(editText: EditText){

    }



}