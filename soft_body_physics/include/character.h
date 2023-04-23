#ifndef CHARACTER_H
#define CHARACTER_H

#include <vector>

#include <box2d/box2d.h>

#include "particle.h"
#include "spring.h"

// A class to store the squish character, which will be made of many springs and particles
// It will need to have a custom draw method, and the ability to move around specific nodes

class Character {
    private:
        std::vector<Particle*> particles;
        std::vector<Spring*> springs;

    public:
        // Constructor takes reference to the world for creation of bodies
        Character(b2World* world);

        void draw();
        void drawConvexPolygon(int vertices[], int size); // Helper method

        // For dragging the character around
        void lockParticle(int nodeID, bool locked);
        void changeParticlePosition(int nodeID, float x, float y);
};

#endif
