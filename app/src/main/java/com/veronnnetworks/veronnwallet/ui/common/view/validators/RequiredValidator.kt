package com.veronnnetworks.veronnwallet.ui.common.view.validators

import android.content.Context
import android.text.TextUtils
import com.veronnnetworks.veronnwallet.ui.common.view.VtlValidationFailureException
import io.reactivex.Completable
import io.reactivex.schedulers.Schedulers

/**
 * Validation error when the text is empty.
 */
class RequiredValidator(
    private val errorMessage: String,
    private val trim: Boolean = true
) : VtlValidator {

    /**
     * Validate and return completable
     *
     * @param context
     * @param text
     * @return Completable
     * @throws Exception which contains the error message
     */
    override fun validateAsCompletable(context: Context, text: String?): Completable {
        return Completable.fromRunnable {
            if (!validate(text)) {
                throw VtlValidationFailureException(errorMessage)
            }
        }.subscribeOn(Schedulers.computation())
    }

    /**
     * Validate immediately
     *
     * @param text
     * @return result
     */
    override fun validate(text: String?): Boolean {
        return !TextUtils.isEmpty(text?.let { if (trim) it.trim() else it })
    }

    /**
     * @return error message
     */
    override fun getErrorMessage(): String {
        return errorMessage
    }

}