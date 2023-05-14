# Soft Body Physics

Inspired by [Coding Challenge 177: Soft Body Physics](https://www.youtube.com/watch?v=IxdGyqhppis), I set out to learn
OpenGL and Box2D with the goal of creating a soft body character. The video tutorial uses JavaScript, but I am using
C++ in order to gain access to OpenGL. 

<image src="../media/soft_body_unicorn.gif" width = 400></image>

The choice of a unicorn for a character is due to my mentoring of [The Fighting Unicorns](https://www.thebluealliance.com/team/2399), a FIRST robotics team.

### Overview

Soft body (jelly-like) physics can be simulated by a collection of points connected by springs.
Add enough points and springs, and the outlines of the character appear to bend and squish as they interact with the environment.

### Libraries 

[Box2D](https://box2d.org/documentation/) is a 2D physics engine designed for video games. It provides common physics
elements like rigid bodies, joints, springs, and forces. [OpenGL](https://www.opengl.org/) is a rendering library designed
for high performance graphics. OpenGL tends to operate close to the bare-metal of the GPU, so I use GLUT
(the OpenGL Utility Toolkit) for features such as handling mouse clicks.
