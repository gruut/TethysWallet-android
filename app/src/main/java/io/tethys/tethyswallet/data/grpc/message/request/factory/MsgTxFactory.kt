package io.tethys.tethyswallet.data.grpc.message.request.factory

import io.tethys.tethyswallet.data.grpc.message.request.MsgTx
import io.tethys.tethyswallet.data.tethys.contracts.NonAnonymUserJoin
import io.tethys.tethyswallet.ui.BaseApp
import io.tethys.tethyswallet.utils.ext.decodeBase58
import io.tethys.tethyswallet.utils.ext.getTimestamp
import io.tethys.tethyswallet.utils.ext.longBytes
import io.tethys.tethyswallet.utils.ext.toSha256Hex

class MsgTxFactory {
    companion object {
        fun create(): MsgTx {
            val time = getTimestamp()
            val userId = BaseApp.prefHelper.commonName!!.decodeBase58()
            val world = BaseApp.prefHelper.worldId!!
            val chain = BaseApp.prefHelper.chainId!!
            val fee = 50

            val input = NonAnonymUserJoin(
                "timestamp",
                "FEMALE",
                "19930310",
                "ISC_TYPE",
                "ISC_CODE",
                20,
                "Incheon",
                "SIGNATURE"
            )

            val txid = (userId + world.toByteArray() + chain.toByteArray() +
                    time.longBytes() +
                    fee.longBytes() +
                    input.getContractId().toByteArray() +
                    input.toCBOR()).toSha256Hex()

            val msgTx = MsgTx(
                txid,
                time.toString(),
                world,
                chain,
                MsgTx.TxBody(
                    input.getContractId(),
                    null,
                    fee,
                    arrayOf(input)
                ),
                MsgTx.User(
                    BaseApp.prefHelper.commonName!!,
                    null, // 실명 사용자일 경우, null
                    "user signature"
                ),
                arrayOf()
            )

            return msgTx
        }
    }
}