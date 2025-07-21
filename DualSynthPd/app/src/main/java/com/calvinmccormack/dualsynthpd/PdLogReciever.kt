package com.calvinmccormack.dualsynthpd

import android.util.Log
import org.puredata.core.PdReceiver

class PdLogReceiver : PdReceiver {
    override fun print(s: String) {
        Log.d("PdPrint", s)
    }

    override fun receiveBang(source: String?) {
        Log.d("PdReceiver", "Bang received from $source")
    }

    override fun receiveFloat(source: String?, value: Float) {
        Log.d("PdReceiver", "Float from $source: $value")
    }

    override fun receiveSymbol(source: String?, symbol: String?) {
        Log.d("PdReceiver", "Symbol from $source: $symbol")
    }

    override fun receiveList(source: String?, vararg args: Any?) {
        Log.d("PdReceiver", "List from $source: ${args.joinToString()}")
    }

    override fun receiveMessage(source: String?, symbol: String?, vararg args: Any?) {
        Log.d("PdReceiver", "Message from $source: $symbol ${args.joinToString()}")
    }
}