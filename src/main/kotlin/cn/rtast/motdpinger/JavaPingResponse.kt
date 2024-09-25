/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/9/26
 */


package cn.rtast.motdpinger

data class JavaPingResponse(
    val version: Version,
    val players: Players,
    val favicon: String?,
    val description: Any,
    override var rawResponse: String
) : Response {
    data class Version(
        val protocol: Int,
        val name: String,
    )

    data class Players(
        val online: Int,
        val max: Int,
    )
}