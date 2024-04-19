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

import java.io.DataInputStream
import java.io.DataOutputStream

fun DataInputStream.readVarInt(): Int {
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

fun DataOutputStream.writeVarInt(value: Int) {
    var v = value
    while ((v and 0x7F.inv()) != 0) {
        this.write((v and 0x7F) or 0x80)
        v = v ushr 7
    }
    this.write(v)
}