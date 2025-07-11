#include <jni.h>
#include <string>
#include "DualSynthEngine.h"

static DualSynthEngine engine;

extern "C"
JNIEXPORT void JNICALL
Java_com_calvinmccormack_dualsynth_DspBridge_setFilterCutoff(
        JNIEnv* env,
        jobject /* this */,
        jfloat value) {
    engine.setFilterCutoff(value);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_calvinmccormack_dualsynth_DspBridge_triggerSample1(
        JNIEnv* env,
        jobject /* this */) {
    engine.triggerSample1();
}