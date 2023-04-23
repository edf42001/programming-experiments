#include "spring.h"

#include "box2d_drawing_utils.cpp"

// Constructor takes in two particles and reference to the world for creation
Spring::Spring(Particle* particle1, Particle* particle2, b2World* world) {
    this->particle1 = particle1;
    this->particle2 = particle2;

    // Create a distance joint between the two bodies to be a spring
    b2DistanceJointDef jointDef;
    jointDef.bodyA = particle1->getBody();
    jointDef.bodyB = particle2->getBody();
    jointDef.stiffness = 20;
    jointDef.damping = 0.1;

    // We set the rest length to the initial length between the particles
    b2Vec2 point1 = this->particle1->getBody()->GetPosition();
    b2Vec2 point2 = this->particle2->getBody()->GetPosition();
    jointDef.length = std::sqrt((point1.x - point2.x) * (point1.x - point2.x) + (point1.y - point2.y) * (point1.y - point2.y));

    world->CreateJoint(&jointDef);
};

void Spring::draw() {
    b2Vec2 point1 = this->particle1->getBody()->GetPosition();
    b2Vec2 point2 = this->particle2->getBody()->GetPosition();

    glColor3f(1.0f, 0.0f, 0.0f); // Set spring color to red
    drawLine(point1.x, point1.y, point2.x, point2.y);
}
