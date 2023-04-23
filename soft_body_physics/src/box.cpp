#include "box.h"

#include "box2d_drawing_utils.cpp"

// Constructor takes in initial x, y, width, height, and reference to the world for creation
Box::Box(float initialX, float initialY, float width, float height, b2World* world) {
    // Define the dynamic body
    b2BodyDef bodyDef;
    bodyDef.type = b2_dynamicBody;
    bodyDef.position.Set(initialX, initialY);

    // Create the dynamic body
    this->body = world->CreateBody(&bodyDef);

    // Define the dynamic body shape
    b2PolygonShape dynamicBox;
    dynamicBox.SetAsBox(width / 2, height / 2); // Uses the half-width

    // Add the dynamic fixture to the dynamic body
    b2FixtureDef fixtureDef;
    fixtureDef.shape = &dynamicBox;
    fixtureDef.density = 1.0f;
    fixtureDef.friction = 0.3f;
    body->CreateFixture(&fixtureDef);
};

void Box::draw() {
    glColor3f(1.0f, 0.0f, 0.0f); // Set box color to red
    drawPolygonBody(this->body);
}
