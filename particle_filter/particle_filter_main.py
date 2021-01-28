#!/usr/bin/env python3 

import signal
import sys
from enum import Enum

import matplotlib.pyplot as plt
import numpy as np

from particle import Particle
from weighted_distribution import WeightedDistribution


class MeasurementMode(Enum):
    DISTANCE = 1  # Distance to any landmark
    DISTANCE_FOV = 2  # Distance to any landmark in the field of view
    DISTANCE_ANGLE = 3  # Distance and angle to any landmark
    DISTANCE_ANGLE_FOV = 4  # Distance and angle to any landmark in the field of view


class ParticleFilter:
    def __init__(self):
        self.n_particles = 100
        self.particles = [Particle(0, 0, 0, noisy=True, std_dev=[1, 1, 0.1, 0.2, 0.2, 0],
                        weight=(1.0 / self.n_particles)) for i in range(self.n_particles)]

        self.robot = Particle(0, 0, 0, noisy=False)

        self.world = [[1, 1], [2, 3], [-4, 2]]  # 2D array of landmark X, Y locations
        self.measurement_mode = MeasurementMode.DISTANCE  # What sensor data the robot can recieve

    def motion_step(self, vel, angular_vel, dt):
        for p in self.particles:
            p.move(vel, angular_vel, dt)

    def plot(self):
        n_particles = len(self.particles)
        xs = [p.x for p in self.particles]
        ys = [p.y for p in self.particles]
        weights = [max(4, 30 * n_particles * p.weight) for p in self.particles]

        x_est, y_est, theta_est = self.estimate_robot_position()

        plt.xlim(-5, 5)
        plt.ylim(-5, 5)

        plt.scatter(xs, ys, s=weights, alpha=0.2)  # Particles
        plt.scatter(self.robot.x, self.robot.y, s=25, alpha=1)  # Robot
        plt.scatter([mark[0] for mark in self.world], [mark[1] for mark in self.world])  # landmarks
        plt.scatter(x_est, y_est, s=16, alpha=1)  # Estimated robot

        plt.show()

    def measurement_step(self):
        z_actual = self.robot.read_sensor(self.world)

        total = 0
        for p in self.particles:
            z_particle = p.read_sensor(self.world)
            p.weight = p.probability_sensor_hit(z_particle, z_actual)
            total += p.weight

        normalizer = 1.0 / total
        for p in self.particles:
            p.weight *= normalizer

    def resample(self):
        n_particles = len(self.particles)
        sampler = WeightedDistribution(self.particles)

        sample_type = "min_var"
        if sample_type == "random":
            self.particles = sampler.random_sample_particles(n_particles)
        elif sample_type == "min_var":
            self.particles = sampler.min_variance_sample(n_particles)
        else:
            print("ERROR: Unknown sample type " + sample_type)

    def estimate_robot_position(self):
        x_total = 0
        y_total = 0
        theta_total = 0
        weight_total = 0

        for p in self.particles:
            weight_total += p.weight
            x_total += p.x * p.weight
            y_total += p.y * p.weight
            theta_total += p.heading * p.weight

        if weight_total == 0:
            return 0, 0, 0

        x = x_total / weight_total
        y = y_total / weight_total
        theta = theta_total / weight_total

        return x, y, theta


def signal_handler(sig, frame):
    plt.close()
    sys.exit(0)


if __name__ == "__main__":
    np.random.seed(1)
    # Stop matplotlib for loop from running
    signal.signal(signal.SIGINT, signal_handler)

    robot_motions = np.array([[1, 0.5, 1],
                              [1, 0.5, 1],
                              [1, 0.5, 1],
                              [1, 0.5, 1],
                              [1, 0.5, 1]])

    particle_filter = ParticleFilter()
    for move in robot_motions:
        particle_filter.plot()

        particle_filter.robot.move(move[0], move[1], move[2])
        particle_filter.motion_step(move[0], move[1], move[2])

        print("After motion")
        particle_filter.plot()

        particle_filter.measurement_step()
        
        print("Weights updated")
        particle_filter.plot()

        particle_filter.resample()
        print("Resampled")
