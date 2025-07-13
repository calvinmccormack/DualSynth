#ifndef DUALSYNTH_AUDIOPLAYER_H
#define DUALSYNTH_AUDIOPLAYER_H
#define M_PI 3.14159265358979323846

#include <AudioSamplePlayerListener.h>
#include <AudioSamplePlayer.h>

class AudioPlayer : dsj::AudioSamplePlayerListener
{
public:

    AudioPlayer();
    ~AudioPlayer() = default;

    void prepareToPlay(int samplesPerBlock, double sampleRate) override;
    void releaseResources() override;
    float* getNextAudioSamples(int nombreSamples, int nombreCanaux) override;

    void play(double frequency);
    void stop();

private:

    dsj::AudioSamplePlayer audioSamplePlayer;
    double deviceSampleRate = 0;
    bool isPlayerActive = false;
    double soundFrequencyHz = 0;
    double lastAngle = 0;

};


#endif //DUALSYNTH_AUDIOPLAYER_H
