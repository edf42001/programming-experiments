var canvas = document.getElementById("mainCanvas");
var width = canvas.width;
var height = canvas.height;
var ctx = canvas.getContext("2d");

var shapes = [];

// ID 0: Sphere
shapes.push({
    id: 0,
    x: 65,
    y: 205,
    z: 200,
    r: 70,
    bob: Math.random() * 6,
});

// ID 1: Rectangle
shapes.push({
    id: 1,
    x: 0,
    y: 100,
    z: 400,
    rx: 50,
    ry: 50,
    rz: 50,
    bob: Math.random() * 6,
});

//// ID 2: Plane
//shapes.push({
//    id: 2,
//    a: 0,
//    b: 1,
//    c: 0,
//    d: 0,
//    bob: Math.random() * 6,
//});

camera = {
    x: 0,
    y: 200,
    z: 0,
    dir: [0, 0, 1],
    fov: 1.0, // Radians, horizontal
    pan: 0,
    tilt: 0
};

// Stores which keys are pressed to move player
var keys = {w: false, a: false, s: false, d: false, up: false, down: false};

// Stores mouse which allows user to rotate.
// Last mouse allows to find mouse speed, determines how fast to rotate
var mouse = {x: 0, y: 0};
var lastMouse = {x: mouse.x, y: mouse.y}; // Assign by reference is annoying so don't do it

// Loop forever
var FPS = 10;
setInterval(update, 1000 / FPS);

let counter = 0;
function update() {
    // Stores the image data in one big array, rgba
    var imgData = ctx.createImageData(width, height);

//    let start = (new Date()).getMilliseconds();

    // Bob shapes up and down
    counter += 1;
    for (let i = 0; i < shapes.length; i++) {
        shapes[i].y += 4 * Math.cos(counter / 10 + shapes[i].bob);
    }


    // Rotate camera. (Careful not to assign mouse by reference)
    // Avoid large jump on first run
    var mouseSensitivity = 0.004;
    if (lastMouse.x != 0 && lastMouse.y != 0) {
        camera.pan -= mouseSensitivity * (mouse.x - lastMouse.x);
        camera.tilt -= mouseSensitivity * (mouse.y - lastMouse.y);

        // Constrain head look view
        camera.tilt = Math.max(Math.min(Math.PI / 2, camera.tilt), -Math.PI / 2);
    }
    lastMouse.x = mouse.x;
    lastMouse.y = mouse.y;

    // Move camera in all 3 axes (move along direction we are facing)
    movementAmount = 15;
    if (keys.w) {
        camera.x += -1 * movementAmount * Math.sin(camera.pan)
        camera.z += movementAmount * Math.cos(camera.pan);
    } else if (keys.s) {
        camera.x -= -1 * movementAmount * Math.sin(camera.pan)
        camera.z -= movementAmount * Math.cos(camera.pan);
    }

    if (keys.a) {
        camera.x += -1 * movementAmount * Math.cos(camera.pan)
        camera.z += -1 * movementAmount * Math.sin(camera.pan);
    } else if (keys.d) {
        camera.x -= -1 * movementAmount * Math.cos(camera.pan)
        camera.z -= -1 * movementAmount * Math.sin(camera.pan);
    }

    if (keys.up) {
        camera.y += movementAmount;
    } else if (keys.down) {
        camera.y -= movementAmount;
    }

    for (var i = 0; i < height; i++) {
        for (var j = 0; j < width; j++)
        {
            // Extract x and y from pixel coord
            x = (j - width / 2);
            y = (height / 2 - i);
            z = 200; // Controls FOV

            ray = {x: x, y: y, z:z};
            magnitude = Math.sqrt(ray.x**2 + ray.y**2 + ray.z**2);
            ray.x /= magnitude; ray.y /= magnitude; ray.z /= magnitude;

            // Ray is rotated by wherever the camera points
            ray = rotate_ray_by_camera_view(ray, camera.pan, camera.tilt)

            color = calculate_and_render_pixel(camera, ray, shapes);

            index = 4 * (i * width + j);
            imgData.data[index+0] = color[0];
            imgData.data[index+1] = color[1];
            imgData.data[index+2] = color[2];
            imgData.data[index+3] = color[3];
        }
    }

//    let end = (new Date()).getMilliseconds();
//    console.log("dt: " + (end - start));
    ctx.putImageData(imgData, 0, 0);
}

