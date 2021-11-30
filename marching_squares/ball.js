class Ball {
    constructor(width, height) {
        this.x = Math.random() * width;
        this.y = Math.random() * height;
        this.xdot = 10 * (Math.random() - 0.5);
        this.ydot = 10 * (Math.random() - 0.5);
        this.r = Math.random() * 35 + 35;
        this.width = width;
        this.height = width;
    }

    update() {
        if (this.x < 0 || this.x > this.width) {
            this.xdot *= -1;
        }

        if (this.y < 0 || this.y > this.height) {
            this.ydot *= -1;
        }

        this.x += this.xdot;
        this.y += this.ydot;
    }
}