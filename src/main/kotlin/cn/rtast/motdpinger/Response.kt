/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/9/25
 */


package cn.rtast.motdpinger

interface Response {
    /**
     * 服务器返回的原始响应文本
     */
    val rawResponse: String

    /**
     * 服务器的延迟
     */
    var latency: Long
}