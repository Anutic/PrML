package com.example.mycalc2

import android.view.GestureDetector
import android.view.MotionEvent

class SwipeGestureListener(
    private val onSwipeLeft: () -> Unit,
    private val onSwipeRight: () -> Unit,
    private val onSwipeUp: () -> Unit,
    private val onSwipeDown: () -> Unit
) : GestureDetector.SimpleOnGestureListener() {

    private val SWIPE_THRESHOLD = 100
    private val SWIPE_VELOCITY_THRESHOLD = 100

    override fun onFling(
        e1: MotionEvent?,
        e2: MotionEvent,
        velocityX: Float,
        velocityY: Float
    ): Boolean {
        val diffX = e2?.x?.minus(e1?.x ?: 0f) ?: 0f
        val diffY = e2?.y?.minus(e1?.y ?: 0f) ?: 0f

        return when {
            // Горизонтальные свайпы
            kotlin.math.abs(diffX) > kotlin.math.abs(diffY) &&
                    kotlin.math.abs(diffX) > SWIPE_THRESHOLD &&
                    kotlin.math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD -> {
                if (diffX > 0) {
                    onSwipeRight()
                } else {
                    onSwipeLeft()
                }
                true
            }
            // Вертикальные свайпы
            kotlin.math.abs(diffY) > SWIPE_THRESHOLD &&
                    kotlin.math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD -> {
                if (diffY > 0) {
                    onSwipeDown()
                } else {
                    onSwipeUp()
                }
                true
            }
            else -> false
        }
    }
}
