#include <jni.h>
#include <string>
#include <JuceHeader.h>
#include <android/log.h>
#include "AudioPlayer.h"

extern "C" JNIEXPORT jlong JNICALL Java_com_calvinmccormack_dualsynth_NativeManager_createPlayer(
        JNIEnv __unused *env, jclass)
{
    auto *audioPlayer = new(std::nothrow) AudioPlayer();
    auto audioPlayerHandle = reinterpret_cast<jlong>(audioPlayer);
    return audioPlayerHandle;
}

extern "C" JNIEXPORT void JNICALL Java_com_calvinmccormack_dualsynth_NativeManager_deletePlayer(
        JNIEnv __unused *env, jclass, jlong audioPlayerHandle)
{
    delete reinterpret_cast<AudioPlayer *>(audioPlayerHandle);
}

extern "C" JNIEXPORT void JNICALL Java_com_calvinmccormack_dualsynth_NativeManager_nativePlay(
        JNIEnv __unused *env, jclass, jlong audioPlayerHandle, jdouble frequency)
{
    auto *audioPlayer = reinterpret_cast<AudioPlayer *>(audioPlayerHandle);
    if (audioPlayer == nullptr)
    {
        __android_log_print(ANDROID_LOG_ERROR, "TAG", "Invalid audio player handle");
        return;
    }
    audioPlayer->play(frequency);
}

extern "C" JNIEXPORT void JNICALL Java_com_calvinmccormack_dualsynth_NativeManager_nativeStop(
        JNIEnv __unused *env, jclass, jlong audioPlayerHandle)
{
    auto *audioPlayer = reinterpret_cast<AudioPlayer *>(audioPlayerHandle);
    if (audioPlayer == nullptr)
    {
        __android_log_print(ANDROID_LOG_ERROR, "TAG", "Invalid audio player handle");
        return;
    }
    audioPlayer->stop();
}