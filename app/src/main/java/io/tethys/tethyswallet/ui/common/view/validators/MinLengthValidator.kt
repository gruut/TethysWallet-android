package io.tethys.tethyswallet.ui.common.view.validators

import android.content.Context
import io.tethys.tethyswallet.ui.common.view.VtlValidationFailureException
import io.reactivex.Completable
import io.reactivex.schedulers.Schedulers

/**
 * Validation error when the text length is shorter
 */
class MinLengthValidator(
    private val errorMessage: String,
    private val minLength: Int,
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
        return text?.let {
            if (trim) it.trim() else it
        }?.length ?: 0 >= minLength
    }

    /**
     * @return error message
     */
    override fun getErrorMessage(): String {
        return errorMessage
    }

}