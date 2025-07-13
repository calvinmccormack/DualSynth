cmake_minimum_required(VERSION 3.22.1)
project(dualsynth)

# optimization
set(CMAKE_CXX_FLAGS_DEBUG "${CMAKE_CXX_FLAGS_DEBUG} -Ofast")

# JUCE directory
set(JUCE_DIR "$ENV{HOME}/JUCE")

# Oboe
set(OBOE_DIR "${JUCE_DIR}/modules/juce_audio_devices/native/oboe")
add_subdirectory(${OBOE_DIR} ./oboe)

# CPU Features
add_library("cpufeatures" STATIC "${ANDROID_NDK}/sources/android/cpufeatures/cpu-features.c")

# JUCE
set(JuceAudio_DIR ${CMAKE_SOURCE_DIR}/dualsynthjuce)
add_library(JuceAudio STATIC IMPORTED)
set_property(TARGET JuceAudio PROPERTY IMPORTED_LOCATION
        ${JuceAudio_DIR}/lib/${ANDROID_ABI}/libdualsynthjuce.a)
set_target_properties(JuceAudio PROPERTIES INTERFACE_INCLUDE_DIRECTORIES
        ${CMAKE_SOURCE_DIR}/dualsynthjuce/lib/include)
include_directories(${CMAKE_SOURCE_DIR}/dualsynthjuce/lib/include)
include_directories(${JUCE_DIR}/modules)

set(SOURCE_FILES
        native-lib.cpp
        AudioPlayer.cpp
        AudioSamplePlayer.cpp
)

add_library( # Sets the name of the library.
        native-lib

        # Sets the library as a shared library.
        SHARED

        # Provides a relative path to your source file(s).
        ${CMAKE_SOURCE_DIR}/native-lib.cpp


        ${CMAKE_SOURCE_DIR}/AudioPlayer.cpp

)

find_library(
        log-lib

        log)

target_link_libraries(
        native-lib

        JuceAudio

        "cpufeatures"
        "oboe"

        ${log-lib})

target_include_directories(native-lib
        PRIVATE
        ${CMAKE_CURRENT_SOURCE_DIR})