var canvas = document.getElementById("mainCanvas");
var ctx = canvas.getContext("2d");
const CANVAS_WIDTH = canvas.width;
const CANVAS_HEIGHT = canvas.height;

const RESOLUTION = 5;
const WIDTH = CANVAS_WIDTH / RESOLUTION + 1;
const HEIGHT = CANVAS_HEIGHT / RESOLUTION + 1;

let grid = [...Array(HEIGHT)].map(e => Array(WIDTH));
let points = [...Array(HEIGHT)].map(e => Array(WIDTH));

// Initialize grid with values
init_grid_points()

// Initialize 5 balls
let balls = [...Array(5)].map(e => new Ball(CANVAS_WIDTH, CANVAS_HEIGHT));

setInterval(update, 20);

let start_time = 0;
let end_time = 0;

function update() {
    // Keep track of execution time
    start_time = (new Date()).getMilliseconds();

    // Ball physics update
    for (const ball of balls) {
        ball.update();
    }

    // Update the grid values based on the balls
    update_grid_values();

    // Clear previous frame
    ctx.clearRect(0, 0, CANVAS_WIDTH, CANVAS_HEIGHT);
    
    // Plot grid
    // plot_grid();

    // Plot squares
    plot_marching_squares();

    // Keep track of execution time
    end_time = (new Date()).getMilliseconds();
    // console.log(end_time - start_time);
}

function init_grid_points() {
    for (let i = 0; i < HEIGHT; i++) {
        for (let j = 0; j < WIDTH; j++) {
            points[i][j] = new Vector(j * RESOLUTION, i * RESOLUTION);
        }
    }
}

function update_grid_values() {
    SCALE = 1.0;
    OFFSET = 2.5;
    for (let i = 0; i < HEIGHT; i++) {
        for (let j = 0; j < WIDTH; j++) {
            let sum = 0;
            for (const ball of balls) {
                sum += SCALE * ball.r / (Math.sqrt((j * RESOLUTION - ball.x)**2 + (i * RESOLUTION - ball.y)**2) + 1)
            }
            grid[i][j] = sum - OFFSET;
        }
    }
}

function plot_grid() {
    for (let i = 0; i < HEIGHT; i++) {
        for (let j = 0; j < WIDTH; j++) {
            let value = grid[i][j];
            let point = points[i][j];

            let inside = value > 0;
            // Multiply by >255, anything over gets capped. Nice to see more red/blue than black. 
            // let color = inside ? rgb(value * 500, 0, 0) : rgb(0, 0, -value * 500);
            let color = inside ? "#FF0000" : "#0000FF";

            ctx.strokeStyle = color;
            ctx.fillStyle = color;
            drawCircle(point.x, point.y, 1);
        }
    }
}

function plot_marching_squares() {
    // Calculate each square in the marching square
    ctx.strokeStyle ="#000000";
    for (let i = 0; i < HEIGHT - 1; i++) {
        for (let j = 0; j < WIDTH - 1; j++) {
            let type = get_numeric_square_type(i, j);
            draw_square_lines(i, j, type);
        }
    }
}

function get_numeric_square_type(i, j) {
    let topleft = grid[i][j] > 0 ? 1 : 0;
    let topright = grid[i][j+1] > 0 ? 1 : 0;
    let bottomleft = grid[i+1][j] > 0 ? 1 : 0;
    let bottomright = grid[i+1][j+1] > 0 ? 1 : 0;

    // Return binary number
    return 8 * topleft + 4 * topright + 2 * bottomright + bottomleft;
}

function draw_square_lines(i, j, type) {
    let topleftvalue = grid[i][j];
    let toprightvalue = grid[i][j+1];
    let bottomleftvalue = grid[i+1][j];
    let bottomrightvalue = grid[i+1][j+1];

    let topleft = points[i][j];
    let topright = points[i][j+1];
    let bottomleft = points[i+1][j];
    let bottomright = points[i+1][j+1];

    let a = Vector.lerp(topleft, topright, -topleftvalue / (toprightvalue - topleftvalue));
    let b = Vector.lerp(topright, bottomright, -toprightvalue / (bottomrightvalue - toprightvalue));
    let c = Vector.lerp(bottomright, bottomleft, -bottomrightvalue / (bottomleftvalue - bottomrightvalue));
    let d = Vector.lerp(bottomleft, topleft, -bottomleftvalue / (topleftvalue - bottomleftvalue));

    switch (type) {
        case 0:
            break;
        case 1:
            drawLine(d.x, d.y, c.x, c.y);
            break;
        case 2:
            drawLine(c.x, c.y, b.x, b.y);
            break;
        case 3:
            drawLine(d.x, d.y, b.x, b.y);
            break;
        case 4:
            drawLine(a.x, a.y, b.x, b.y);
            break;
        case 5:
            drawLine(a.x, a.y, b.x, b.y);
            drawLine(d.x, d.y, c.x, c.y);
            break;
        case 6:
            drawLine(a.x, a.y, c.x, c.y);
            break;
        case 7:
            drawLine(d.x, d.y, a.x, a.y);
            break;
        case 8:
            drawLine(d.x, d.y, a.x, a.y);
            break;
        case 9:
            drawLine(a.x, a.y, c.x, c.y);
            break;
        case 10:
            drawLine(d.x, d.y, a.x, a.y);
            drawLine(c.x, c.y, b.x, b.y);
            break;
        case 11:
            drawLine(a.x, a.y, b.x, b.y);
            break;
        case 12:
            drawLine(d.x, d.y, b.x, b.y);
            break;
        case 13:
            drawLine(b.x, b.y, c.x, c.y);
            break;
        case 14:
            drawLine(d.x, d.y, c.x, c.y);
            break;
        case 15:
            break;
    }
}

function drawCircle(x1, y1, r) {
    // Draws a hollow circle
    ctx.beginPath();
    ctx.arc(x1, y1, r, 0, 2 * Math.PI);
    ctx.fill();
    ctx.stroke();
}

function drawLine(x1, y1, x2, y2) {
    // Draws a line from one coord to the next
    ctx.beginPath();
    ctx.moveTo(x1, y1);
    ctx.lineTo(x2, y2);
    ctx.stroke();
}

function drawRect(x1, y1, x2, y2) {
    // Coordinates of the rectangle. Could be changed to width/height
    ctx.beginPath();
    ctx.rect(x1, y1, x2-x1, y2-y1);
    ctx.stroke();
}

function rgb(r, g, b){
    r = Math.floor(r);
    g = Math.floor(g);
    b = Math.floor(b);
    return ["rgb(",r,",",g,",",b,")"].join("");
}