/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/10/10
 */

import cn.rtast.motdpinger.JavaPing


fun main() {
    println(JavaPing().ping("test.geysermc.org", 25565, 10000)?.latency)
}