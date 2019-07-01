package io.tethys.tethyswallet.service

import io.tethys.tethyswallet.data.grpc.message.request.factory.MsgTxFactory

class TestTransactionService constructor(
    private val messageSenderService: MessageSenderService
){
    fun send(ipAddress: String, port: String) {
        val transaction = MsgTxFactory.create()

        messageSenderService.send(ipAddress, port, transaction)
    }
}