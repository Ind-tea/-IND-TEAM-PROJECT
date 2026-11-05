package com.indteam.app

import android.accessibilityservice.AccessibilityService
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.PixelFormat
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.view.Gravity
import android.view.MotionEvent
import android.view.WindowManager
import android.widget.TextView
import kotlin.math.abs

class FloatingShortcut(
    private val service: AccessibilityService
) {
    private var windowManager: WindowManager? = null
    private var shortcut: TextView? = null

    fun show() {
        if (shortcut != null) return

        windowManager = service.getSystemService(
            Context.WINDOW_SERVICE
        ) as WindowManager

        val view = TextView(service).apply {
            text = "☾\nIND TEAM"
            setTextColor(Color.WHITE)
            textSize = 11f
            gravity = Gravity.CENTER
            typeface = Typeface.DEFAULT_BOLD

            background = GradientDrawable().apply {
                setColor(Color.BLACK)
                cornerRadius = 100f
                setStroke(2, Color.WHITE)
            }

            setPadding(10, 8, 10, 8)
        }

        val layout = WindowManager.LayoutParams(
            90,
            90,
            WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.START
            x = 20
            y = 300
        }

        var startX = 0
        var startY = 0
        var initialX = 0
        var initialY = 0

        view.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    startX = event.rawX.toInt()
                    startY = event.rawY.toInt()
                    initialX = layout.x
                    initialY = layout.y
                    true
                }

                MotionEvent.ACTION_MOVE -> {
                    layout.x = initialX + event.rawX.toInt() - startX
                    layout.y = initialY + event.rawY.toInt() - startY
                    windowManager?.updateViewLayout(view, layout)
                    true
                }

                MotionEvent.ACTION_UP -> {
                    val moved =
                        abs(event.rawX.toInt() - startX) > 10 ||
                        abs(event.rawY.toInt() - startY) > 10

                    if (!moved) {
                        val intent = Intent(
                            service,
                            MainActivity::class.java
                        ).apply {
                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        }
                        service.startActivity(intent)
                    }
                    true
                }

                else -> true
            }
        }

        shortcut = view
        windowManager?.addView(view, layout)
    }

    fun hide() {
        shortcut?.let {
            try {
                windowManager?.removeView(it)
            } catch (_: Exception) {
            }
        }
        shortcut = null
    }
}
