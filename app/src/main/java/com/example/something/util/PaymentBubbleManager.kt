package com.example.something.util

import android.content.Context
import android.graphics.PixelFormat
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import com.example.something.R

object PaymentBubbleManager {
    private var bubbleView: View? = null

    fun showBubble(context: Context) {
        if (bubbleView != null) return // Prevent multiple bubbles

        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val layoutInflater = LayoutInflater.from(context)

        bubbleView = layoutInflater.inflate(R.layout.layout_payment_bubble, null)
        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        )
        params.gravity = Gravity.TOP or Gravity.END
        params.x = 20
        params.y = 100

        val closeButton = bubbleView!!.findViewById<ImageView>(R.id.close_bubble)
        closeButton.setOnClickListener {
            removeBubble(windowManager)
        }

        bubbleView!!.setOnTouchListener(object : View.OnTouchListener {
            var initialX = 0
            var initialY = 0
            var touchX = 0f
            var touchY = 0f

            override fun onTouch(v: View, event: MotionEvent): Boolean {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        initialX = params.x
                        initialY = params.y
                        touchX = event.rawX
                        touchY = event.rawY
                    }
                    MotionEvent.ACTION_MOVE -> {
                        params.x = initialX + (event.rawX - touchX).toInt()
                        params.y = initialY + (event.rawY - touchY).toInt()
                        windowManager.updateViewLayout(bubbleView, params)
                    }
                }
                return false
            }
        })

        windowManager.addView(bubbleView, params)
    }

    fun removeBubble(windowManager: WindowManager) {
        bubbleView?.let {
            windowManager.removeView(it)
            bubbleView = null
        }
    }
}
