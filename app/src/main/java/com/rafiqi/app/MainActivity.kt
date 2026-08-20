package com.rafiqi.app

import android.app.*
import android.content.*
import android.graphics.Color
import android.net.Uri
import android.os.*
import android.provider.Settings
import android.view.Gravity
import android.widget.*

class MainActivity : Activity() {
    private lateinit var list: LinearLayout
    private val prefs by lazy { getSharedPreferences("rafiqi", MODE_PRIVATE) }
    override fun onCreate(b: Bundle?) { super.onCreate(b); buildUi() }
    private fun messages() = prefs.getStringSet("messages", setOf("ابتسم، فالحياة أجمل بالأمل"))!!.toMutableSet()
    private fun save(s:Set<String>) = prefs.edit().putStringSet("messages", s).apply()
    private fun buildUi() {
        val root=LinearLayout(this).apply { orientation=LinearLayout.VERTICAL; gravity=Gravity.CENTER_HORIZONTAL; setPadding(32,60,32,24); layoutDirection=android.view.View.LAYOUT_DIRECTION_RTL }
        root.addView(TextView(this).apply { text="رفيقي"; textSize=34f; gravity=Gravity.CENTER; setTextColor(Color.rgb(30,80,50)) })
        root.addView(TextView(this).apply { text="رسائل صغيرة ترافقك خلال يومك"; textSize=17f; gravity=Gravity.CENTER; setPadding(0,10,0,30) })
        val input=EditText(this).apply { hint="اكتب رسالة جديدة..."; gravity=Gravity.RIGHT; minLines=2 }
        root.addView(input, LinearLayout.LayoutParams(-1,-2))
        val add=Button(this).apply { text="إضافة الرسالة"; setOnClickListener { val t=input.text.toString().trim(); if(t.isNotEmpty()){ val m=messages(); m.add(t); save(m); input.text.clear(); refresh() } } }
        root.addView(add, LinearLayout.LayoutParams(-1,-2))
        val start=Button(this).apply { text="تشغيل رفيقي"; setOnClickListener { if(!Settings.canDrawOverlays(this@MainActivity)){ startActivity(Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName"))) } else { startForegroundService(Intent(this@MainActivity,RafiqiService::class.java)); Toast.makeText(this@MainActivity,"تم تشغيل رفيقي",Toast.LENGTH_SHORT).show() } } }
        root.addView(start, LinearLayout.LayoutParams(-1,-2))
        root.addView(Button(this).apply { text="إيقاف رفيقي"; setOnClickListener { stopService(Intent(this@MainActivity,RafiqiService::class.java)) } }, LinearLayout.LayoutParams(-1,-2))
        val scroll=ScrollView(this); list=LinearLayout(this).apply { orientation=LinearLayout.VERTICAL }; scroll.addView(list); root.addView(scroll,LinearLayout.LayoutParams(-1,0,1f)); setContentView(root); refresh()
    }
    private fun refresh(){ list.removeAllViews(); messages().forEach { msg -> val row=LinearLayout(this).apply{orientation=LinearLayout.HORIZONTAL}; row.addView(TextView(this).apply{text=msg;textSize=18f;setPadding(10,18,10,18)},LinearLayout.LayoutParams(0,-2,1f)); row.addView(Button(this).apply{text="حذف";setOnClickListener{val m=messages();m.remove(msg);save(m);refresh()}}); list.addView(row) } }
}
