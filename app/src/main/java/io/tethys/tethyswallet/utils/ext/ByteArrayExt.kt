package io.tethys.tethyswallet.utils.ext

import java.nio.ByteBuffer

fun Int.longBytes(): ByteArray = ByteBuffer.allocate(8).putLong(this.toLong()).array()
