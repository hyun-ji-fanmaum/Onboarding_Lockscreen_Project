package com.example.lockapplication

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LockService: Service() {

    companion object {
        private const val CHANNEL_ID = "ForegroundServiceChannel"
        const val SERVICE_ID = 1001
    }

    private val CHANNEL_NAME = "Lock Service Channel"

    // 잠금화면 실행 시점 브로드캐스트 리시버 등록
    private val lockRunningReceiver: LockRunningReceiver = LockRunningReceiver()

    override fun onCreate() {
        super.onCreate()
        // 포그라운드 서비스 채널 생성
        createNotificationChannel(this)

        // 리시버 알림 필터 설정
        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_SCREEN_OFF)
        }

        // 리시버 등록
        if (Build.VERSION.SDK_INT >= 33) {
            registerReceiver(
                lockRunningReceiver,
                filter,
                Context.RECEIVER_NOT_EXPORTED // TODO flags 찾아보기
            )
        } else {
            @Suppress("DEPRECATION")
            registerReceiver(lockRunningReceiver, filter)
        }


        // TODO test
        mThread?.start()
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
            .setOngoing(true)
            .build()

        // 34버전부터는 서비스 타입 지정 필요
        val foregroundServiceType = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE
            } else {
                0 //ServiceInfo.FOREGROUND_SERVICE_TYPE_NONE
            }

        // 포그라운드 서비스로 시작
        ServiceCompat.startForeground(
            this,
            SERVICE_ID,
            notification,
            foregroundServiceType
        )


        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private fun createNotificationChannel(context: Context) {
        // Android O 이상에서 필요
        val serviceChannel = NotificationChannel(
            CHANNEL_ID,
            CHANNEL_NAME,
            // 서비스 중요도, 비교해보기
            NotificationManager.IMPORTANCE_HIGH
        )

        // 서비스 채널을 생성하고 알림 매니저에 등록
        getSystemService(NotificationManager::class.java)
            ?.createNotificationChannel(serviceChannel)

    }

    // TODO test
    private var mThread: Thread? = object : Thread("My Thread") {
        override fun run() {
            super.run()
            var i = 0
            while (true) {
                i += 1
                Log.d("Service Tread", "count : " + i)

                try {
                    sleep(1000)
                } catch (e: InterruptedException) {
                    currentThread().interrupt()
                    break
                }
            }
        }
    }

    override fun onDestroy() {
        // 서비스가 종료될 때 브로드캐스트 리시버 해제
        unregisterReceiver(lockRunningReceiver)

        // test
        if (mThread != null){
            mThread?.interrupt();
            mThread = null;
        }

        super.onDestroy()
    }
}

fun isMyFgsAliveByNotification(ctx: Context, notiId: Int): Boolean {
    val nm = ctx.getSystemService(NotificationManager::class.java)
    return nm.activeNotifications.any { it.id == notiId }
}