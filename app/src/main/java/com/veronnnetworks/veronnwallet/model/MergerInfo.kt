package com.veronnnetworks.veronnwallet.model

import com.veronnnetworks.veronnwallet.ui.common.databinding.InputRule

data class MergerInfo(
    var ip: String?,
    var port: String?
) {
    constructor() : this("", "")

    val ipValidator = InputRule.IP_RULE
    val portValidator = InputRule.PORT_RULE

    fun validate(): Boolean {
        return ipValidator.valid(ip) && portValidator.valid(port)
    }
}