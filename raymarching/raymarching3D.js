var canvas = document.getElementById("mainCanvas");
var width = canvas.width;
var height = canvas.height;
var ctx = canvas.getContext("2d");
var fpsDisplay = document.getElementById("fpsDisplay");

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

// ID 1: Rectangle
shapes.push({
    id: 1,
    x: 300,
    y: 150,
    z: 400,
    rx: 20,
    ry: 100,
    rz: 120,
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

// A light source
light = {
    x: -450,
    y: 400,
    z: 100,
}

// Array to store camera rays, instead of calculating every frame
var rays = create_camera_ray_matrix(width, height, 200);

// Stores which keys are pressed to move player
var keys = {w: false, a: false, s: false, d: false, up: false, down: false};

// Stores mouse which allows user to rotate.
// Last mouse allows to find mouse speed, determines how fast to rotate
var mouse = {x: 0, y: 0};
var lastMouse = {x: mouse.x, y: mouse.y}; // Assign by reference is annoying so don't do it

// Loop forever
var FPS = 10;
setInterval(update, 1000 / FPS);

// To keep track of performance
let currentFPS = FPS;
let lastFPS = currentFPS;
let lastTime = new Date();

// Stores the image data in one big array, rgba. Alpha will always be 255.
var imgData = create_image_data(width, height);

// Store key presses so the update function can move the camera
window.onkeydown = function(event){
    updateKey(event.keyCode, true);
};

window.onkeyup = function(event){
    updateKey(event.keyCode, false);
};

// Store mouse movements so the player can move the screen
window.onmousemove = function(event) {
    mouse.x = event.x;
    mouse.y = event.y;
}

let counter = 0;
function update() {
    // Keep track of deltaT and fps
    let currTime = new Date();
    // Smooth by adjusting alpha param
    currentFPS = 0.4 * (1000 / (currTime - lastTime)) + 0.6 * lastFPS;
    lastTime = currTime;
    lastFPS = currentFPS;
    fpsDisplay.innerHTML = "FPS: " + Math.trunc(currentFPS);

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
    movementAmount = 20;
    let dx = movementAmount * Math.cos(camera.pan);
    let dy = movementAmount * Math.sin(camera.pan);
    if (keys.w) {
        camera.x += -dy;
        camera.z += dx;
    } else if (keys.s) {
        camera.x += dy;
        camera.z += -dx;
    }

    if (keys.a) {
        camera.x += -dx;
        camera.z += -dy;
    } else if (keys.d) {
        camera.x += dx;
        camera.z += dy;
    }

    if (keys.up) {
        camera.y += movementAmount;
    } else if (keys.down) {
        camera.y -= movementAmount;
    }

    for (var i = 0; i < height; i++) {
        for (var j = 0; j < width; j++)
        {
            // The camera ray is rotated by wherever the camera points
            ray = rotate_ray_by_camera_view(rays[i][j], camera.pan, camera.tilt)

            color = calculate_and_render_pixel(camera, ray, shapes);

            index = 4 * (i * width + j);
            imgData.data[index+0] = color[0];
            imgData.data[index+1] = color[1];
            imgData.data[index+2] = color[2];
        }
    }

    ctx.putImageData(imgData, 0, 0);
}

function calculate_and_render_pixel(camera, ray, shapes) {
    // Uses raymarching to calculate the color a pixel should be
    // Camera is the camera parameters: position, facing direction vector, fov)
    // Ray is the direction being looked in relative to camera dir
    // Shapes is the shapes in the world
    // Return rgb array
    let magnitude = 1000;
    let distLimit = 0.1;

    let marchedLength = 0;
    let minDist = 1000;
    let numSteps = 0;

    // Do one iteration first with a random dist adjustment so banding
    // of num iterations ambient occlusion disappears
    let dist = globalDistanceEstimate(camera.x, camera.y, camera.z, shapes);
    marchedLength = dist * (Math.random() * 0.9 + 0.1);
    numSteps += 1;

    do {
        currX = camera.x + ray.x * marchedLength;
        currY = camera.y + ray.y * marchedLength;
        currZ = camera.z + ray.z * marchedLength;
        dist = globalDistanceEstimate(currX, currY, currZ, shapes);
        minDist = Math.min(dist, minDist);

        marchedLength += dist;
        numSteps += 1;
    } while (dist > distLimit && marchedLength < magnitude)

    hit = (dist <= distLimit);

    if (hit) {
        color = [150, 0, 0]; // No light color is not entirely black because of ambient

        // Ambient occlusion (surface complexity proportional to steps taken)
        color[0] -= numSteps * 5;

        // Apply global light source, intensity from 0 to 1
        let point = {x: currX, y: currY, z: currZ};

        rayToLight = {x: light.x - point.x, y: light.y - point.y, z: light.z - point.z};
        distToLight = ray_magnitude(rayToLight);
        normalize_ray(rayToLight);

        // TODO: check if ray hits light before doing more calculations
        // TODO: See if this fixes specular issues
        normalVec = normal_vector(point.x, point.y, point.z, shapes);
        let specular = specular_light_intensity(rayToLight, normalVec, ray);
        let diffuse = diffuse_light_intensity(rayToLight, normalVec);
        let shadow = shadow_light_intensity(rayToLight, distToLight, point, shapes);

        let intensity = specular + shadow;

        // Diffuse light should be the color of the object, but the others the color of the light
        let lightScale = 200;
        color[0] += (intensity + diffuse) * lightScale;
        color[1] += intensity * lightScale;
        color[2] += intensity * lightScale;
    } else {
        color = [230, 200, 255];
    }

    if (minDist > distLimit) {
        // This is actually a reverse glow, the default color is violet
        // and this makes it bluer
        color[0] -= 5 * minDist
    }

    return color;
}


function specular_light_intensity(rayToLight, normal, viewRay) {
    let n = 50; // Adjusts radius of highlight
    let k = 2; // Adjusts brightness of highlight

    // Calculate the reflected ray R = 2(N*L)N - L
    let scaler = 2 * (rayToLight.x * normal.x + rayToLight.y * normal.y + rayToLight.z * normal.z);
    let reflected = {
        x: scaler * normal.x - rayToLight.x,
        y: scaler * normal.y - rayToLight.y,
        z: scaler * normal.z - rayToLight.z
    }

    // Specular highlight is brightest when reflected ray from light
    // aligns from view ray
    // Need a negative sign because view ray and reflected rays face opposite directions
    let specular = -(reflected.x * viewRay.x + reflected.y * viewRay.y + reflected.z * viewRay.z);

    if (specular < 0) {
        // If the rays face opposite directions then there is no way for light to reach it
        return 0;
    }

    return k * Math.pow(specular, n);
}


function diffuse_light_intensity(rayToLight, normal) {
    let k = 1; // Control intensity of diffuse light

    // Diffuse reflection is strongest directly under the light, and tapers off
    let diffuse = (rayToLight.x * normal.x + rayToLight.y * normal.y + rayToLight.z * normal.z);

    return k * Math.max(0, diffuse);
}


function shadow_light_intensity(rayToLight, distToLight, point, shapes) {
    // To find if light hits an object, trace from the intersection of the camera ray back towards the light source
    // If it makes it without hitting anything, light falls on the object

    let distLimit = 0.1;
    let marchedLength = 1; // Need to start away from surface otherwise it'll think it hit itself

    do {
        currX = point.x + rayToLight.x * marchedLength;
        currY = point.y + rayToLight.y * marchedLength;
        currZ = point.z + rayToLight.z * marchedLength;
        dist = globalDistanceEstimate(currX, currY, currZ, shapes);

        marchedLength += dist;
    } while (dist > distLimit && marchedLength < distToLight)

    // The > 3 check ensures it doesn't shadow itself, which looks bad
    let inShadow = marchedLength < distToLight && marchedLength > 3;

    if (inShadow) {
        return -1;
    }

    return 0;
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

// Uses discrete steps to find normal vector
function normal_vector(x, y, z, shapes) {
    let eps = 0.2; // Step size
    let dx = globalDistanceEstimate(x+eps, y, z, shapes) - globalDistanceEstimate(x-eps, y, z, shapes);
    let dy = globalDistanceEstimate(x, y+eps, z, shapes) - globalDistanceEstimate(x, y-eps, z, shapes);
    let dz = globalDistanceEstimate(x, y, z+eps, shapes) - globalDistanceEstimate(x, y, z-eps, shapes);

    ray = {x: dx, y: dy, z: dz};
    normalize_ray(ray);
    return ray;
}

// Create an array to store camera rays, instead of calculating every frame
function create_camera_ray_matrix(rays, z_fov) {
    var rays = [...Array(height)].map(e => Array(width));
    for (var i = 0; i < height; i++) {
        for (var j = 0; j < width; j++)
        {
            // Extract x and y from pixel coord
            x = (j - width / 2);
            y = (height / 2 - i);

            ray = {x: x, y: y, z:z_fov};
            normalize_ray(ray);

            rays[i][j] = ray;
        }
    }

    return rays;
}

function create_image_data(width, height) {
    var imgData = ctx.createImageData(width, height);
    for (var i = 0; i < height; i++) {
        for (var j = 0; j < width; j++)
        {
            index = 4 * (i * width + j);
            imgData.data[index+3] = 255;
        }
    }

    return imgData;
}

// Modifies in place
function normalize_ray(ray) {
    let magnitude = ray_magnitude(ray);
    ray.x /= magnitude; ray.y /= magnitude; ray.z /= magnitude;
}

// Pythagorean theorem
function ray_magnitude(ray) {
    return Math.sqrt(ray.x**2 + ray.y**2 + ray.z**2);
}

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

function globalDistanceEstimate(x, y, z, shapes) {
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
    return (x * plane.a + y * plane.b + z * plane.c) + plane.d;
}

function drawRect(x1, y1, x2, y2) {
    // Coordinates of the rectangle. Could be changed to width/height
    ctx.beginPath();
    ctx.rect(x1, y1, x2-x1, y2-y1);
    ctx.stroke();
}