function calculate_and_render_pixel(camera, ray, shapes) {
    // Uses raymarching to calculate the color a pixel should be
    // Camera is the camera parameters: position, facing direction vector, fov)
    // Ray is the direction being looked in relative to camera dir
    // Shapes is the shapes in the world
    // Return rgba array
    let magnitude = 1000;
    let distLimit = 0.1;

    let marchedLength = 0;
    let minDist = 1000;
    let numSteps = 0;

    // If we start in a shape, invert the distance estimate so we know when we are out of the shape
//    let inside = 1;
//    if (getGlobalDistanceEstimate(camera.x, camera.y, camera.z, shapes) <= 0) {
//        inside = -1;
//    }

    do {
        currX = camera.x + ray.x * marchedLength;
        currY = camera.y + ray.y * marchedLength;
        currZ = camera.z + ray.z * marchedLength;
        dist = getGlobalDistanceEstimate(currX, currY, currZ, shapes);
        minDist = Math.min(dist, minDist);

        marchedLength += dist;
        numSteps += 1;
    } while (dist > distLimit && marchedLength < magnitude)

    hit = (dist <= distLimit);

    if (hit) {
        color = [255, 0, 0, 255];
        color[0] -= numSteps * 5;
    } else {
        color = [200, 200, 255, 255];
    }

    if (minDist > distLimit) {
        // This is actually a reverse glow, the default color is violet
        // and this makes it bluer
        color[0] -= 3 * minDist
    }

    return color;
}

function rotate_ray_by_camera_view(ray, pan, tilt) {
    // Rotates a ray from camera to a pixel to its new direction
    // given the fact that the camera is titled and panned
    // This is a rotation matrix but those aren't in javascript so
    // I do the calculations myself
    x = [Math.cos(pan), 0, Math.sin(pan)];
    y = [Math.sin(pan) * Math.sin(tilt), Math.cos(tilt), -Math.cos(pan) * Math.sin(tilt)];
    z = [-Math.sin(pan) * Math.cos(tilt), Math.sin(tilt), Math.cos(pan) * Math.cos(tilt)];

    rot_ray = {x: 0, y: 0, z: 0};

    rot_ray.x = ray.x * x[0] + ray.y * y[0] + ray.z * z[0];
    rot_ray.y = ray.x * x[1] + ray.y * y[1] + ray.z * z[1];
    rot_ray.z = ray.x * x[2] + ray.y * y[2] + ray.z * z[2];

    return rot_ray;
}

// Store key presses so the update function can move the camera
window.onkeydown = function(event){
    updateKey(event.keyCode, true);
};

window.onkeyup = function(event){
    updateKey(event.keyCode, false);
};

function updateKey(keyCode, value) {
    // wasd (space) (shift) = 87,65,83,68,32,16
    switch (event.keyCode) {
        case 87:
            keys.w = value;
            break;
        case 65:
            keys.a = value;
            break;
        case 83:
            keys.s = value;
            break;
        case 68:
            keys.d = value;
            break;
        case 32:
            keys.up = value;
            break;
        case 16:
            keys.down = value;
            break;
        default:
            break;
    }
}

// Store mouse movements so the player can move the screen
window.onmousemove = function(event) {
    mouse.x = event.x;
    mouse.y = event.y;
}

function getGlobalDistanceEstimate(x, y, z, shapes) {
    // The distance estimate is the minimum of the distance to all objects on screen
    let dist = 100000;
    for (let i = 0; i < shapes.length; i++) {
        if (shapes[i].id == 0) {
            dist = Math.min(dist, circleDistanceEstimate(x, y, z, shapes[i]));
        } else if (shapes[i].id == 1) {
            dist = Math.min(dist, rectDistanceEstimate(x, y, z, shapes[i]));
        } else if (shapes[i].id == 2) {
             dist = Math.min(dist, planeDistanceEstimate(x, y, z, shapes[i]));
        } else {
            console.log("Unknown shape id: " + id);
        }
    }

    return dist;
}

function circleDistanceEstimate(x, y, z, circle) {
    // Distance estimate for a circle: dist from a point to surface of circle
    return Math.sqrt((x - circle.x)**2 + (y - circle.y)**2 + (z - circle.z)**2) - circle.r;
}

function rectDistanceEstimate(x, y, z, rect) {
    // Distance from a point to the edge, or 0 if inside
    x = Math.max(Math.abs(x - rect.x) - rect.rx, 0)
    y = Math.max(Math.abs(y - rect.y) - rect.ry, 0)
    z = Math.max(Math.abs(z - rect.z) - rect.rz, 0)
    return Math.sqrt(x*x + y*y + z*z);
}

function planeDistanceEstimate(x, y, z, plane) {
    // Distance from point to plane
    // TODO: Need to divide abc part by norm n
    return (x * plane.a + y * plane.b + z * plane.c) /  + plane.d;
}

function drawRect(x1, y1, x2, y2) {
    // Coordinates of the rectangle. Could be changed to width/height
    ctx.beginPath();
    ctx.rect(x1, y1, x2-x1, y2-y1);
    ctx.stroke();
}
