package com.example.lockapplication

import android.Manifest
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.lockapplication.ui.theme.LockApplicationTheme
import java.util.concurrent.TimeUnit
import androidx.core.net.toUri


class MainActivity : ComponentActivity() {

    private val repeatWorker = PeriodicWorkRequestBuilder<LockWorker>(
        15, TimeUnit.MINUTES //15분마다 실행
    ).build()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LockApplicationTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        modifier = Modifier.padding(innerPadding),
                        startService = {
                            SavedRepository.setServiceRunning(this, true)

                            val intent = Intent(this, LockService::class.java)
                            ContextCompat.startForegroundService(applicationContext, intent)

                            WorkManager.getInstance(applicationContext)
                                .enqueueUniquePeriodicWork(
                                    "lock-worker",
                                    ExistingPeriodicWorkPolicy.KEEP,
                                    repeatWorker
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

        NotificationPermissionButton()
        OverlayPermissionCard()
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


@Composable
fun NotificationPermissionButton() {
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            Toast.makeText(context, "알림 권한 허용됨", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "알림 권한 거부됨", Toast.LENGTH_SHORT).show()
        }
    }

    Button(onClick = {
        if (Build.VERSION.SDK_INT >= 33) {
            launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
        } else {
            // 33 미만은 권한 없이 알림 가능
            Toast.makeText(context, "알림 권한 허용됨", Toast.LENGTH_SHORT).show()
        }
    }) { Text("알림 권한 요청") }
}

/** 권한 보유 여부 체크 */
fun Context.canDrawOverlaysCompat(): Boolean = Settings.canDrawOverlays(this)

/** 설정 화면 인텐트 */
fun Context.overlayPermissionIntent(): Intent =
    Intent(
        Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
        "package:$packageName".toUri()
    )

@Composable
fun OverlayPermissionCard(
    modifier: Modifier = Modifier,
    onGranted: () -> Unit = {}     // 권한 허용 후 할 작업(예: 오버레이 표시/서비스 시작)
) {
    val context = LocalContext.current
    var granted by remember { mutableStateOf(context.canDrawOverlaysCompat()) }

    // 설정 화면 → 복귀 콜백
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) {
        granted = context.canDrawOverlaysCompat()
        if (granted) onGranted()
    }

    Card(modifier = modifier.padding(16.dp)) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = if (granted) "오버레이 권한이 허용되어 있습니다."
                else "오버레이 권한이 필요합니다.",
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = if (granted)
                    "이제 다른 앱 위에 표시할 수 있어요."
                else
                    "설정 > ‘다른 앱 위에 표시’에서 앱을 허용해 주세요.",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(16.dp))

            if (granted) {
                Button(onClick = onGranted) {
                    Text("오버레이 시작")
                }
            } else {
                Button(onClick = {
                    launcher.launch(context.overlayPermissionIntent())
                }) {
                    Text("권한 설정 열기")
                }
            }
        }
    }
}
