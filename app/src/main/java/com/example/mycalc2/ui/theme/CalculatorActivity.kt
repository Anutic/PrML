package com.example.mycalc2

import android.app.NotificationChannel
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.widget.TextView
import kotlin.math.*
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mycalc2.adapters.HistoryAdapter
import com.example.mycalc2.utils.FirebaseManager
import com.example.mycalc2.utils.CalculationUtils
import com.example.mycalc2.ui.ThemeBottomSheetDialog
import com.example.mycalc2.utils.ThemeManager
import android.app.NotificationManager
import android.os.Build
import android.util.Log
import com.google.firebase.messaging.FirebaseMessaging

class CalculatorActivity : AppCompatActivity() {

    private lateinit var tvResult: TextView
    private var activeOperationButton: Button? = null
    private var activeFunctionButton: Button? = null
    private var currentNumber: String = ""
    private var previousNumber: String = ""
    private var operation: String? = null
    private var resultDisplayed: Boolean = false
    private lateinit var gestureDetector: GestureDetector

    private lateinit var firebaseManager: FirebaseManager
    private lateinit var historyAdapter: HistoryAdapter
    private val historyList = mutableListOf<Pair<String, String>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calculator)

        createNotificationChannel()

        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("FCM", "Fetching FCM registration token failed", task.exception)
                return@addOnCompleteListener
            }
            val token = task.result
            Log.d("FCM Token", token)
        }
        val rootLayout = findViewById<View>(R.id.rootLayout) // Получаем корневой layout
//        val savedColor = ThemeManager.getSavedThemeColor(this)
//        ThemeManager.applyTheme(rootLayout, savedColor) // Устанавливаем цвет при запуске
        ThemeManager.getSavedThemeColor(this) { color ->
            ThemeManager.applyTheme(rootLayout, color)
        }

        val btnThemeSettings: Button = findViewById(R.id.btnThemeSettings)
        btnThemeSettings.setOnClickListener {
            ThemeBottomSheetDialog(rootLayout).show(supportFragmentManager, "ThemeBottomSheetDialog")
        }

        firebaseManager = FirebaseManager()
        // Блокируем ориентацию экрана при запуске
