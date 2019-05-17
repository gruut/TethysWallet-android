package com.veronnnetworks.veronnwallet.data.local

interface PreferenceHelper {
    var commonName: String?
    var ecPublicKey: ByteArray?
    var ecSecretKey: ByteArray?
    var isAutonym: Boolean
}