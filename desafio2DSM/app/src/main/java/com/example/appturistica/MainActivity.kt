package com.example.appturistica

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.appturistica.ui.AppNavigation
import com.example.appturistica.ui.theme.AppTuristicaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppTuristicaTheme {
                AppNavigation()
            }
        }
    }
}