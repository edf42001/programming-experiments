var canvas = document.getElementById("mainCanvas");
var ctx = canvas.getContext("2d");

// Outline the canvas
drawRect(0, 0, canvas.width, canvas.height);

var shapes = [];

// ID 0: Circle
shapes.push({
    id: 0,
    x: 200,
    y: 100,
    r: 50,
    bob: Math.random() * 6
});

shapes.push({
    id: 0,
    x: 265,
    y: 225,
    r: 70,
    bob: Math.random() * 6
});

shapes.push({
    id: 1,
    x: 175,
    y: 320,
    rx: 100,
    ry: 20,
    bob: Math.random() * 6
});

// Global variable for mouse pos
var mousePos = {x: 0, y: 0};
var lastMousePos = mousePos;

// Loop forever
var FPS = 30;
setInterval(update, 1000 / FPS);

// To bob up and down
var counter = 0;

function update() {
    counter += 1;

    // Bob shapes up and down
    for (let i = 0; i < shapes.length; i++) {
        shapes[i].y += 0.5 * Math.cos(counter / 30 + shapes[i].bob);
    }

    lastMousePos = mousePos;

    let startX = 20; let startY = 200;
    let magnitude = Math.sqrt((mousePos.x - startX) ** 2 + (mousePos.y - startY) ** 2)
    let unitVector = {x: (mousePos.x - startX) / magnitude, y: (mousePos.y - startY) / magnitude};

    let marchedLength = 0;
    do {
        currX = startX + unitVector.x * marchedLength;
        currY = startY + unitVector.y * marchedLength;
        dist = getGlobalDistanceEstimate(currX, currY, shapes);

        marchedLength += dist;
    } while (dist > 0.1 && dist < 600 && marchedLength < magnitude)

    hit = (dist <= 0.1);

    // Drawing begins now
    // Clear canvas and draw a line. Don't clear the border around the canvas
    ctx.clearRect(1, 1, canvas.width-2, canvas.height-2);

    if (hit) {
        // Draw the line only to where it hit and make it red
        ctx.strokeStyle ="#FF0000";
        drawLine(currX, currY, 20, 200);
        drawCircle(currX, currY, 3);  // Highlight the intersection point
    } else {
        // Draw the line normally
        ctx.strokeStyle ="#000000";
        drawLine(mousePos.x, mousePos.y, 20, 200);
    }

    // Draw shapes to screen, make sure they have the right color
    ctx.strokeStyle ="#000000";
    for (let i = 0; i < shapes.length; i++) {
        if (shapes[i].id == 0) {
            drawCircle(shapes[i].x, shapes[i].y, shapes[i].r);
        } else if (shapes[i].id == 1) {
            x = shapes[i].x; y = shapes[i].y; rx = shapes[i].rx, ry = shapes[i].ry;
            drawRect(x-rx, y-ry, x+rx, y+ry);
        } else {
            console.log("Unknown shape id: " + id);
        }
    }
}

function getGlobalDistanceEstimate(x1, y1, shapes) {
    // The distance estimate is the minimum of the distance to all objects on screen
    let dist = 100000;
    for (let i = 0; i < shapes.length; i++) {
        if (shapes[i].id == 0) {
            dist = Math.min(dist, circleDistanceEstimate(x1, y1, shapes[i]));
        } else if (shapes[i].id == 1) {
            dist = Math.min(dist, rectDistanceEstimate(x1, y1, shapes[i]));
        } else {
            console.log("Unknown shape id: " + id);
        }
    }

    return dist;
}

function circleDistanceEstimate(x1, y1, circle) {
    // Distance estimate for a circle: dist from a point to surface of circle
    return Math.sqrt((x1 - circle.x) ** 2 + (y1 - circle.y) ** 2) - circle.r;
}

function rectDistanceEstimate(x1, y1, rect) {
    // Distance from a point to the edge, or 0 if inside
    x = Math.max(Math.abs(x1 - rect.x) - rect.rx, 0)
    y = Math.max(Math.abs(y1 - rect.y) - rect.ry, 0)
    return Math.sqrt(x*x + y*y);
}

function drawCircle(x1, y1, r) {
    // Draws a hollow circle
    ctx.beginPath();
    ctx.arc(x1, y1, r, 0, 2 * Math.PI);
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

// Basic implementation of mouse position on canvas from
// https://stackoverflow.com/questions/17130395/real-mouse-position-in-canvas
function getMousePos(canvas, evt) {
    var rect = canvas.getBoundingClientRect();
    return {
      x: evt.clientX - rect.left,
      y: evt.clientY - rect.top
    };
}

function storeMousePos(event) {
    // Store mouse pos in global variable
    mousePos = getMousePos(canvas, event);
}