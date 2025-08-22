package com.example.lockapplication

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LockService: Service() {

    private val CHANNEL_ID = "ForegroundServiceChannel"
    private val CHANNEL_NAME = "Lock Service Channel"
    private val SERVICE_ID = 1

    // 브로드캐스트 등록
    // private lateinit var lockReceiver: LockReceiver

    override fun onCreate() {
        super.onCreate()
        // 포그라운드 서비스 채널 생성
        // Android O 이상에서 필요
        createNotificationChannel(this)
    }

    override fun onStartCommand(
        intent: Intent?,
        flags: Int,
        startId: Int
    ): Int {
        Log.d("LockService", "onStartCommand called, starting foreground service")

        // 알림 생성
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("잠금화면 서비스")
            .setContentText("잠금 화면을 띄울 것 입니다.")
            .build()

        // 포그라운드 서비스로 시작
        startForeground(SERVICE_ID, notification)

        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        TODO("Not yet implemented")
    }

    private fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_LOW // 보통 서비스는 낮은 중요도로 설정
            )

            // 서비스 채널을 생성하고 알림 매니저에 등록
            context.getSystemService(NotificationManager::class.java).apply {
                createNotificationChannel(serviceChannel)
            }

        }
    }

}