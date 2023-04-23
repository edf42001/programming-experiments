#include "particle.h"

#include "box2d_drawing_utils.cpp"

// Constructor takes in initial x, y, and reference to the world for creation
Particle::Particle(float initialX, float initialY, b2World* world) {
    // Define the dynamic body
    b2BodyDef bodyDef;
    bodyDef.type = b2_dynamicBody;
    bodyDef.position.Set(initialX, initialY);

    // Create the dynamic body
    this->body = world->CreateBody(&bodyDef);

    // Define the dynamic body shape
    b2CircleShape circleShape;
    circleShape.m_radius = 0.2f; // A small radius to act as a point mass

    // Add the dynamic fixture to the dynamic body
    b2FixtureDef fixtureDef;
    fixtureDef.shape = &circleShape;
    fixtureDef.density = 1.0f;
    fixtureDef.friction = 0.3f;
    body->CreateFixture(&fixtureDef);
};

void Particle::draw() {
    float radius = ((b2CircleShape*) body->GetFixtureList()->GetShape())->m_radius;
    drawCircleBody(this->body, radius);
}

b2Body* Particle::getBody() {
    return this->body;
}
