//
// Created by Calvin McCormack on 7/11/25.
//

#pragma once

class DualSynthEngine {
public:
    void setFilterCutoff(float cutoff);
    void triggerSample1();

private:
    float currentCutoff = 1000.0f;
};