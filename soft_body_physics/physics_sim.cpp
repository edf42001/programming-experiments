#include <box2d/box2d.h>
#include <GL/glut.h>
#include <iostream>
#include <vector>

#include "box.h"
#include "particle.h"
#include "spring.h"
#include "character.h"
#include "src/box2d_drawing_utils.cpp"

Box* box1;
Box* box2;
Character* character;

// Define the gravity vector
b2Vec2 gravity(0.0f, -10.0f);

// Construct a world object
b2World world(gravity);

void renderWorld() {
    glClear(GL_COLOR_BUFFER_BIT);

    character->draw();

    box1->draw();
    box2->draw();

    glFlush();

    // Advance time by 1/30th of a second
    world.Step(1.0f / 30.0f, 6, 2);
}

void reshape(int width, int height) {
    glViewport(0, 0, width, height);
    glMatrixMode(GL_PROJECTION);
    glLoadIdentity();
    glOrtho(-width / 2, width / 2, 0, height, -1, 1);
    glMatrixMode(GL_MODELVIEW);
}

void timer(int value) {
    glutPostRedisplay();
    glutTimerFunc(1000 / 30, timer, 0); // 30 fps
}

void convertPixelsToCoords(float &x, float &y) {
    // Modifies the variables in place by reference

    // TODO: get these values automatically
    // These are coordiantes in Box2D world and OpenGL. We want to convert pixels to these
    float left = -5;
    float right = 5;
    float bottom = 0;
    float top = 10;

    float widthPx = 300;
    float heightPx = 300;

    x = (x / widthPx) * (right - left) + left;
    y = (1 - y / heightPx) * (top - bottom) + bottom;  // Need to negate y axis with `1 - `
}

void handleMouseDrag(int xPx, int yPx) {
    float x = xPx;
    float y = yPx;
    convertPixelsToCoords(x, y);

    // Move the frozen particle
    character->changeParticlePosition(3, x, y);
}

void handleMousePress(int button, int state, int xPx, int yPx) {
    if (button == GLUT_LEFT_BUTTON && state == GLUT_DOWN) {

        float x = xPx;
        float y = yPx;
        convertPixelsToCoords(x, y);

        // Freeze the particle, and move it to the clicked location
        character->changeParticlePosition(3, x, y);
        character->lockParticle(3, true);

    } else if (button == GLUT_LEFT_BUTTON && state == GLUT_UP) {
        // Unfreeze the particle
        character->lockParticle(3, false);
    }
}

void createBoundaries() {
    // Set up two wall boundaries and a ground and ceiling. My world boundaries are -5 to 5 on x, 0 to 10 on y
    b2BodyDef boundaryBodyDef;
    boundaryBodyDef.position.Set(0.0f, -10.0f);
    b2Body* boundaryBody = world.CreateBody(&boundaryBodyDef); // Create the ground body
    b2PolygonShape boundaryBox; // Define the ground shape
    boundaryBox.SetAsBox(50.0f, 10.0f);
    boundaryBody->CreateFixture(&boundaryBox, 0.0f); // Add the ground fixture to the ground body

    // We can reuse these variables
    boundaryBodyDef.position.Set(-10.0f, -10.0f);
    boundaryBody = world.CreateBody(&boundaryBodyDef);
    boundaryBox.SetAsBox(5.0f, 50.0f);
    boundaryBody->CreateFixture(&boundaryBox, 0.0f);

    boundaryBodyDef.position.Set(10.0f, -10.0f);
    boundaryBody = world.CreateBody(&boundaryBodyDef);
    boundaryBox.SetAsBox(5.0f, 50.0f);
    boundaryBody->CreateFixture(&boundaryBox, 0.0f);

    boundaryBodyDef.position.Set(0.0f, 20.0f);
    boundaryBody = world.CreateBody(&boundaryBodyDef);
    boundaryBox.SetAsBox(50.0f, 10.0f);
    boundaryBody->CreateFixture(&boundaryBox, 0.0f);
}

void createPhysicsWorld() {
    createBoundaries();

    // Create my physics objects
    box1 = new Box(0.0f, 4.0f, 2.0f, 2.0f, &world);
    box2 = new Box(0.8f, 10.0f, 2.0f, 2.0f, &world);

    character = new Character(&world);
};


int main(int argc, char **argv) {
    createPhysicsWorld();

    glutInit(&argc, argv);
    glutInitDisplayMode(GLUT_SINGLE | GLUT_RGB);
    glutCreateWindow("OpenGL Window");
    glutInitWindowSize(640, 640);

    // Set up the projection matrix
    reshape(10, 10);

    glutDisplayFunc(renderWorld);
    glutTimerFunc(0, timer, 0);
    glutMouseFunc(handleMousePress);
    glutMotionFunc(handleMouseDrag);
    glutMainLoop();

    return 0;
}
