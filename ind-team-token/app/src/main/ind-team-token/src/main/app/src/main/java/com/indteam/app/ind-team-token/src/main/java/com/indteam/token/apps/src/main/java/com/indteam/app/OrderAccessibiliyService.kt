package com.indteam.app

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import android.util.Log

class OrderAccessibilityService : AccessibilityService() {

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
            Log.d("IND_TEAM_ORDER", orderText)
            checkOrder(orderText)
        }
    }

    private fun readNodeText(
        node: AccessibilityNodeInfo,
        result: StringBuilder
    ) {
        node.text?.let {
            result.append(it).append(" ")
        }

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

        if (price != null && distance != null) {
            if (price in minimumPrice..maximumPrice &&
                distance <= maximumDistance
            ) {
                Log.d(
                    "IND_TEAM_MATCH",
                    "Order memenuhi batas: Rp$price, $distance KM"
                )
            }
        }
    }

    override fun onInterrupt() {
        Log.d("IND_TEAM_ORDER", "Layanan dihentikan")
    }
}
