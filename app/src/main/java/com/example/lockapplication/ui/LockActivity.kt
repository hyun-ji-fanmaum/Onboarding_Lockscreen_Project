package com.example.lockapplication.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement.Center
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.lockapplication.ui.theme.LockApplicationTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LockActivity: ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            LockApplicationTheme {
                LockScreen()
            }
        }
    }
}

@Composable
fun LockScreen() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = CenterHorizontally,
        verticalArrangement = Center
    ) {
        Text(text = "잠금 화면이 활성화되었습니다.")
    }
}

@Preview(
    showBackground = true
)
@Composable
fun LockScreenPreview() {
    LockScreen()
}