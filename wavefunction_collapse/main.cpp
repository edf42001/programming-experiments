#include <iostream>
#include <ranges>

#include <opencv2/opencv.hpp>

#include "tile.cpp"
#include "cell.cpp"

using namespace cv;

// Store tiles and tile images. Some tiles are rotations of others
std::vector<Mat> tileImages;
std::vector<Tile> tiles;

// Define the parameters of the grid and tiles
const int GRID_ROWS = 25; // Squares
const int GRID_COLS = 25; // Squares
const int TILE_SIZE = 56; // Pixels
const std::size_t NUM_GRID_CELLS = GRID_ROWS * GRID_COLS; // For initializing my array

// The grid, represented as a 1D array
std::vector<Cell> grid;

// The main image canvas. Fill with a light grey color for if there is no tile
Mat image(GRID_ROWS * TILE_SIZE, GRID_COLS * TILE_SIZE, CV_8UC3, cv::Scalar(200, 200, 200));

// Map describing which tiles can go next to each other. For each tile, each direction stores an array of tiles.
enum class Direction {UP, RIGHT, DOWN, LEFT};

// Goes through the grid and finds the lowest entropy tiles (currently measured by length of options)
// Randomly chooses one and returns the index
int pickTileToCollapse() {
    // Filter out cells that are collapsed and find the minimum entropy
    auto nonCollapsed = std::views::filter(grid, [](const Cell &a) {return !a.collapsed;});
    auto minElement = std::min_element(nonCollapsed.begin(), nonCollapsed.end(), [](const Cell &a, const Cell & b) {return a.options.size() < b.options.size();});
    int min = minElement->options.size();

    // Select all elements to with that min entropy, copy into a vector, and choose one at random.
    // We can't choose directly from the view because filtering no longer allows for random access or knowledge of the size
    auto gridElementsToChoseView = std::views::filter(nonCollapsed, [min](const Cell &a) {return a.options.size() == min;});
    std::vector<Cell*> gridElementsToChose;

    for (Cell &c : gridElementsToChoseView) {
        gridElementsToChose.push_back(&c);
    }

    // Return an index at random.
    return gridElementsToChose.at(rand() % gridElementsToChose.size())->index;
}

// Given the index of a tile, update its options based on the options of its 4 neighbords
std::vector<int> updateTileOptions(int tileIndex) {
    // TODO: This code is very similiar to the below, is there a way to combine them?
    std::map<Direction, std::pair<int, int>> directionOffsets = {
        {Direction::UP, {0, -1}},
        {Direction::DOWN, {0, 1}},
        {Direction::LEFT, {-1, 0}},
        {Direction::RIGHT, {1, 0}},
    };

    int x = tileIndex % GRID_COLS;
    int y = tileIndex / GRID_COLS;

    // The current options in this cell
    std::vector<int> options = grid.at(tileIndex).options;

    // For each of its 4 neighbors
    for (auto &offset : directionOffsets) {
        Direction d = offset.first;
        int newX = x + offset.second.first;
        int newY = y + offset.second.second;

        // Make sure the cell exists
        bool inRange = 0 <= newX && newX < GRID_COLS && 0 <= newY && newY < GRID_ROWS;
        if (!inRange) {
            continue;
        }

        int index = newY * GRID_COLS + newX;
        Cell &adjacentCell = grid.at(index);

        // For each of the adjacent cell's options, update our options based on the direction and possible connections
        std::vector<int> newPossibilites; // All possible valid new options.
        for (auto option : adjacentCell.options) {
            // Add two to direction and modulus to get the oppostie direction for the connection leading to this cell
            int connectionDirection = (static_cast<int>(d) + 2) % 4;
            std::vector<int> &possibilities = tiles.at(option).connectionMap.at(connectionDirection);
            for (auto t : possibilities) {
                // If not already added (use a set for this)
                if(std::find(newPossibilites.begin(), newPossibilites.end(), t) == newPossibilites.end()) {
                    newPossibilites.push_back(t);
                }
            }
        }

        // Now that we've collected all possibly options, keep only those that are in both
        std::vector<int> newOptions;
        for (int t : newPossibilites) {
            if(std::find(options.begin(), options.end(), t) != options.end()) {
                newOptions.push_back(t);
            }
        }

        // Commit these changes by setting options to newOptions
        options = newOptions;
    }

    return options;
}

