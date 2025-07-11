/*
  ==============================================================================

    AudioSamplePlayer.h
    Created: 12 Jul 2025 9:42:44pm
    Author:  Calvin McCormack

  ==============================================================================
*/

#pragma once

#include "JuceHeader.h"
#include "AudioSamplePlayerListener.h"

namespace dsj
{
    class AudioSamplePlayer : public juce::AudioSource
    {
    public:
        AudioSamplePlayer();
        void setListener(AudioSamplePlayerListener *listener);
        void setupPlayer();
        void closePlayer();
        void prepareToPlay(int samplesPerBlock, double sampleRate) override;
        void releaseResources() override;
        void getNextAudioBlock(const juce::AudioSourceChannelInfo& bufferToFill) override;
    private:
        AudioSamplePlayerListener *listener = nullptr;
        std::unique_ptr<juce::AudioSourcePlayer> player;
        juce::AudioDeviceManager deviceManager;
        JUCE_DECLARE_NON_COPYABLE_WITH_LEAK_DETECTOR(AudioSamplePlayer)
    };
}