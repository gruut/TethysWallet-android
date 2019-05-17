package com.veronnnetworks.veronnwallet.ui.common.databinding

import android.text.Editable
import android.text.TextWatcher
import androidx.databinding.BindingAdapter
import com.google.android.material.textfield.TextInputLayout

@BindingAdapter(value = ["app:setRule", "app:errorText"])
fun setErrorMessage(view: TextInputLayout, rule: InputRule, errorMessage: String) {
    view.editText?.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            view.isErrorEnabled = !rule.valid(view.editText?.text.toString())

            if (!rule.valid(view.editText?.text.toString())) {
                view.error = errorMessage
            } else {
                view.error = ""
            }
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        }

    })
}