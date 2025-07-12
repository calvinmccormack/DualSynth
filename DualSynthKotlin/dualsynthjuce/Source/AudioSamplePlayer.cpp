/*
  ==============================================================================

    AudioSamplePlayer.cpp
    Created: 12 Jul 2025 9:42:59pm
    Author:  Calvin McCormack

  ==============================================================================
*/

#include "AudioSamplePlayer.h"

namespace dsj
{
    AudioSamplePlayer::AudioSamplePlayer()
    {
        player = std::make_unique<juce::AudioSourcePlayer>();
        player->setSource(this);
        deviceManager.addAudioCallback(player.get());
    }
    
    void AudioSamplePlayer::setListener(AudioSamplePlayerListener *listener)
    {
        this->listener = listener;
    }
    
    void AudioSamplePlayer::setupPlayer()
    {
        deviceManager.initialiseWithDefaultDevices(0, 2);
    }
    
    void AudioSamplePlayer::closePlayer()
    {
        deviceManager.closeAudioDevice();
    }
    
    // override
    void AudioSamplePlayer::prepareToPlay(int samplesPerBlock, double sampleRate)
    {
        if (listener != nullptr)
        {
            listener->prepareToPlay(samplesPerBlock, sampleRate);
        }
    }
    
    // override
    void AudioSamplePlayer::releaseResources()
    {
        if (listener != nullptr)
        {
            listener->releaseResources();
        }
    }
    
    // override
    void AudioSamplePlayer::getNextAudioBlock(const juce::AudioSourceChannelInfo& bufferToFill)
    {
        if (listener == nullptr) return;
        int outputChannelsNumber = bufferToFill.buffer->getNumChannels();
        
        // get the audio samples to redirect to the audio output (numSamples represents the total number of frames needed to be played):
        auto* samplesInline = listener->getNextAudioSamples(bufferToFill.numSamples, outputChannelsNumber);
        
        // map the block of audio frames stored in samplesInline to the audio output:
        for (auto channel = 0 ; channel < outputChannelsNumber ; channel++)
        {
            // get a pointer to the start sample in the buffer for this audio output channel :
            auto* buffer = bufferToFill.buffer->getWritePointer(channel, bufferToFill.startSample);
            
            // fill the required number of samples :
            for (auto a = 0 ; a < bufferToFill.numSamples ; a++)
            {
                buffer[a] = samplesInline[a * outputChannelsNumber + channel];
            }
        }
    }
}