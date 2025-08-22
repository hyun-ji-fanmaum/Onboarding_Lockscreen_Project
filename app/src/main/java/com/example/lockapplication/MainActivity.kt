package com.example.lockapplication

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import com.example.lockapplication.ui.theme.LockApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LockApplicationTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        modifier = Modifier.padding(innerPadding),
                        startService = {
                            // TODO 서비스 실행 중이 아니면 실행시키기

                            val intent = Intent(this, LockService::class.java)

                            ContextCompat.startForegroundService(
                                this,
                                intent
                            )
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting(
    modifier: Modifier = Modifier,
    startService: () -> Unit
) {

    Column(
        modifier = modifier.fillMaxSize()
    ) {
        Button(
            onClick = {
                startService.invoke()
            },
        ) {
            Text(text = "잠금화면 시작")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    LockApplicationTheme {
        Greeting() {

        }
    }
}