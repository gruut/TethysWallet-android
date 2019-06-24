package io.tethys.tethyswallet.data

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.mockk.mockkStatic
import io.tethys.tethyswallet.data.grpc.message.request.MsgTx
import io.tethys.tethyswallet.data.tethys.contracts.NonAnonymUserJoin
import io.tethys.tethyswallet.ui.BaseApp
import io.tethys.tethyswallet.utils.ext.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class SmartContractJsonTest {
    @Before
    fun setup() {
        mockkStatic(BaseApp::class)
        BaseApp.prefHelper.commonName = "common_name".toSha256().encodeToBase58String()
        BaseApp.prefHelper.worldId = "_TETHYS_"
        BaseApp.prefHelper.chainId = "TSTCHAIN"
        BaseApp.prefHelper.worldCreator = "world_creator".toSha256().encodeToBase58String()
    }

    @Test
    fun msgTxTestAutonym() {
        // TODO: Endorser 관련 부분 정의되면 수정해야 함

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

        val string = jacksonObjectMapper().writeValueAsString(msgTx)
    }
}