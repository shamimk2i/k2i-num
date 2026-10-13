package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.ui.screens.MainConverterScreen
import com.example.ui.theme.NumK2ITheme
import com.example.viewmodel.ConverterViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: ConverterViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val uiState by viewModel.uiState.collectAsState()
            NumK2ITheme(darkTheme = uiState.isDarkTheme) {
                MainConverterScreen(viewModel = viewModel)
            }
        }
    }
}
