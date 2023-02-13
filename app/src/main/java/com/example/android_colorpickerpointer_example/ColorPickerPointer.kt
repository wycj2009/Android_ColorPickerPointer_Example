package com.example.android_colorpickerpointer_example

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.view.doOnPreDraw
import androidx.core.view.isVisible

class ColorPickerPointer @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : View(context, attrs, defStyleAttr, defStyleRes) {
    var pickedColor: Int = Color.TRANSPARENT
    var viewMargin: Float = 1f
    var colorChipRadius: Float = 30f
    var colorChipY: Float = 30f
    private var capturedParentBitmap: Bitmap? = null
    private var colorChangedCallback: ((color: Int) -> Unit)? = null
    private val pickedColorPaint: Paint = Paint()
    private val touchBehavior = object {
        private var initX: Float = 0f
        private var initY: Float = 0f

        fun invoke(event: MotionEvent): Boolean {
            when (event.action and MotionEvent.ACTION_MASK) {
                MotionEvent.ACTION_DOWN -> {
                    initX = event.x
                    initY = event.y
                }
                MotionEvent.ACTION_MOVE -> {
                    val newX: Float = x + event.x - initX
                    val newY: Float = y + event.y - initY
                    val newPointerX: Float = newX + (width / 2f)
                    val newPointerY: Float = newY + height
                    val (parentWidth: Int, parentHeight: Int) = (parent as View).let { it.width to it.height }
                    x = when {
                        newPointerX < viewMargin -> -(width / 2f) + viewMargin
                        newPointerX > parentWidth - viewMargin -> parentWidth - (width / 2f) - viewMargin
                        else -> newX
                    }
                    y = when {
                        newPointerY < viewMargin -> -height + viewMargin
                        newPointerY > parentHeight - viewMargin -> parentHeight - height - viewMargin
                        else -> newY
                    }
                    pickColor()
                }
                MotionEvent.ACTION_UP -> {
                    initX = 0f
                    initY = 0f
                }
            }
            invalidate()
            return true
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        return touchBehavior.invoke(event)
    }

    override fun onDraw(canvas: Canvas) {
        canvas.drawCircle(width / 2f, colorChipY, colorChipRadius, pickedColorPaint.apply { color = pickedColor })
    }

    fun show(colorChangedCallback: (color: Int) -> Unit) {
        capturedParentBitmap = (parent as View).let { parentView: View ->
            Bitmap.createBitmap(parentView.width, parentView.height, Bitmap.Config.ARGB_8888).also { bitmap: Bitmap ->
                parentView.draw(Canvas(bitmap))
            }
        }
        this.colorChangedCallback = colorChangedCallback
        isVisible = true
        doOnPreDraw {
            pickColor()
        }
    }

    fun hide() {
        pickedColor = Color.TRANSPARENT
        capturedParentBitmap = null
        this.colorChangedCallback = null
        translationX = 0f
        translationY = 0f
        isVisible = false
    }

    private fun pickColor() {
        capturedParentBitmap?.let { bitmap: Bitmap ->
            pickedColor = bitmap.getPixel((x + (width / 2f)).toInt(), (y + height).toInt()).also { color: Int ->
                colorChangedCallback?.invoke(color)
            }
        }
    }
}
