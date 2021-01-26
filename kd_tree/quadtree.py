#!/usr/bin/env python3
import matplotlib.pyplot as plt
import random


class QuadNode:
    def __init__(self, center, size):
        self.center = center
        self.size = size  # half square width
        self.element = None
        self.children = [None] * (2 ** len(center))

    def add(self, new):
        empty = self.children[0] is None

        if not self.element and empty:
            self.element = new
        else:
            if empty:
                self.children[0] = QuadNode([self.center[0] - self.size / 4, self.center[1] - self.size / 4], self.size / 2)
                self.children[1] = QuadNode([self.center[0] - self.size / 4, self.center[1] + self.size / 4], self.size / 2)
                self.children[2] = QuadNode([self.center[0] + self.size / 4, self.center[1] - self.size / 4], self.size / 2)
                self.children[3] = QuadNode([self.center[0] + self.size / 4, self.center[1] + self.size / 4], self.size / 2)

                self.children[self.get_relative_location(self.element)].element = self.element
                self.element = None

            self.children[self.get_relative_location(new)].add(new)

    def get_relative_location(self, other):
        location = ''
        for i in range(len(self.center)):
            location += '0' if other[i] < self.center[i] else '1'

        return int(location, 2)

    def plot(self):
        if self.children[0]:
            plt.plot((self.center[0] - self.size / 2, self.center[0] + self.size / 2), (self.center[1], self.center[1]), 'b')
            plt.plot((self.center[0], self.center[0]), (self.center[1] - self.size / 2, self.center[1] + self.size / 2), 'b')

            for child in self.children:
                child.plot()

        if self.element:
            plt.scatter(self.element[0], self.element[1])

points = []
for i in range(30):
    points.append([2 * random.random(), 2 * random.random()])

top_node = QuadNode([1, 1], 2)

for p in points:
    top_node.add(p)

plt.xlim(0, 2)
plt.ylim(0, 2)
top_node.plot()
plt.show()
