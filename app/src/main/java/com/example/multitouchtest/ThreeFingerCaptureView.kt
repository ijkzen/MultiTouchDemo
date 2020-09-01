package com.example.multitouchtest

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import androidx.appcompat.app.AppCompatActivity
import kotlin.math.abs

data class Pointer(val id: Int, val x: Float, val y: Float)

class ThreeFingerCaptureView : View {

    private var bitmap: Bitmap? = null

    private var activity: AppCompatActivity? = null

    private val pointerList = ArrayList<Pointer>()

    private val distance = ViewConfiguration.get(context).scaledTouchSlop * 20

    private var mount = 0

    constructor(context: Context?) : super(context)

    constructor(context: Context?, attributes: AttributeSet) : super(context, attributes)

    fun setActivity(activity: AppCompatActivity) {
        this.activity = activity
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        bitmap?.let {
            canvas?.drawBitmap(bitmap!!, 0F, 0F, null)
        }
    }

    private fun updateBackground() {
        bitmap = getCapture()
        invalidate()
    }

    private fun getCapture(): Bitmap? {
        return if (activity != null) {
            val rootView = activity!!.window.decorView
            val tmp =
                Bitmap.createBitmap(rootView.width, rootView.height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(tmp)
            rootView.draw(canvas)
            tmp
        } else {
            null
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event!!.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                Log.e("test", "on action down")
                addPointer(event)
            }
            MotionEvent.ACTION_POINTER_DOWN -> {
                Log.e("test", "on action pointer down")
                addPointer(event)
            }

            MotionEvent.ACTION_UP -> {
                Log.e("test", "on action up")
                removePointer(event)
            }
            MotionEvent.ACTION_POINTER_UP -> {
                Log.e("test", "on action pointer up")
                removePointer(event)
            }
        }
        return true
    }

    private fun addPointer(event: MotionEvent?) {
        event?.let {
            val id = it.getPointerId(it.actionIndex)
            Log.e("test", "down id: $id")
            pointerList.removeIf { pointer -> pointer.id == id }
            val pointer = Pointer(id, it.getX(it.actionIndex), it.getY(it.actionIndex))
            pointerList.add(pointer)

            Log.e("test", "downX: ${pointer.x}")
            Log.e("test", "downY: ${pointer.y}")
        }
    }

    private fun removePointer(event: MotionEvent?) {
        mount++
        event?.let {
            val id = it.getPointerId(it.actionIndex)
            if (isValidPointer(Pointer(id, it.getX(it.actionIndex), it.getY(it.actionIndex)))) {
                pointerList.removeIf { pointer -> pointer.id == id }
            }
        }

        if (pointerList.isEmpty()) {
            updateBackground()
        }

        if (mount % 3 == 0) {
            pointerList.clear()
            mount = 0
        }
    }

    private fun isValidPointer(pointer: Pointer): Boolean {
        val origin = pointerList.find { it.id == pointer.id }
        return if (origin != null) {
            val deltaY = pointer.y - origin.y
            val deltaX = pointer.x - origin.x

            Log.e("test", "up id: ${pointer.id}")
            Log.e("test", "upX: ${pointer.x}")
            Log.e("test", "upY: ${pointer.y}")
            Log.e("test", "deltaX: $deltaX")
            Log.e("test", "deltaY: $deltaY")

            deltaY >= distance && abs(deltaX) < 200
        } else {
            false
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        if (bitmap != null && !bitmap!!.isRecycled) {
            bitmap!!.recycle()
        }

        bitmap = null
    }
}