// Given the index of a tile that was just collapsed, update the available options for the neighboring tiles
void updateNeighboringTileOptions(int tileIndex) {
    std::map<Direction, std::pair<int, int>> directionOffsets = {
        {Direction::UP, {0, -1}},
        {Direction::DOWN, {0, 1}},
        {Direction::LEFT, {-1, 0}},
        {Direction::RIGHT, {1, 0}},
    };

    int x = tileIndex % GRID_COLS;
    int y = tileIndex / GRID_COLS;

    // Update the cell in each adjacent direction
    for (auto &offset : directionOffsets) {
        Direction d = offset.first;
        int newX = x + offset.second.first;
        int newY = y + offset.second.second;

        // Make sure the cell is on the grid
        bool inRange = 0 <= newX && newX < GRID_COLS && 0 <= newY && newY < GRID_ROWS;
        if (!inRange) {
            continue;
        }

        int index = newY * GRID_COLS + newX;

        // Do another loop to look at the 4 cells surrounding that cell
        grid.at(index).options = updateTileOptions(index);
    }
}

// Loads the images from disk into the array
void loadOriginalTiles() {
    for (int i = 0; i < 3; i++) {
        String fileName = "tiles/original/" + std::to_string(i) + ".png";
        tileImages.push_back(imread(fileName, IMREAD_COLOR));
    }

    Tile blank = Tile(tileImages.at(0), {"AAA", "AAA", "AAA", "AAA"});
    Tile up = Tile(tileImages.at(1), {"BBB", "BBB", "AAA", "BBB"});

    tiles.push_back(up);
    tiles.push_back(up.rotate(1));
    tiles.push_back(up.rotate(2));
    tiles.push_back(up.rotate(3));
    tiles.push_back(blank);
}

void loadCircuitTiles() {
    for (int i = 0; i < 13; i++) {
        String fileName = "tiles/circuit/" + std::to_string(i) + ".png";
        tileImages.push_back(imread(fileName, IMREAD_COLOR));
    }

    Tile black = Tile(tileImages.at(0), {"AAA", "AAA", "AAA", "AAA"});
    Tile green = Tile(tileImages.at(1), {"BBB", "BBB", "BBB", "BBB"});
    Tile oneKnob = Tile(tileImages.at(2), {"BBB", "BCB", "BBB", "BBB"});
    Tile greyLine = Tile(tileImages.at(3), {"BBB", "BDB", "BBB", "BDB"});
    Tile endConnector = Tile(tileImages.at(4), {"ABB", "BCB", "BBA", "AAA"});
    Tile corner = Tile(tileImages.at(5), {"ABB", "BBB", "BBB", "BBA"});
    Tile greenLine = Tile(tileImages.at(6), {"BBB", "BCB", "BBB", "BCB"});
    Tile cross = Tile(tileImages.at(7), {"BDB", "BCB", "BDB", "BCB"});
    Tile greyGreenKnob = Tile(tileImages.at(8), {"BDB", "BBB", "BCB", "BBB"});
    Tile tShape = Tile(tileImages.at(9), {"BCB", "BCB", "BBB", "BCB"});
    Tile diagonal = Tile(tileImages.at(10), {"BCB", "BCB", "BCB", "BCB"});
    Tile singleDiagonal = Tile(tileImages.at(11), {"BCB", "BCB", "BBB", "BBB"});
    Tile twoWayKnob = Tile(tileImages.at(12), {"BBB", "BCB", "BBB", "BCB"});

    tiles.push_back(black);
    tiles.push_back(green);
    tiles.push_back(oneKnob);
    tiles.push_back(greyLine);
    tiles.push_back(endConnector);
    tiles.push_back(corner);
    tiles.push_back(greenLine);
    tiles.push_back(cross);
    tiles.push_back(greyGreenKnob);
    tiles.push_back(tShape);
    tiles.push_back(diagonal);
    tiles.push_back(singleDiagonal);
    tiles.push_back(twoWayKnob);

    tiles.push_back(oneKnob.rotate(1));
    tiles.push_back(oneKnob.rotate(2));
    tiles.push_back(oneKnob.rotate(3));

    tiles.push_back(greyLine.rotate(1));

    tiles.push_back(greenLine.rotate(1));

    tiles.push_back(cross.rotate(1));

    tiles.push_back(greyGreenKnob.rotate(1));
    tiles.push_back(greyGreenKnob.rotate(2));
    tiles.push_back(greyGreenKnob.rotate(3));

    tiles.push_back(tShape.rotate(1));
    tiles.push_back(tShape.rotate(2));
    tiles.push_back(tShape.rotate(3));

    tiles.push_back(diagonal.rotate(1));

    tiles.push_back(singleDiagonal.rotate(1));
    tiles.push_back(singleDiagonal.rotate(2));
    tiles.push_back(singleDiagonal.rotate(3));

    tiles.push_back(twoWayKnob.rotate(3));

    tiles.push_back(endConnector.rotate(1));
    tiles.push_back(endConnector.rotate(2));
    tiles.push_back(endConnector.rotate(3));

    tiles.push_back(corner.rotate(1));
    tiles.push_back(corner.rotate(2));
    tiles.push_back(corner.rotate(3));

}

