package com.veronnnetworks.veronnwallet.data.grpc.message

import com.veronnnetworks.veronnwallet.utils.VeronnConfigs.CHAIN_ID_TYPE_SIZE
import com.veronnnetworks.veronnwallet.utils.VeronnConfigs.HEADER_LENGTH
import com.veronnnetworks.veronnwallet.utils.VeronnConfigs.IDENTIFIER
import com.veronnnetworks.veronnwallet.utils.VeronnConfigs.MSG_LENGTH_SIZE
import com.veronnnetworks.veronnwallet.utils.VeronnConfigs.MSG_VERSION
import com.veronnnetworks.veronnwallet.utils.VeronnConfigs.SENDER_ID_TYPE_SIZE
import com.veronnnetworks.veronnwallet.utils.VeronnConfigs.TEST_CHAIN_ID
import com.veronnnetworks.veronnwallet.utils.VeronnConfigs.TEST_WORLD_ID
import com.veronnnetworks.veronnwallet.utils.VeronnConfigs.WORLD_ID_TYPE_SIZE
import com.veronnnetworks.veronnwallet.utils.ext.decodeBase58
import com.veronnnetworks.veronnwallet.utils.ext.encodeToBase58String
import java.nio.ByteBuffer

class MsgHeader(
    private var identifier: Byte = IDENTIFIER,
    private var msgVersion: Byte = MSG_VERSION,
    var msgType: TypeMsg = TypeMsg.MSG_NULL,
    var macType: TypeMac = TypeMac.NONE,
    var serializationType: TypeSerialization = TypeSerialization.CBOR,
    private var dummy: Byte = 0x00,
    var totalLength: Int = 0,
    var worldId: String? = TEST_WORLD_ID,
    var chainId: String? = TEST_CHAIN_ID,
    var sender: String? = null
) {
    constructor(byteArray: ByteArray) : this() {
        var offset = 0
        identifier = byteArray[offset++]
        msgVersion = byteArray[offset++]
        msgType = TypeMsg.getByValue(byteArray[offset++]) ?: TypeMsg.MSG_NULL
        macType = TypeMac.getByValue(byteArray[offset++]) ?: TypeMac.NONE
        serializationType = TypeSerialization.getByValue(byteArray[offset++])
            ?: TypeSerialization.NONE
        dummy = byteArray[offset++]

        totalLength = ByteBuffer.wrap(byteArray, offset, MSG_LENGTH_SIZE).int
        offset += MSG_LENGTH_SIZE

        worldId = String(byteArray.copyOfRange(offset, offset + WORLD_ID_TYPE_SIZE))
        offset += WORLD_ID_TYPE_SIZE

        chainId = String(byteArray.copyOfRange(offset, offset + CHAIN_ID_TYPE_SIZE))
        offset += CHAIN_ID_TYPE_SIZE

        sender = byteArray.copyOfRange(offset, offset + SENDER_ID_TYPE_SIZE).encodeToBase58String()
    }

    fun toByteArray(): ByteArray {
        val buffer = ByteBuffer.allocate(HEADER_LENGTH)
            .put(identifier)
            .put(msgVersion)
            .put(msgType.type)
            .put(macType.type)
            .put(serializationType.type)
            .put(dummy)
            .putInt(totalLength)
            .put(worldId?.toByteArray(Charsets.UTF_8) ?: ByteArray(WORLD_ID_TYPE_SIZE))
            .put(chainId?.toByteArray(Charsets.UTF_8) ?: ByteArray(CHAIN_ID_TYPE_SIZE))
            .put(sender?.decodeBase58() ?: ByteArray(SENDER_ID_TYPE_SIZE))

        return buffer.array()
    }

    override fun toString(): String {
        return "header(" +
                "identifier=$identifier," +
                "msgVersion=$msgVersion," +
                "msgType=$msgType," +
                "macType=$macType," +
                "serializationType=$serializationType," +
                "totalLength=$totalLength," +
                "worldId=$worldId," +
                "chainId=$chainId," +
                "sender=$sender)"
    }
}