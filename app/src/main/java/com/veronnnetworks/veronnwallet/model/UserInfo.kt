package com.veronnnetworks.veronnwallet.model

import com.veronnnetworks.veronnwallet.ui.common.databinding.InputRule

data class UserInfo(
    var name: String?,
    var birthDate: String?,
    var mobile: String?,
    var gender: String?
) {
    constructor() : this("", "", "", "")

    val nameValidator = InputRule.NON_NULL_RULE
    val birthDateValidator = InputRule.NON_NULL_RULE
    val mobileValidator = InputRule.NON_NULL_RULE

    fun validate(): Boolean {
        return nameValidator.valid(name) && birthDateValidator.valid(birthDate) && mobileValidator.valid(
            mobile
        )
    }
}