// Copy a smaller image into a part of a larger image
void copyImageTo(Mat &src, Mat &target, int left, int top) {
    src.copyTo(target(Rect(left, top, src.cols, src.rows)));
}

// Draws cell at specific index
void updateCanvasAtIndex(int index) {
    Cell cell = grid.at(index);
    int x = index % GRID_COLS;
    int y = index / GRID_COLS;
    copyImageTo(tiles.at(cell.options.at(0)).image, image, x * TILE_SIZE, y * TILE_SIZE);
}

void reset() {
    // Initialize each cell to be any possibility
    grid.clear();
    for (int i = 0; i < NUM_GRID_CELLS; i++) {
        grid.push_back(Cell(i, tiles.size()));
    }
}

int main() {
    // TODO: upgrade to c++ 17 and use filter for finding the smallest thing (or view?)
    srand(5); // Seed random number generator

    loadCircuitTiles();

    // Generate connections for all tiles
    for (auto &tile : tiles) {
        tile.analyzeTileConnections(tiles);
    }

    reset(); // Initialize grid

    // Main loop
    namedWindow("Display window", WINDOW_NORMAL);
    resizeWindow("Display window", 500, 500);  // Resize window and trigger resizing image to fit window

    int placedTileCounter = 0;  // Gets reset on failure to find solution
    int totalIterations = 0;  // Always increments

    auto startTime = std::chrono::system_clock::now();

    while (placedTileCounter < NUM_GRID_CELLS) {
        placedTileCounter++;
        totalIterations++;

        int tileToCollapse = pickTileToCollapse();
        std::cout << "Chose tile " << tileToCollapse << std::endl;

        // Choose an option at random to collapse to
        Cell &tile = grid.at(tileToCollapse);

        if (tile.options.size() == 0) {
            // Reset grid cells and counter and clear the drawing canvas
            std::cout << "Tile with no options found, retrying" << std::endl;
            reset();
            placedTileCounter = 0;
            image = Mat(GRID_ROWS * TILE_SIZE, GRID_COLS * TILE_SIZE, CV_8UC3, cv::Scalar(200, 200, 200));
            continue;
        }

        tile.collapsed = true;
        tile.options = {tile.options.at(rand() % tile.options.size())};

        updateNeighboringTileOptions(tileToCollapse);

        updateCanvasAtIndex(tileToCollapse);

        imshow("Display window", image);
        waitKey(1);
    }

    auto endTime = std::chrono::system_clock::now();
    double elapsedTime = 1e-6 * std::chrono::duration_cast<std::chrono::microseconds>(endTime - startTime).count();
    std::cout << "Elapsed time: " << std::setprecision(4) << elapsedTime << std::endl;
    std::cout << "Iterations: " << totalIterations << std::endl;
    std::cout << "Iterations/sec: " << (totalIterations / elapsedTime) << std::endl;

    waitKey(0);

    return 0;
}
