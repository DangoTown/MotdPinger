/*
 * Copyright 2024 RTAkland
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */


package cn.rtast.motdpinger

import com.google.gson.Gson
import java.io.ByteArrayOutputStream
import java.io.DataInputStream
import java.io.DataOutputStream
import java.net.InetSocketAddress
import java.net.Socket

class MotdPinger {

    companion object {
        val gson = Gson()
    }

    private fun readPacket(dataInput: DataInputStream, dataOutput: DataOutputStream): String {
        dataInput.readVarInt()
        var id = dataInput.readVarInt()

        if (id == -1 || id != 0x00) throw Exception()

        val length = dataInput.readVarInt()

        if (length == -1 || length == 0) throw Exception()

        val bytesInput = ByteArray(length)
        dataInput.readFully(bytesInput)
        val stringInput = String(bytesInput)

        val timeNow = System.currentTimeMillis()
        dataOutput.writeByte(0x09)
        dataOutput.writeByte(0x01)
        dataOutput.writeLong(timeNow)

        dataInput.readVarInt()
        id = dataInput.readVarInt()
        if (id == -1 || id != 0x01) throw Exception()

        return stringInput
    }

    private fun handshakePacket(host: String, port: Int): ByteArrayOutputStream {
        val baos = ByteArrayOutputStream()
        val handshakePacket = DataOutputStream(baos)
        handshakePacket.writeByte(0x00)
        handshakePacket.writeVarInt(4)
        handshakePacket.writeVarInt(host.length)
        handshakePacket.writeBytes(host)
        handshakePacket.writeShort(port)
        handshakePacket.writeVarInt(1)
        return baos
    }

    fun pingServer(host: String, port: Int = 25565): ServerMOTDResponse? {
        val address = InetSocketAddress(host, port)
        if (address.isUnresolved) return null

        val socket = Socket()
        socket.connect(address)

        val outStream = socket.getOutputStream()
        val inputStream = socket.getInputStream()

        val dataOutput = DataOutputStream(outStream)
        val dataInput = DataInputStream(inputStream)

        val handshakePacket = this.handshakePacket(host, port)

        dataOutput.writeVarInt(handshakePacket.size())
        dataOutput.write(handshakePacket.toByteArray())

        dataOutput.writeByte(0x01)
        dataOutput.writeByte(0x00)
        val stringResponse = this.readPacket(dataInput, dataOutput)

        println(stringResponse)

        return gson.fromJson(stringResponse, ServerMOTDResponse::class.java)
    }

}


fun main() {
    val pinger = MotdPinger().pingServer("localhost")
}