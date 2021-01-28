#!/usr/bin/env python3
import numpy as np

class Particle:
    def __init__(self, x, y, theta, noisy=True, std_dev=None, weight=0.0):
        """
        std_dev: array of length 6, std deviations of noise for 
            x, y
            theta
            linear_vel
            angular_vel
            sensor_distance
            sensor_angle
        """
        self.x = x
        self.y = y
        self.theta = theta
        self.noisy = noisy
        self.std_dev = std_dev

        self.weight = weight

        # Initialize particles randomly
        if noisy:
            self.x = np.random.uniform(-self.std_dev[0], self.std_dev[0])
            self.y = np.random.uniform(-self.std_dev[0], self.std_dev[0])
            self.heading = np.random.uniform(0, 2 * np.pi)

    def copy(self):
        p = Particle(self.x, self.y, self.theta, self.noisy, self.std_dev, self.weight)

        # Right now these are randomized on init so set them back
        p.x = self.x
        p.y = self.y
        p.theta = self.theta

        return p

    def move(self, vel, angular_vel, dt):
        if self.noisy:
            angular_vel = self.add_noise(angular_vel, self.std_dev[3])
            vel = self.add_noise(vel, self.std_dev[2])

        dtheta = angular_vel * dt

        if abs(dtheta) < 0.0001:
            dx = vel * dt
            dy = 0
        else:
            # theta * r = distance around circle
            radius = vel * dt / dtheta
            dx = radius * np.sin(dtheta)
            dy = radius * (1 - np.cos(dtheta))

        # rotate translation in base_frame to global_frame
        self.x += dx * np.cos(self.theta) - dy * np.sin(self.theta)
        self.y += dx * np.sin(self.theta) + dy * np.cos(self.theta)
        self.theta += dtheta

    def add_noise(self, x, std_dev):
        return x + np.random.normal(0, std_dev)

    def read_sensor(self, world):
        """
        Find sensor measurement given particle location
        Will be compared to robot's noisy measurement

        world: 2D array of X, Y locations of landmarks
        """

        readings = []
        for i in range(len(world)):
            readings.append([self.euclidean_distance(self.x, self.y, world[i][0], world[i][1])])
        
        return readings

    def euclidean_distance(self, x1, y1, x2, y2):
        return np.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2))

    def probability_sensor_hit(self, z, z_actual):
        """z and z_actual: 2D arrays of measured and perfect landmark measurements"""

        # Sum distances sensor distance errors to all landmarks
        total_dist = 0
        for i in range(len(z)):
            total_dist += abs(z[i][0] -z_actual[i][0])

        # Normal distribution
        return 1 / (np.sqrt(2 * np.pi) * self.std_dev[4]) * \
            np.exp(-0.5 * (total_dist / self.std_dev[4]) ** 2)


if __name__ == "__main__":
    np.random.seed(0)

    particle = Particle(0, 0, 0, noisy=True, std_dev=[0, 0, 0, 0, 0, 0])

    for i in range(10):
        particle.move(2, 0.1, 0.5)
        print("%.3f, %.3f, %.3f" % (particle.x, particle.y, particle.theta))
