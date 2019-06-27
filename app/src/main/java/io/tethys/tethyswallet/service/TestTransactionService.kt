package io.tethys.tethyswallet.service

import io.tethys.tethyswallet.data.grpc.message.request.factory.MsgTxFactory

class TestTransactionService {
    companion object {
        fun send() {
            val transaction = MsgTxFactory.create()
            MessageSenderService.send(transaction)
        }
    }
}