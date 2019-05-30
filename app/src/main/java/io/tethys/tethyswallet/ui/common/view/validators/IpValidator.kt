package io.tethys.tethyswallet.ui.common.view.validators

import android.content.Context
import android.text.TextUtils
import android.util.Patterns
import io.tethys.tethyswallet.ui.common.view.VtlValidationFailureException
import io.reactivex.Completable
import io.reactivex.schedulers.Schedulers

/**
 * Validation error when the text is invalid ip address
 */
class IpValidator(
    private val errorMessage: String
) : VtlValidator {

    companion object {
        private val PATTERN = Patterns.IP_ADDRESS
    }

    override fun validateAsCompletable(context: Context, text: String?): Completable {
        return Completable.fromRunnable {
            if (!validate(text)) {
                throw VtlValidationFailureException(errorMessage)
            }
        }.subscribeOn(Schedulers.computation())
    }

    override fun validate(text: String?): Boolean {
        return TextUtils.isEmpty(text) || PATTERN.matcher(text).matches()
    }

    override fun getErrorMessage(): String {
        return errorMessage
    }

}