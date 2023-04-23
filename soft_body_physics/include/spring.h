#ifndef SPRING_H
#define SPRING_H

#include <box2d/box2d.h>

#include "particle.h"

// A spring that connects two particles.
// Constructor will take in the two particles to connect
// Draw method will draw a line connecting the particles

class Spring {
    private:
        // Stores the two connected particles objects
        Particle* particle1;
        Particle* particle2;

    public:
        // Constructor takes in two particles and reference to the world for creation
        Spring(Particle* particle1, Particle* particle2, b2World* world);

        void draw();
};

#endif
