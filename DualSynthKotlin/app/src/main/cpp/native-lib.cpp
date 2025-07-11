#include <jni.h>
#include <string>
#include "DualSynthEngine.h"

static DualSynthEngine engine;

extern "C" JNIEXPORT jstring JNICALL
Java_com_calvinmccormack_dualsynth_MainActivity_stringFromJNI(
        JNIEnv* env,
        jobject /* this */) {
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}