package io.tethys.tethyswallet.data

import com.fasterxml.jackson.databind.ObjectMapper
import io.tethys.tethyswallet.data.grpc.message.request.MsgTx
import io.tethys.tethyswallet.data.tethys.contracts.NonAnonymUserJoin
import io.tethys.tethyswallet.utils.ext.getTimestamp
import org.junit.Test

class SmartContractJsonTest {

    @Test
    fun JsonSerializeTest() {

        val input =
            NonAnonymUserJoin(
                "timestamp",
                "FEMALE",
                "19930310",
                "ISC_TYPE",
                "ISC_CODE",
                20,
                "Incheon",
                "SIGNATURE"
            )
        val msgTx = MsgTx(
            "txid",
            getTimestamp(),
            "TETHYS",
            "TSTCHAIN",
            MsgTx.TxBody(
                "contract_id",
                "receiver_id",
                50,
                arrayOf(input)
            ),
            MsgTx.User(
                "user_id",
                "publickeystring",
                "user signature"
            ),
            arrayOf(
                MsgTx.Endorser(
                    "endorser_id",
                    "publickeystring",
                    "endorser signature"
                )
            )
        )
        val string = ObjectMapper().writeValueAsString(msgTx)
    }
}