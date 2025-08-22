package com.example.lockapplication

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.lockapplication.ui.LockActivity

class LockReceiver: BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        // 잠금 화면 실행
        // TODO 개별 Task 공부하기

        val lockIntent = Intent(context, LockActivity::class.java)
        //lockIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        context?.startActivity(lockIntent)
    }
}