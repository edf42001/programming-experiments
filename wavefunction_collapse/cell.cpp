#ifndef CELL_CPP
#define CELL_CPP

#include <vector>
#include <numeric>

class Cell {
    public:
        bool collapsed; // Has this cell been collapsed to one option yet
        std::vector<int> options; // Which cells can this possibly be?
        int index; // The index of this cell in the grid

        Cell(int index, int numTileTypes);
};

Cell::Cell(int index, int numTileTypes) {
    this->collapsed = false;
    this->index = index;

    // Create a vector with 0-n where n is the number of different tiles
    this->options = std::vector<int>(numTileTypes);
    std::iota(this->options.begin(), this->options.end(), 0);
}

#endif
