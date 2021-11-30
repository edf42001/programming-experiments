class Vector {
    constructor(x, y) {
        this.x = x;
        this.y = y;
    }

    add(other) {
        return new Vector(this.x + other.x, this.y + other.y);
    }

    scale(a) {
        return new Vector(this.x * a, this.y * a);
    }

    static lerp(v1, v2, a) {
        return new Vector(v1.x * (1 - a) + v2.x * a, v1.y * (1 - a) + v2.y * a);
    }
}