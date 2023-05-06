#ifndef TILE_CPP
#define TILE_CPP

#include <vector>

#include <opencv2/opencv.hpp>

using namespace cv;

class Tile {
    public:
        Mat image;
        std::vector<String> edgeTypes;
        std::vector<std::vector<int>> connectionMap;

    public:
        Tile(Mat image, std::vector<String> edgeTypes);

        Tile rotate(int amount);

        void analyzeTileConnections(std::vector<Tile> &tiles);
};

Tile::Tile(Mat image, std::vector<String> edgeTypes) {
    this->image = image;
    this->edgeTypes = edgeTypes;
}

// Rotates 90 degrees clockwise times amount
Tile Tile::rotate(int amount) {
    cv::Mat newImage;

    // Rotate the image
    cv::rotate(image, newImage, amount - 1); // Number of 90 degree rotations can be encoded as an int

    // Rotate the edge types (shift array)
    std::vector<String> newEdgeTypes;
    for (int i = 0; i < edgeTypes.size(); i++) {
        // Calculated shifted index with mod (add edgeTypes.size() so it is positive, % can return negative values)
        int shiftedIndex = (i - amount + edgeTypes.size()) % edgeTypes.size();
        newEdgeTypes.push_back(edgeTypes.at(shiftedIndex));
    }

    return Tile(newImage, newEdgeTypes);
}

// Determines which tiles this tile can connect to in which directions
void Tile::analyzeTileConnections(std::vector<Tile> &tiles) {
    // Direcion goes from 0-3 (up, right, down, left)
    for (int d = 0; d < 4; d++) {
        std::vector<int> validTiles;
        for (int t = 0; t < tiles.size(); t++) {
            // Because some tiles are assymetrical and edges are described in the clockwise direction,
            // need to reverse the string to check alignment
            String otherEdgeString = (tiles.at(t).edgeTypes.at((d + 2) % 4));
            std::reverse(otherEdgeString.begin(), otherEdgeString.end());
            if (edgeTypes.at(d) == otherEdgeString) {
                validTiles.push_back(t);
            }
        }

        this->connectionMap.push_back(validTiles);
    }
}

#endif
