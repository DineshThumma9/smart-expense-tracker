package com.example.something.util



import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.util.Log
import android.view.accessibility.AccessibilityEvent

class PaymentAccessibilityService : AccessibilityService() {

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null) return

        // Detect if the payment was made (Based on Payment App package and UI Text)
        val eventText = event.text.toString()
        val packageName = event.packageName?.toString()

        Log.d("PaymentService", "App: $packageName - Text: $eventText")

        if (packageName != null && eventText.contains("payment successful", ignoreCase = true)) {
            // ✅ Payment detected - Show a bubble or notification
            PaymentBubbleManager.showBubble(this)
        }
    }

    override fun onInterrupt() {
        Log.d("PaymentService", "Accessibility Service Interrupted")
    }

    override fun onServiceConnected() {
        val info = AccessibilityServiceInfo().apply {
            eventTypes = AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED or
                    AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED
            feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC
            notificationTimeout = 100
        }
        this.serviceInfo = info
    }
}
