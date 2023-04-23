cmake_minimum_required(VERSION 3.10)
project(physicsSim)

include_directories(include)

ADD_LIBRARY(MyCode src/box.cpp src/character.cpp src/particle.cpp src/spring.cpp src/box2d_drawing_utils.cpp)

add_executable(physicsSim physics_sim.cpp)
target_link_libraries(physicsSim box2d GL glut)
target_link_libraries(physicsSim MyPhysics)
