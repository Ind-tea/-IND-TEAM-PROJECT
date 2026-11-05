package com.indteam.app

import android.accessibilityservice.AccessibilityService
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import android.util.Log

class OrderAccessibilityService : AccessibilityService() {

    private val channelId = "ind_team_orders"
    private var lastNotification = ""
    private var lastNotificationTime = 0L

    override fun onServiceConnected() {
        super.onServiceConnected()
        createNotificationChannel()
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null) return

        if (event.eventType != AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED &&
            event.eventType != AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED &&
            event.eventType != AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED
        ) return

        val root = rootInActiveWindow ?: return
        val visibleText = StringBuilder()

        readNodeText(root, visibleText)

        val orderText = visibleText.toString()

        if (orderText.isNotBlank()) {
            checkOrder(orderText)
        }
    }

    private fun readNodeText(
        node: AccessibilityNodeInfo,
        result: StringBuilder
    ) {
        node.text?.let { result.append(it).append(" ") }
        node.contentDescription?.let {
            result.append(it).append(" ")
        }

        for (i in 0 until node.childCount) {
            val child = node.getChild(i) ?: continue
            readNodeText(child, result)
            child.recycle()
        }
    }

    private fun checkOrder(text: String) {
        val normalized = text.lowercase()

        val pricePattern = Regex(
            """(?:rp\s*)?(\d{1,3}(?:[.,]\d{3})+|\d{4,6})"""
        )

        val distancePattern = Regex(
            """(\d+(?:[.,]\d+)?)\s*(km|kilometer)"""
        )

        val price = pricePattern.find(normalized)
            ?.groupValues?.get(1)
            ?.replace(".", "")
            ?.replace(",", "")
            ?.toIntOrNull()

        val distance = distancePattern.find(normalized)
            ?.groupValues?.get(1)
            ?.replace(",", ".")
            ?.toFloatOrNull()

        val minimumPrice = 8000
        val maximumPrice = 200000
        val maximumDistance = 10f

        if (price != null && distance != null &&
            price in minimumPrice..maximumPrice &&
            distance <= maximumDistance
        ) {
            val signature = "$price-$distance"

            Log.d("IND_TEAM_MATCH", signature)
            showOrderNotification(price, distance, signature)
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Order IND TEAM",
                NotificationManager.IMPORTANCE_HIGH
            )

            val manager =
                getSystemService(Context.NOTIFICATION_SERVICE)
                    as NotificationManager

            manager.createNotificationChannel(channel)
        }
    }

    private fun showOrderNotification(
        price: Int,
        distance: Float,
        signature: String
    ) {
        val now = System.currentTimeMillis()

        if (signature == lastNotification &&
            now - lastNotificationTime < 60000
        ) return

        if (Build.VERSION.SDK_INT >= 33 &&
            checkSelfPermission(
                android.Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        val manager =
            getSystemService(Context.NOTIFICATION_SERVICE)
                as NotificationManager

        val builder = if (Build.VERSION.SDK_INT >= 26) {
            Notification.Builder(this, channelId)
        } else {
            Notification.Builder(this)
        }

        val notification = builder
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("IND TEAM: Order memenuhi batas")
            .setContentText("Harga Rp$price • Jarak $distance KM")
            .setAutoCancel(true)
            .build()

        manager.notify(1001, notification)

        lastNotification = signature
        lastNotificationTime = now
    }

    override fun onInterrupt() {
        Log.d("IND_TEAM_ORDER", "Layanan dihentikan")
    }
}
