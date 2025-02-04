package com.example.mycalc2

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.Surface
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity

object ApiUtils {

    // Вибрация устройства для обратной связи
    fun vibrate(context: Context, duration: Long = 50) {
        val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if (!vibrator.hasVibrator()) {
            // Устройство не поддерживает вибрацию
            return
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(duration, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(duration)
        }
    }

    // Проигрывание звукового сигнала при нажатии кнопок
    fun playClickSound() {
        val toneGen = ToneGenerator(AudioManager.STREAM_MUSIC, 100)
        toneGen.startTone(ToneGenerator.TONE_PROP_BEEP, 150)
    }

    // Копирование текста в буфер обмена
    fun copyToClipboard(context: Context, text: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Результат калькулятора", text)
        clipboard.setPrimaryClip(clip)
    }

    // Блокировка ориентации экрана
    fun lockScreenOrientation(activity: AppCompatActivity) {
        val rotation = (activity.getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay.rotation
        val orientation = when (rotation) {
            Surface.ROTATION_0, Surface.ROTATION_180 -> android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            else -> android.content.pm.ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        }
        activity.requestedOrientation = orientation
    }

    // Разблокировка ориентации экрана
    fun unlockScreenOrientation(activity: AppCompatActivity) {
        activity.requestedOrientation = android.content.pm.ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
    }
}
