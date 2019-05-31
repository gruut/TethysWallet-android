package io.tethys.tethyswallet.data.local

interface PreferenceHelper {
    var commonName: String?
    var ecPublicKey: ByteArray?
    var ecSecretKey: ByteArray?
    var isAutonym: Boolean
    val isSigner: Boolean
    val signerForeground: Boolean
    var worldId: String?
    var chainId: String?
}