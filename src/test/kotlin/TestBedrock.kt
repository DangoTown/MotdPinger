/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/10/10
 */

import cn.rtast.motdpinger.BedrockPing

fun main() {
    println(BedrockPing().ping("test.geysermc.org", 19132, 10000).latency)
}