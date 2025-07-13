package com.calvinmccormack.dualsynth

class NativeManager
{
    companion object
    {
        init
        {
            try
            {
                System.loadLibrary("native-lib")
            }
            catch (_: UnsatisfiedLinkError) {}
        }


        @JvmStatic private external fun createPlayer(): Long
        @JvmStatic private external fun deletePlayer(audioPlayerHandle: Long)
        @JvmStatic private external fun nativePlay(audioPlayerHandle: Long, frequency: Double)
        @JvmStatic private external fun nativeStop(audioPlayerHandle: Long)
    }

    private var audioPlayerHandle: Long = 0

    
    fun setup()
    {
        if (audioPlayerHandle == 0L)
        {
            audioPlayerHandle = createPlayer()
        }
    }

    fun dispose()
    {
        if (audioPlayerHandle != 0L)
        {
            deletePlayer(audioPlayerHandle)
        }
        audioPlayerHandle = 0L
    }

    fun play(frequency: Double)
    {
        if (audioPlayerHandle != 0L)
        {
            nativePlay(audioPlayerHandle, frequency)
        }
    }

    fun stop()
    {
        if (audioPlayerHandle != 0L)
        {
            nativeStop(audioPlayerHandle)
        }
    }
}