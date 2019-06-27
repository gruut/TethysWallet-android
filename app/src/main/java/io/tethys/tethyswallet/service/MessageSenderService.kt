package io.tethys.tethyswallet.service

import io.tethys.tethyswallet.data.grpc.GrpcService
import io.tethys.tethyswallet.data.grpc.message.request.MsgTx

class MessageSenderService {
    companion object {
        fun send(transaction: MsgTx) {
            print("Do Something")
        }
    }
}