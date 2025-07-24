package com.calvinmccormack.dualsynthpd

import android.util.Log
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.nio.ByteBuffer
import java.nio.charset.Charset

object OscSender {

    fun send(address: String, value: Float) {
        try {
            val ip = InetAddress.getByName(InteractionRouter.oscTargetIp)
            val port = InteractionRouter.oscTargetPort

            val data = buildOscPacket("/$address", value)
            val packet = DatagramPacket(data, data.size, ip, port)
            DatagramSocket().use { it.send(packet) }

        } catch (e: Exception) {
            Log.e("OscSender", "Error sending OSC message: ${e.message}")
        }
    }

    private fun buildOscPacket(address: String, value: Float): ByteArray {
        val charset = Charset.forName("US-ASCII")
        val addressBytes = padOscString(address, charset)
        val typeTagBytes = padOscString(",f", charset)  // Single float argument
        val valueBytes = ByteBuffer.allocate(4).putFloat(value).array()

        return addressBytes + typeTagBytes + valueBytes
    }

    private fun padOscString(s: String, charset: Charset): ByteArray {
        val raw = s.toByteArray(charset)
        val padding = (4 - (raw.size % 4)) % 4
        return raw + ByteArray(padding)
    }
}