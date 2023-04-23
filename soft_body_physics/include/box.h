#ifndef BOX_H
#define BOX_H

#include <box2d/box2d.h>

// A box. Wraps a Box2D ploygon physics object (with four sides)
// methods: draw (will use my helper method for drawing polygons)

class Box {
    private:
        // Stores the box pyhsics object
        b2Body* body;

    public:
        // Constructor takes in initial x, y, width, height, and reference to the world for creation
        Box(float initialX, float initialY, float width, float height, b2World* world);

        void draw();
};

#endif
