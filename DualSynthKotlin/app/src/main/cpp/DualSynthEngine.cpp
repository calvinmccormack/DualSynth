//
// Created by Calvin McCormack on 7/11/25.
//

#include "DualSynthEngine.h"
#include <android/log.h>

#define LOG_TAG "DualSynthEngine"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)

void DualSynthEngine::setFilterCutoff(float cutoff) {
    currentCutoff = cutoff;
    LOGI("Filter cutoff set to: %f", cutoff);
}

void DualSynthEngine::triggerSample1() {
    LOGI("Sample 1 triggered");
}