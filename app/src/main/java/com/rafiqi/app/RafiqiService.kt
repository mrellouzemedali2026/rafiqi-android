package com.rafiqi.app

import android.app.*
import android.content.*
import android.graphics.*
import android.os.*
import android.view.*
import android.widget.TextView
import kotlin.random.Random

class RafiqiService: Service() {
    private val handler=Handler(Looper.getMainLooper()); private var overlay:View?=null
    private val task=object:Runnable{override fun run(){showMessage();handler.postDelayed(this, Random.nextLong(15*60*1000L,45*60*1000L))}}
    override fun onCreate(){super.onCreate(); val id="rafiqi"; val nm=getSystemService(NotificationManager::class.java); nm.createNotificationChannel(NotificationChannel(id,"رفيقي",NotificationManager.IMPORTANCE_LOW)); startForeground(1,Notification.Builder(this,id).setContentTitle("رفيقي يعمل").setContentText("سيعرض رسائلك من وقت لآخر").setSmallIcon(android.R.drawable.ic_dialog_info).build()); handler.postDelayed(task,5000)}
    private fun showMessage(){ val msgs=getSharedPreferences("rafiqi",MODE_PRIVATE).getStringSet("messages",emptySet())!!.toList(); if(msgs.isEmpty())return; val wm=getSystemService(WINDOW_SERVICE) as WindowManager; overlay?.let{runCatching{wm.removeView(it)}}; val v=TextView(this).apply{text=msgs.random();textSize=20f;setTextColor(Color.WHITE);setBackgroundColor(Color.argb(225,35,75,55));setPadding(42,30,42,30);gravity=Gravity.CENTER;layoutDirection=View.LAYOUT_DIRECTION_RTL}; val p=WindowManager.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT,WindowManager.LayoutParams.WRAP_CONTENT,WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,PixelFormat.TRANSLUCENT).apply{gravity=Gravity.TOP or Gravity.CENTER_HORIZONTAL;y=180}; runCatching{wm.addView(v,p);overlay=v;handler.postDelayed({runCatching{wm.removeView(v)};if(overlay===v)overlay=null},8000)} }
    override fun onDestroy(){handler.removeCallbacksAndMessages(null); overlay?.let{runCatching{(getSystemService(WINDOW_SERVICE) as WindowManager).removeView(it)}};super.onDestroy()}
    override fun onBind(i:Intent?)=null
}
