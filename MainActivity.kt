package com.surfersmenu

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.graphics.Color

class MainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(32, 48, 32, 32)
            setBackgroundColor(Color.rgb(24, 20, 32))
        }
        root.addView(TextView(this).apply {
            text = "SURFERS MENU"
            textSize = 28f
            setTextColor(Color.rgb(190, 130, 255))
        })
        root.addView(TextView(this).apply {
            text = "Floating overlay demo • toggles are UI-only"
            textSize = 15f
            setTextColor(Color.LTGRAY)
            setPadding(0, 12, 0, 24)
        })
        root.addView(Button(this).apply {
            text = "Enable overlay / Open menu"
            setOnClickListener {
                if (!Settings.canDrawOverlays(this@MainActivity)) {
                    startActivity(Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:$packageName")))
                } else {
                    startService(Intent(this@MainActivity, OverlayService::class.java))
                    finish()
                }
            }
        })
        setContentView(root)
    }
}
