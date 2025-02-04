package com.example.mycalc2

import android.content.Context
import android.view.View
import android.widget.Button

object GestureUtils {

    /**
     * Добавляет обработчик нажатия с вибрацией и звуком для любой кнопки.
     *
     * @param context Контекст приложения.
     * @param button Кнопка, на которую добавляется обработчик.
     * @param action Лямбда-функция, выполняемая при нажатии кнопки.
     * @param vibrationDuration Длительность вибрации в миллисекундах (по умолчанию 50 мс).
     */
    fun setButtonClickListener(
        context: Context,
        button: Button,
        vibrationDuration: Long = 50,
        action: (View) -> Unit
    ) {
        button.setOnClickListener { view ->
            ApiUtils.vibrate(context, vibrationDuration)
            ApiUtils.playClickSound()
            action(view)
        }
    }
}
