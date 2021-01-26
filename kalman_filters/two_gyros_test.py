#!/usr/bin/env python3

import numpy as np
import matplotlib.pyplot as plt

np.random.seed(0)

z_0 = np.random.normal(0, 1, size=(200, ))
z_1 = np.random.normal(0, 1, size=(200, ))
z_combined = (z_0 + z_1) * 0.5


def test_sensors_individually(z_0, z_1):
    x = np.array([[0]])
    p = np.array([[16]])
    q = np.array([[0.001]])
    f = np.array([[1]])
    r = np.array([[1, 0],
                  [0, 1]])
    h = np.array([[1], [1]])

    xs = []
    ps = []

    for i in range(len(z_0)):
        x = f.dot(x)
        p = f.dot(p).dot(f.T) + q

        z = np.array([[z_0[i]],[z_1[i]]])

        k = p.dot(h.T).dot(np.linalg.inv(h.dot(p).dot(h.T) + r))
        x = x + k.dot(z - h.dot(x))
        p = p - k.dot(h).dot(p)

        xs.append(x[0, 0])
        ps.append(p[0, 0])

    return xs, ps


def test_sensors_combined(z_0, z_1):
    x = np.array([[0]])
    p = np.array([[16]])
    q = np.array([[0.001]])
    f = np.array([[1]])
    r = np.array([[0.5]])
    h = np.array([[1]])

    z_combined = (z_0 + z_1) * 0.5 
    xs = []
    ps = []

    for i in range(len(z_0)):
        x = f.dot(x)
        p = f.dot(p).dot(f.T) + q

        z = z_combined[i]

        k = p.dot(h.T).dot(np.linalg.inv(h.dot(p).dot(h.T) + r))
        x = x + k.dot(z - h.dot(x))
        p = p - k.dot(h).dot(p)

        xs.append(x[0, 0])
        ps.append(p[0, 0])

    return xs, ps

xs, ps = test_sensors_individually(z_0, z_1)
xs_2, ps_2 = test_sensors_combined(z_0, z_1)

# plt.subplot(1, 2, 1)
# plt.plot(xs)
# plt.plot(z_0)
# plt.plot(z_1)
# plt.subplot(1, 2, 2)
# plt.plot(ps)
plt.plot(xs)
plt.plot(xs_2)
plt.show()
