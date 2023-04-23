#include <iostream>

#include "character.h"

#include "box2d_drawing_utils.cpp"

Character::Character(b2World* world) {
    // Define the coordinates of the unicorn in Google drawings coordinates
    float points[][2] = {{2, 2.5}, {3, 2.5}, {3, 3.15}, {4.125, 3.15}, {5.25, 3.15}, {5.25, 4},
                        {4.75, 5.25}, {4.25, 4}, {3.65, 4}, {3.15, 5.25}, {2.65, 4},
                        {2.65, 3.5}, {2, 3.5}, {1.5, 2}, {2.3, 2.8}};

    for (auto &point : points) {
        particles.push_back(new Particle(point[0]-0.75, 9 - point[1], world));
    }

    // Connect each particle to every other particle
    for (int i = 0; i < 15; i++) {
        for (int j = i+1; j < 15; j++) {
            springs.push_back(new Spring(particles.at(i), particles.at(j), world));
        }
    }
}

void Character::drawConvexPolygon(int vertices[], int size) {
    glBegin(GL_POLYGON);
    glColor3f(1.0f, 1.0f, 0.9f); // Set polygon to a nice white
    for (int i = 0; i < size; i++) {
        b2Vec2 point1 = particles.at(vertices[i])->getBody()->GetPosition();
        glVertex2f(point1.x, point1.y);
    }
    glEnd();
}

void Character::draw() {
    drawConvexPolygon(new int[5]{0, 1, 2, 11, 12}, 5);
    drawConvexPolygon(new int[5]{2, 3, 8, 10, 11}, 5);
    drawConvexPolygon(new int[4]{3, 4, 5, 7}, 4);
    drawConvexPolygon(new int[3]{5, 6, 7}, 3);
    drawConvexPolygon(new int[3]{8, 9, 10}, 3);
    drawConvexPolygon(new int[3]{3, 7, 8}, 3);

    // Only draw the springs on the outside of the character
    // Use a hacky minus 2 to exclude the horn
    glColor3f(0.8f, 0.05f, 0.7f); // Set line color to pink
    for (int i = 0; i < particles.size()-2; i++) {
        // Use modulus to connect the last point back to the first
        b2Vec2 point1 = particles.at(i)->getBody()->GetPosition();
        b2Vec2 point2 = particles.at((i+1) % (particles.size()-2))->getBody()->GetPosition();

        drawLine(point1.x, point1.y, point2.x, point2.y);
    }

    b2Vec2 point1 = particles.at(0)->getBody()->GetPosition();
    b2Vec2 point2 = particles.at(13)->getBody()->GetPosition();
    drawLine(point1.x, point1.y, point2.x, point2.y);

    glColor3f(0.1f, 0.5f, 0.9f); // Set iris color to blue
    drawCircleBody(particles.at(14)->getBody(), 0.25);
    glColor3f(0.0f, 0.0f, 0.0f); // Set pupil color to black
    drawCircleBody(particles.at(14)->getBody(), 0.15);
}

void Character::lockParticle(int nodeID, bool locked) {
    this->particles.at(nodeID)->getBody()->SetType(locked ? b2_staticBody : b2_dynamicBody);
}

void Character::changeParticlePosition(int nodeID, float x, float y) {
    this->particles.at(nodeID)->getBody()->SetTransform(b2Vec2(x, y), 0.0);
}
