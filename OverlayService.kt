package com.surfersmenu

import android.app.Service
import android.content.Intent
import android.graphics.Color
import android.graphics.PixelFormat
import android.os.IBinder
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView

class OverlayService : Service() {
    private lateinit var wm: WindowManager
    private var bubble: View? = null
    private var panel: View? = null

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        wm = getSystemService(WINDOW_SERVICE) as WindowManager
        showBubble()
    }

    private fun layoutParams(): WindowManager.LayoutParams =
        WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        ).apply { gravity = Gravity.TOP or Gravity.START; x = 40; y = 180 }

    private fun showBubble() {
        val b = Button(this).apply {
            text = "S"
            textSize = 18f
            setTextColor(Color.WHITE)
            setBackgroundColor(Color.rgb(145, 70, 220))
            setOnClickListener { if (panel == null) showPanel() else hidePanel() }
        }
        bubble = b
        val lp = layoutParams()
        wm.addView(b, lp)
        b.setOnTouchListener(dragListener(lp) { view, params -> wm.updateViewLayout(view, params) })
    }

    private fun showPanel() {
        val box = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(20, 16, 20, 16)
            setBackgroundColor(Color.rgb(30, 25, 42))
        }
        box.addView(TextView(this).apply {
            text = "SURFERS MENU"
            textSize = 20f
            setTextColor(Color.rgb(190, 130, 255))
            setPadding(0, 0, 0, 12)
        })
        listOf("Speed", "Jump", "Magnet", "Auto Coins").forEach { label ->
            box.addView(Button(this).apply {
                text = "$label: OFF"
                setTextColor(Color.WHITE)
                setBackgroundColor(Color.rgb(75, 55, 100))
                setOnClickListener {
                    text = if (text.toString().endsWith("OFF")) "$label: ON" else "$label: OFF"
                }
            })
        }
        box.addView(Button(this).apply { text = "Close"; setOnClickListener { hidePanel() } })
        panel = box
        wm.addView(box, layoutParams().apply { x = 110; y = 180 })
    }

    private fun hidePanel() {
        panel?.let { wm.removeView(it) }
        panel = null
    }

    private fun dragListener(lp: WindowManager.LayoutParams,
        update: (View, WindowManager.LayoutParams) -> Unit) = View.OnTouchListener { view, event ->
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                view.tag = floatArrayOf(event.rawX, event.rawY, lp.x.toFloat(), lp.y.toFloat())
                true
            }
            MotionEvent.ACTION_MOVE -> {
                val a = view.tag as FloatArray
                lp.x = (a[2] + event.rawX - a[0]).toInt()
                lp.y = (a[3] + event.rawY - a[1]).toInt()
                update(view, lp)
                true
            }
            else -> false
        }
    }

    override fun onDestroy() {
        bubble?.let { wm.removeView(it) }
        hidePanel()
        super.onDestroy()
    }
}
