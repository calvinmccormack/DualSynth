/*
  ==============================================================================

    AudioSamplePlayerListener.h
    Created: 12 Jul 2025 9:40:51pm
    Author:  Calvin McCormack

  ==============================================================================
*/

#pragma once

namespace dsj // dualSynthJuce
{
    class AudioSamplePlayerListener
    {
        protected:
            AudioSamplePlayerListener() = default;
            ~AudioSamplePlayerListener() = default;
        public:
            virtual void prepareToPlay(int samplesPerBlock, double sampleRate) = 0;
            virtual void releaseResources() = 0;
            virtual float* getNextAudioSamples(int numberOfFrames, int numberOfOutputChannels) = 0;
    };        
}