//        ApiUtils.lockScreenOrientation(this)
        // Связываем элементы интерфейса
        tvResult = findViewById(R.id.tvResult)

        val btnCopy: Button = findViewById(R.id.btnCopy) // Кнопка для копирования результата
        val btnClear: Button = findViewById(R.id.btnClear)
        val btnPlusMinus: Button = findViewById(R.id.btnPlusMinus)
        val btnPercent: Button = findViewById(R.id.btnPercent)
        val btnAdd: Button = findViewById(R.id.btnAdd)
        val btnMinus: Button = findViewById(R.id.btnMinus)
        val btnMultiply: Button = findViewById(R.id.btnMultiply)
        val btnDivide: Button = findViewById(R.id.btnDivide)
        val btnEqual: Button = findViewById(R.id.btnEqual)
        val btnFloat: Button = findViewById(R.id.btnFloat)
        val btnClearEntry: Button = findViewById(R.id.btnClearEntry)
        val btnSin: Button = findViewById(R.id.btnSin)
        val btnCos: Button = findViewById(R.id.btnCos)
        val btnTan: Button = findViewById(R.id.btnTan)
        val btnCtg: Button = findViewById(R.id.btnCtg)
        val btnSqrt: Button = findViewById(R.id.btnSqrt)

        val numberButtons = listOf(
            findViewById<Button>(R.id.btn0),
            findViewById<Button>(R.id.btn1),
            findViewById<Button>(R.id.btn2),
            findViewById<Button>(R.id.btn3),
            findViewById<Button>(R.id.btn4),
            findViewById<Button>(R.id.btn5),
            findViewById<Button>(R.id.btn6),
            findViewById<Button>(R.id.btn7),
            findViewById<Button>(R.id.btn8),
            findViewById<Button>(R.id.btn9)
        )
        // Используем GestureUtils для числовых кнопок
        for (button in numberButtons) {
            GestureUtils.setButtonClickListener(this, button) {
                appendNumber(button.text.toString())
            }
        }
        // Кнопка очистки с длительной вибрацией
        GestureUtils.setButtonClickListener(this, btnClear, vibrationDuration = 100) {
            clear()
        }

        // Кнопка копирования результата
        GestureUtils.setButtonClickListener(this, btnCopy) {
            ApiUtils.copyToClipboard(this, tvResult.text.toString())
        }

        // Пример для кнопки сложения
        GestureUtils.setButtonClickListener(this, btnAdd) {
            setOperation("+")
        }

        // Равно
        GestureUtils.setButtonClickListener(this, btnEqual) {
            calculateResult()
        }

        // Создаем GestureDetector с нашим новым SwipeGestureListener
        gestureDetector = GestureDetector(this, SwipeGestureListener(
            onSwipeLeft = { handleSwipeLeft() },
            onSwipeRight = { handleSwipeRight() },
            onSwipeUp = { handleSwipeUp() },
            onSwipeDown = { handleSwipeDown() }
        ))


        // Назначаем слушатель свайпов на корневой layout
        rootLayout.setOnTouchListener { _, event ->
            gestureDetector.onTouchEvent(event)
            true
        }
        // Добавляем вибрацию и звук при нажатии числовых кнопок
        for (button in numberButtons) {
            button.setOnClickListener {
                appendNumber(button.text.toString())
                ApiUtils.vibrate(this) // Вибрация
                ApiUtils.playClickSound() // Звук
            }
        }
        // Добавляем вибрацию и звук для кнопки очистки
        btnClear.setOnClickListener {
            clear()
            ApiUtils.vibrate(this, 100) // Более длительная вибрация
            ApiUtils.playClickSound()
        }
        // Кнопка копирования результата
        btnCopy.setOnClickListener {
            ApiUtils.copyToClipboard(this, tvResult.text.toString())
            ApiUtils.vibrate(this, 50)
            ApiUtils.playClickSound()
        }

        // Обработчики операций
        btnAdd.setOnClickListener { setOperation("+") }
        btnMinus.setOnClickListener { setOperation("-") }
        btnMultiply.setOnClickListener { setOperation("*") }
        btnDivide.setOnClickListener { setOperation("/") }
        btnEqual.setOnClickListener { calculateResult() }
        btnFloat.setOnClickListener { appendNumber(".") }
        btnPlusMinus.setOnClickListener { toggleSign() }
        btnPercent.setOnClickListener { applyPercent() }
        btnClearEntry.setOnClickListener { clearEntry() }
        btnSin.setOnClickListener { applyFunction("sin") }
        btnCos.setOnClickListener { applyFunction("cos") }
        btnTan.setOnClickListener { applyFunction("tan") }
        btnCtg.setOnClickListener { applyFunction("ctg") }
        btnSqrt.setOnClickListener { applyFunction("sqrt") }
        // История
        val rvHistory: RecyclerView = findViewById(R.id.rvHistory)
        historyAdapter = HistoryAdapter(historyList)
        rvHistory.layoutManager = LinearLayoutManager(this)
        rvHistory.adapter = historyAdapter

        loadCalculationHistory()
    }

    // Переопределяем для обработки событий жестов
    override fun onTouchEvent(event: MotionEvent): Boolean {
        return gestureDetector.onTouchEvent(event) || super.onTouchEvent(event)
    }

    // Методы обработки свайпов
    private fun handleSwipeLeft() {
        appendNumber("0")
    }

    private fun handleSwipeRight() {
        if (currentNumber.isNotEmpty()) {
            currentNumber = currentNumber.dropLast(1)
            tvResult.text = if (currentNumber.isEmpty()) "0" else currentNumber
        }
    }

    private fun handleSwipeUp() {
        calculateResult()
    }

    private fun handleSwipeDown() {
        clear()
    }
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "default_channel_id"
            val channelName = "Default Channel"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId, channelName, importance)

            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }
