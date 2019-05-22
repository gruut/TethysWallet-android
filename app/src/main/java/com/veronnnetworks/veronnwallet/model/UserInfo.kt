package com.veronnnetworks.veronnwallet.model

data class UserInfo(
    var name: String?,
    var birthDate: String?,
    var mobile: String?,
    var gender: String?
) {
    constructor() : this("", "", "", "")
}