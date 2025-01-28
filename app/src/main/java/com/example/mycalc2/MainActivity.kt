package com.example.mycalc2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.mycalc2.ui.theme.MyCalc2Theme
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private var activeOperationButton: Button? = null
    private lateinit var tvResult: TextView
    private var currentNumber: String = ""
    private var previousNumber: String = ""
    private var operation: String? = null
    private var resultDisplayed: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Связываем элементы интерфейса
        tvResult = findViewById(R.id.tvResult)

        val btnClear: Button = findViewById(R.id.btnClear)
        val btnPlusMinus: Button = findViewById(R.id.btnPlusMinus)
        val btnPercent: Button = findViewById(R.id.btnPercent)
        val btnAdd: Button = findViewById(R.id.btnAdd)
        val btnMinus: Button = findViewById(R.id.btnMinus)
        val btnMultiply: Button = findViewById(R.id.btnMultiply)
        val btnDivide: Button = findViewById(R.id.btnDivide)
        val btnEqual: Button = findViewById(R.id.btnEqual)
        val btnFloat: Button = findViewById(R.id.btnFloat)

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

        // Обработчики для чисел
        for (button in numberButtons) {
            button.setOnClickListener { appendNumber(button.text.toString()) }
        }

        // Обработчики операций
        btnAdd.setOnClickListener { setOperation("+") }
        btnMinus.setOnClickListener { setOperation("-") }
        btnMultiply.setOnClickListener { setOperation("*") }
        btnDivide.setOnClickListener { setOperation("/") }
        btnEqual.setOnClickListener { calculateResult() }
        btnClear.setOnClickListener { clear() }
        btnFloat.setOnClickListener { appendNumber(".") }
        btnPlusMinus.setOnClickListener { toggleSign() }
        btnPercent.setOnClickListener { applyPercent() }
    }

    private fun appendNumber(number: String) {
        if (resultDisplayed) {
            currentNumber = ""
            resultDisplayed = false
        }

        if (number == "." && currentNumber.contains(".")) return
        currentNumber += number
        tvResult.text = currentNumber

        // Сбрасываем фон активной кнопки
        activeOperationButton?.setBackgroundResource(R.drawable.rounded_button)
        activeOperationButton = null
    }

    private fun setOperation(op: String) {
        if (currentNumber.isEmpty() && previousNumber.isEmpty()) return

        // Если текущая операция уже выбрана, просто обновляем её
        operation = op

        // Если это первая операция, запоминаем текущее число
        if (previousNumber.isEmpty()) {
            previousNumber = currentNumber
            currentNumber = ""
        }

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

    private fun calculateResult() {
        if (currentNumber.isEmpty() || previousNumber.isEmpty() || operation == null) return

        val result = when (operation) {
            "+" -> previousNumber.toDouble() + currentNumber.toDouble()
            "-" -> previousNumber.toDouble() - currentNumber.toDouble()
            "*" -> previousNumber.toDouble() * currentNumber.toDouble()
            "/" -> if (currentNumber == "0") Double.NaN else previousNumber.toDouble() / currentNumber.toDouble()
            else -> return
        }

        tvResult.text = if (result.isNaN()) "Ошибка" else result.toString()
        currentNumber = result.toString()
        previousNumber = ""
        operation = null
        resultDisplayed = true
    }

    private fun clear() {
        currentNumber = ""
        previousNumber = ""
        operation = null
        tvResult.text = "0"
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