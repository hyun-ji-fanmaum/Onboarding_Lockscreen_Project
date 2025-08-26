package com.example.lockapplication

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        // 포그라운드 서비스 시작

        // 사용자 실행 여부
        if (SavedRepository.isServiceRunning(context)) {
            val serviceIntent = Intent(context, LockService::class.java)
            context.startForegroundService(serviceIntent)
        }
    }
}