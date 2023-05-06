cmake_minimum_required(VERSION 3.0.0)
project(WaveFunctionCollapse)

set(CMAKE_CXX_STANDARD 20)

find_package(OpenCV REQUIRED)
include_directories(${OpenCV_INCLUDE_DIRS})

# Move the executable to the main directory so it can access the tiles folder
set (CMAKE_RUNTIME_OUTPUT_DIRECTORY ..)

add_executable(WaveFunctionCollapse main.cpp)
target_link_libraries(WaveFunctionCollapse ${OpenCV_LIBS})
