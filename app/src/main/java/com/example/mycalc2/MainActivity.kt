package com.example.mycalc2

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.mycalc2.ui.theme.MyCalc2Theme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyCalc2Theme {
                MainScreen()
            }
        }
    }

    @Composable
    fun MainScreen() {
        Button(onClick = { startCalculatorActivity() }) {
            Text("Open Calculator")
        }
    }

    private fun startCalculatorActivity() {
        val intent = Intent(this, CalculatorActivity::class.java)
        startActivity(intent)
    }
}
