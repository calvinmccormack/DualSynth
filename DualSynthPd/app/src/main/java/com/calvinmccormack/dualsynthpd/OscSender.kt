package com.calvinmccormack.dualsynthpd

import android.util.Log
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.nio.ByteBuffer
import java.nio.charset.Charset

object OscSender {

    fun send(address: String, value: Float) {
        Thread{
            try {
                val ip = InetAddress.getByName(targetHost)
                val port = targetPort

                val data = buildOscPacket("/$address", value)
                val packet = DatagramPacket(data, data.size, ip, port)
                DatagramSocket().use { it.send(packet) }

            } catch (e: Exception) {
                Log.e("OscSender", "Error sending OSC message", e)
            }
        }.start()
    }

    @Volatile private var targetHost: String = "127.0.0.1"
    @Volatile private var targetPort: Int = 9000

    fun configure(host: String, port: Int) {
        targetHost = host
        targetPort = port
    }


    private fun buildOscPacket(address: String, value: Float): ByteArray {
        val charset = Charset.forName("US-ASCII")
        val addressBytes = padOscString(address, charset)
        val typeTagBytes = padOscString(",f", charset)  // Single float argument
        val valueBytes = ByteBuffer.allocate(4).putFloat(value).array()

        return addressBytes + typeTagBytes + valueBytes
    }

    private fun padOscString(s: String, charset: Charset): ByteArray {
        val raw = s.toByteArray(charset) + 0
        val padding = (4 - (raw.size % 4)) % 4
        return raw + ByteArray(padding)
    }
}