//    private fun applyFunction(func: String) {
//        if (currentNumber.isEmpty()) return
//
//        val value = currentNumber.toDoubleOrNull() ?: return
//        val result = when (func) {
//            "sin" -> sin(Math.toRadians(value))
//            "cos" -> cos(Math.toRadians(value))
//            "tan" -> tan(Math.toRadians(value))
//            "ctg" -> if (tan(Math.toRadians(value)) != 0.0) 1 / tan(Math.toRadians(value)) else Double.NaN
//            "sqrt" -> if (value >= 0) sqrt(value) else Double.NaN
//            else -> return
//        }
//
//        tvResult.text = if (result.isNaN()) "Ошибка" else result.toString()
//        currentNumber = result.toString()
//        resultDisplayed = true
//    }
private fun applyFunction(func: String) {
    if (currentNumber.isEmpty()) return

    val value = currentNumber.toDoubleOrNull() ?: return
    val result = CalculationUtils.applyFunction(value, func)

    if (result.isNotEmpty()) {
        saveCalculationToFirebase("$func($currentNumber)", result)
        updateHistory("$func($currentNumber)", result)
        tvResult.text = result
        currentNumber = result
        resultDisplayed = true
    }
}
    private fun saveCalculationToFirebase(expression: String, result: String) {
        firebaseManager.saveCalculation(expression, result,
            { println("История сохранена") },
            { e -> println("Ошибка сохранения: $e") }
        )
    }

    private fun updateHistory(expression: String, result: String) {
        historyList.add(0, expression to result)
        historyAdapter.notifyItemInserted(0)
    }
    // Разблокируем ориентацию при выходе из активности
    override fun onDestroy() {
        super.onDestroy()
        ApiUtils.unlockScreenOrientation(this)
    }

    private fun clearEntry() {
        currentNumber = "0"
        tvResult.text = currentNumber
    }

    private fun appendNumber(number: String) {
        if (resultDisplayed) {
            currentNumber = ""
            resultDisplayed = false
        }
        if (currentNumber.length >= 9 && number != ".") return
        if (currentNumber == "0" && number != ".") {
            currentNumber = ""
        }

        if (number == "." && currentNumber.contains(".")) return
        currentNumber += number
        tvResult.text = currentNumber
    }

    private fun setOperation(op: String) {
        if (currentNumber.isEmpty() && previousNumber.isEmpty()) return

        if (previousNumber.isNotEmpty() && currentNumber.isNotEmpty()) {
            val result = when (operation) {
                "+" -> previousNumber.toDouble() + currentNumber.toDouble()
                "-" -> previousNumber.toDouble() - currentNumber.toDouble()
                "*" -> previousNumber.toDouble() * currentNumber.toDouble()
                "/" -> if (currentNumber == "0") Double.NaN else previousNumber.toDouble() / currentNumber.toDouble()
                else -> return
            }

            previousNumber = if (result.isNaN()) {
                "Ошибка"
            } else {
                result.toString()
            }
            tvResult.text = previousNumber
            currentNumber = ""
        } else if (currentNumber.isNotEmpty()) {
            previousNumber = currentNumber
            currentNumber = ""
        }

        operation = op
        // Обновляем фон активной кнопки
        val operationButton = when (op) {
            "+" -> findViewById<Button>(R.id.btnAdd)
            "-" -> findViewById<Button>(R.id.btnMinus)
            "*" -> findViewById<Button>(R.id.btnMultiply)
            "/" -> findViewById<Button>(R.id.btnDivide)
            else -> null
        }

        // Сбрасываем подсветку у предыдущей активной кнопки
        activeOperationButton?.setBackgroundResource(R.drawable.rounded_button)

        // Подсвечиваем новую кнопку
        operationButton?.setBackgroundResource(R.drawable.button_active)
        activeOperationButton = operationButton
    }

//    private fun calculateResult() {
//        if (currentNumber.isEmpty() || previousNumber.isEmpty() || operation == null) return
//
//        val result = when (operation) {
//            "+" -> previousNumber.toDouble() + currentNumber.toDouble()
//            "-" -> previousNumber.toDouble() - currentNumber.toDouble()
//            "*" -> previousNumber.toDouble() * currentNumber.toDouble()
//            "/" -> if (currentNumber == "0") Double.NaN else previousNumber.toDouble() / currentNumber.toDouble()
//            else -> return
//        }
//
//        tvResult.text = if (result.isNaN()) "Ошибка" else result.toString()
//        currentNumber = result.toString()
//        previousNumber = ""
//        operation = null
//        resultDisplayed = true
//
//        // Сбрасываем подсветку активной кнопки после вычисления
//        activeOperationButton?.setBackgroundResource(R.drawable.rounded_button)
//        activeOperationButton = null
//    }
    //2
//private fun calculateResult() {
//    val result = CalculationUtils.calculate(previousNumber, currentNumber, operation)
//    if (result.isNotEmpty()) {
//        tvResult.text = result
//        firebaseManager.saveCalculation("$previousNumber $operation $currentNumber", result,
//            { println("История сохранена") },
//            { e -> println("Ошибка сохранения: $e") }
//        )
//        historyList.add(0, "$previousNumber $operation $currentNumber" to result)
//        historyAdapter.notifyItemInserted(0)
//        previousNumber = ""
//        currentNumber = result
//        operation = null
//        resultDisplayed = true
//    }
//}
private fun calculateResult() {
    val result = CalculationUtils.calculate(previousNumber, currentNumber, operation)
    if (result.isNotEmpty()) {
        saveCalculationToFirebase("$previousNumber $operation $currentNumber", result)
        updateHistory("$previousNumber $operation $currentNumber", result)
        previousNumber = ""
        currentNumber = result
        operation = null
        resultDisplayed = true
    }
}

    private fun loadCalculationHistory() {
        firebaseManager.loadCalculationHistory(
            { history ->
                historyList.clear()
                historyList.addAll(history)
                historyAdapter.notifyDataSetChanged()
            },
            { e -> println("Ошибка загрузки истории: $e") }
        )
    }

    private fun clear() {
        currentNumber = ""
        previousNumber = ""
        operation = null
        tvResult.text = "0"
        // Сбрасываем подсветку активной кнопки
        activeOperationButton?.setBackgroundResource(R.drawable.rounded_button)
        activeOperationButton = null
    }

    private fun toggleSign() {
        if (currentNumber.isEmpty()) return
        currentNumber = if (currentNumber.startsWith("-")) {
            currentNumber.substring(1)
        } else {
            "-$currentNumber"
        }
        tvResult.text = currentNumber
    }

    private fun applyPercent() {
        if (currentNumber.isEmpty()) return
        currentNumber = (currentNumber.toDouble() / 100).toString()
        tvResult.text = currentNumber
    }
}
