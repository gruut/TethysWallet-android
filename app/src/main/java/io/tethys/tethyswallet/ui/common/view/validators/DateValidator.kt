package io.tethys.tethyswallet.ui.common.view.validators

import android.content.Context
import android.text.TextUtils
import io.tethys.tethyswallet.ui.common.view.VtlValidationFailureException
import io.reactivex.Completable
import io.reactivex.schedulers.Schedulers
import java.util.regex.Pattern

/**
 * Validation error when the text is invalid date format
 */
class DateValidator(
    private val errorMessage: String
) : VtlValidator {

    companion object {
        // TODO: Localization 에 맞춰 추후 수정 요망
        private val PATTERN = Pattern.compile("(\\d{2}(0[1-9]|1[0-2])(0[1-9]|[12]\\d|3[01]))")
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