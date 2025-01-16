package com.example.projekconsultant.methods

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.view.View

class Animate {

    fun CustomJSDots(dot: View, isActive: Boolean) {
        val scale = if (isActive) 1f else 0.8f // Skala lebih besar untuk dot aktif
        val animator = ObjectAnimator.ofPropertyValuesHolder(
            dot,
            PropertyValuesHolder.ofFloat(View.SCALE_Y, scale),
            PropertyValuesHolder.ofFloat(View.SCALE_X, scale),
        )
        animator.duration = 250
        animator.start()
    }

    fun CustomJSDotsAlpha(dot: View, isActive: Boolean) {
            val alpha = if (isActive) 1.0f else 0.5f // Transparansi lebih tinggi untuk dot aktif
            val animator = ObjectAnimator.ofFloat(dot, "alpha", alpha)
            animator.duration = 300 // Durasi animasi dalam milidetik
            animator.startDelay = 200
            animator.start()
    }
}

