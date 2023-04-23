#ifndef BOX2D_DRAWING_UTILS_CPP
#define BOX2D_DRAWING_UTILS_CPP

#include <GL/glut.h>
#include <box2d/box2d.h>

static void drawPolygonBody(b2Body* body) {
    b2Vec2 position = body->GetPosition();
    float angle = body->GetAngle();

    glPushMatrix();
    glTranslatef(position.x, position.y, 0.0f);
    glRotatef(angle * 180.0f / b2_pi, 0.0f, 0.0f, 1.0f);
    glBegin(GL_QUADS);

    b2PolygonShape* bodyShape = (b2PolygonShape*) body->GetFixtureList()->GetShape();
    for (int i = 0; i < bodyShape->m_count; i++) {
        b2Vec2 vertex = bodyShape->m_vertices[i];
        glVertex2f(vertex.x, vertex.y);
    }

    glEnd();
    glPopMatrix();
}

static void drawCircleBody(b2Body* body, float radius) {
    // Assuming you have a b2Body object named body
    b2Vec2 position = body->GetPosition();
    float angle = body->GetAngle();

    // Draw the body as a circle using OpenGL
    glPushMatrix();
    glTranslatef(position.x, position.y, 0.0f);
    glRotatef(angle * 180.0f / b2_pi, 0.0f, 0.0f, 1.0f);
    glBegin(GL_TRIANGLE_FAN);
    glVertex2f(0.0f, 0.0f); // Center of circle
    int segments = 16; // Change this to your desired number of circle segments
    for (int i = 0; i <= segments; i++) {
        float theta = i * 2.0f * b2_pi / segments;
        float x = radius * cosf(theta);
        float y = radius * sinf(theta);
        glVertex2f(x, y);
    }
    glEnd();
    glPopMatrix();
}

static void drawLine(float x1, float y1, float x2, float y2) {
    glBegin(GL_LINES);
    glVertex2f(x1, y1);
    glVertex2f(x2, y2);
    glEnd();
    glLineWidth(3.0f);
}

#endif
