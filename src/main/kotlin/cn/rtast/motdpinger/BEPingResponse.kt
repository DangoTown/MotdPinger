/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/9/26
 */


package cn.rtast.motdpinger

data class BEPingResponse(
    val motd: String,
    val protocolVersion: Int,
    val version: String,
    val onlinePlayers: Int,
    val maxPlayers: Int,
    val serverGUID: String,
    val subTitle: String,
    val gameMode: String,
    val mapId: Int,
    val externalPort: Int,
    val internalPort: Int,
    override val rawResponse: String,
    override var latency: Long
) : Response