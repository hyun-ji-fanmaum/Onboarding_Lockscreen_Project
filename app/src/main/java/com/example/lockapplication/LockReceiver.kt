package com.example.lockapplication

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.lockapplication.ui.LockActivity

class LockReceiver: BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        Log.d("LockReceiver", "onReceive: Screen Off detected, launching LockActivity")

        val lockIntent = Intent(context, LockActivity::class.java)
        // recevier에서 start 하기 위해서는 필요한 설정 <- TODO 알아보기
        lockIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        // 잠금 화면 실행
        context.startActivity(lockIntent)
    }
}