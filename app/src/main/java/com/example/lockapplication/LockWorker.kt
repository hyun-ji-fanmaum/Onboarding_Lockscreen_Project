package com.example.lockapplication

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

class LockWorker(
    private val context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    // 실행 코드
    override suspend fun doWork(): Result {
        Log.d("LockWorker", "LockWorker is working")

        if (isMyFgsAliveByNotification(context, LockService.SERVICE_ID)) {
            Log.d("LockWorker", "LockService is already running")

        } else if (SavedRepository.isServiceRunning(context)) {
            Log.d("LockWorker", "LockService is not running, starting service")

            val serviceIntent = Intent(context, LockService::class.java)
            ContextCompat.startForegroundService(applicationContext, serviceIntent)
        }

        return Result.success()
    }
}