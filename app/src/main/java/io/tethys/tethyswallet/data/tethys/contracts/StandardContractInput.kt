package io.tethys.tethyswallet.data.tethys.contracts

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.cbor.CBORFactory
import io.tethys.tethyswallet.data.local.PreferenceHelper
import io.tethys.tethyswallet.ui.BaseApp

abstract class StandardContractInput {
    abstract val contractType: ContractType

    private val prefHelper: PreferenceHelper = BaseApp.prefHelper

    fun getContractId(): String =
        contractType.value + "::" + prefHelper.worldCreator + "::" + prefHelper.chainId + "::" + prefHelper.worldId

    fun toCBOR(): ByteArray = with(ObjectMapper(CBORFactory())) {
        writeValueAsBytes(this@StandardContractInput)
    }

    enum class ContractType(val value: String) {
        NON_ANONYMOUS_USER_JOIN("NON-ANONYMOUS-USER-JOIN"),
        UPDATE_USER_CERTIFICATE("UPDATE-USER-CERTIFICATE"),
        VALUE_TRANSFER("VALUE-TRANSFER"),
        VALUE_CREATE("VALUE-CREATE"),
        VALUE_INCINERATE("VALUE-INCINERATE"),
        SCOPE_USER("SCOPE-USER");
    }
}