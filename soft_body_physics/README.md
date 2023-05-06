# Soft Body Physics

In this project I set out to practice C++, and gain experience with OpenGL and Box2D. I was inspired by the video tutorial [Coding Challenge 177: Soft Body Physics](https://www.youtube.com/watch?v=IxdGyqhppis), but I chose to use C++ instead of JavaScript.

Soft body physics are generally simulated with a collection of points connected by springs. Add enough points and springs, and the outlines of the body appear to bend and squish as they interact with their environment.

<image src="../media/soft_body_unicorn.gif" width = 400>

Box2D is a 2D physics engine designed for use in video games. OpenGL is a rendering library designed for high performance graphics. OpenGL tends to operate close to the bare-metal of the GPU, so I use GLUT (the OpenGL Utility Toolkit) for features such as handling mouse clicks.

The choice of a unicorn for a character is due to my mentoring of [The Fighting Unicorns](https://www.thebluealliance.com/team/2399), a FIRST robotics team.
