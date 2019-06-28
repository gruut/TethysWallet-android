package io.tethys.tethyswallet.service

import io.tethys.tethyswallet.data.grpc.message.request.factory.MsgTxFactory

class TestTransactionService {
    companion object {
        fun send(ipAddress: String, port: String) {
            val transaction = MsgTxFactory.create()

            val s = MessageSenderService()
            s.send(ipAddress, port, transaction)
        }
    }
}