package com.bangkit.annaapp.view.custom

import android.content.Context
import android.graphics.Canvas
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import com.bangkit.annaapp.R
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class MyEditText : TextInputEditText {

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
    }

    private fun init() {
        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // Do nothing.
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                val parentLayout = this@MyEditText.parent.parent as? TextInputLayout

                if (this@MyEditText.id == R.id.emailEditText) {
                    val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
                    if (!s.isNullOrEmpty() && !s.matches(emailPattern.toRegex())) {
                        parentLayout?.error = context.getString(R.string.invalid_email_error)
                    } else {
                        parentLayout?.error = null
                    }
                } else if (this@MyEditText.id == R.id.passwordEditText) {
                    if (!s.isNullOrEmpty() && s.length < 8) {
                        parentLayout?.error = context.getString(R.string.short_password_error)
                    } else {
                        parentLayout?.error = null
                    }
                } else if (this@MyEditText.id == R.id.passwordConfirmEditText) {
                    if (!s.isNullOrEmpty() && s.length < 8) {
                        parentLayout?.error = context.getString(R.string.short_password_error)
                    } else {
                        parentLayout?.error = null
                    }
                }
            }

            override fun afterTextChanged(s: Editable) {
                // Do nothing.
            }
        })
    }
}