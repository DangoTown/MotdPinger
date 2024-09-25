/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/9/25
 */


package cn.rtast.motdpinger

interface MOTDPing {
    /**
     * ping服务器
     */
    fun ping(host: String, port: Int, timeout: Int): Response?
}