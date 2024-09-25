/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/9/3
 */


package cn.rtast.motdpinger

import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.nio.ByteBuffer

class BedrockPing : MOTDPing {

    private fun String.removeColorCodes(): String {
        return this.replace(Regex("§."), "")
    }

    private fun String.decodeHex(): ByteArray {
        return chunked(2).map { it.toInt(16).toByte() }.toByteArray()
    }


    override fun ping(host: String, port: Int, timeout: Int): BEPingResponse {
        val socket = DatagramSocket()
        socket.soTimeout = timeout
        val address = InetAddress.getByName(host)
        val buffer = ByteBuffer.allocate(25)
        buffer.put(0x01)
        buffer.putLong(0)
        buffer.put("00ffff00fefefefefdfdfdfd12345678".decodeHex())
        val requestPacket = DatagramPacket(buffer.array(), buffer.position(), address, port)
        socket.send(requestPacket)
        val responseBuffer = ByteArray(1024)
        val responsePacket = DatagramPacket(responseBuffer, responseBuffer.size)
        return socket.use {
            it.receive(responsePacket)
            val responseData = responsePacket.data
            val rawResponse = String(responseData, 35, responsePacket.length - 35)
            val splitResponse = rawResponse.split(";")
            BEPingResponse(
                rawResponse, splitResponse[1].removeColorCodes(),
                splitResponse[2].toInt(), splitResponse[3], splitResponse[4].toInt(),
                splitResponse[5].toInt(), splitResponse[6], splitResponse[7],
                splitResponse[8], splitResponse[9].toInt(), splitResponse[10].toInt(),
                splitResponse[11].toInt()
            )
        }
    }
}