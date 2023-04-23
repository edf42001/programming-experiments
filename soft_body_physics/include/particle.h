#ifndef PARTICLE_H
#define PARTICLE_H

#include <box2d/box2d.h>

// A particle. This particle can be connected to others by springs. I model the particle as a small circle
// Constructor will take in initial x and initial y
// Draw method will draw a circle at the particle's location, though eventualy may want to hide each particle

class Particle {
    private:
        // Stores the box2d pyhsics object
        b2Body* body;

    public:
        // Constructor takes in initial x, y, and reference to the world for creation
        Particle(float initialX, float initialY, b2World* world);

        void draw();

        // Getters and setters
        b2Body* getBody();
};

#endif
