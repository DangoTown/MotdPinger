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

import com.google.gson.GsonBuilder
import java.io.ByteArrayOutputStream
import java.io.DataInputStream
import java.io.DataOutputStream
import java.net.InetSocketAddress
import java.net.Socket

class JavaPing : MOTDPing {

    private val gson = GsonBuilder().disableHtmlEscaping().create()

    private fun DataInputStream.readVarInt(): Int {
        var value = 0
        var i = 0
        var b: Int
        while (this.read().also { b = it } != -1) {
            value = value or ((b and 0x7F) shl i * 7)
            if (b and 0x80 == 0) {
                return value
            }
            i++
        }
        return -1
    }

    private fun DataOutputStream.writeVarInt(value: Int) {
        var v = value
        while ((v and 0x7F.inv()) != 0) {
            this.write((v and 0x7F) or 0x80)
            v = v ushr 7
        }
        this.write(v)
    }

    private fun readPacket(
        dataInput: DataInputStream,
        dataOutput: DataOutputStream,
        sendTime: Long
    ): Pair<String, Long> {
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
        val receiveTime = System.currentTimeMillis()
        return stringInput to receiveTime - sendTime
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

    override fun ping(host: String, port: Int, timeout: Int): JavaPingResponse? {
        val address = InetSocketAddress(host, port)
        if (address.isUnresolved) return null
        val socket = Socket()
        socket.use {
            it.connect(address, timeout)
            val outStream = socket.getOutputStream()
            val inputStream = socket.getInputStream()
            val dataOutput = DataOutputStream(outStream)
            val dataInput = DataInputStream(inputStream)
            val handshakePacket = this.handshakePacket(host, port)
            dataOutput.writeVarInt(handshakePacket.size())
            dataOutput.write(handshakePacket.toByteArray())
            dataOutput.writeByte(0x01)
            dataOutput.writeByte(0x00)
            val sendTime = System.currentTimeMillis()
            val (rawResponse, latency) = this.readPacket(dataInput, dataOutput, sendTime)
            val json = gson.fromJson(rawResponse, JavaPingResponse::class.java)
            json.rawResponse = rawResponse
            json.latency = latency
            return json
        }
    }
}
