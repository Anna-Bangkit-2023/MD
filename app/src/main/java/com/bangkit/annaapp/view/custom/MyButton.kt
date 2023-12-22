package com.bangkit.annaapp.view.custom

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.Gravity
import androidx.core.content.ContextCompat
import com.google.android.material.R
import com.google.android.material.button.MaterialButton

class MyButton : MaterialButton {

    private lateinit var enabledButton: MaterialButton
    private lateinit var disabledButton: MaterialButton
    private var txtColor: Int = 0

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (isEnabled) {
            enabledButton.draw(canvas)
        } else {
            disabledButton.draw(canvas)
        }

        setTextColor(txtColor)
        textSize = 12f
        gravity = Gravity.CENTER
        text = if (isEnabled) "Submit" else "Fill in first"
    }

    private fun init() {
        txtColor = ContextCompat.getColor(context, android.R.color.background_light)
        enabledButton = MaterialButton(
            context,
            null,
            R.style.Widget_MaterialComponents_Button
        )
        disabledButton = MaterialButton(
            context,
            null,
            R.style.Widget_MaterialComponents_Button_OutlinedButton
        )
    }
}