#!/usr/bin/env python3

class KDNode:
    def __init__(self, element):
        self.element = element
        self.level = 0
        self.left = None
        self.right = None

    def add(self, new):
        if new[self.level] < self.element[self.level]:
            if self.left:
                self.left.add(new)
            else:
                self.left = KDNode(new)
                self.left.level = (self.level + 1) % len(new)
        else:
            if self.right:
                self.right.add(new)
            else:
                self.right = KDNode(new)
                self.right.level = (self.level + 1) % len(new)

    def display(self):
        print("Node: " + str(self.element))
        if self.left:
            self.left.display()
        if self.right:
            self.right.display()


class KDTree:
    def __init__(self):
        self.base_node = None

    def create(self, points):
        self.n = len(points[0])

        self.base_node = KDNode(points[0])

        for i in range(1, len(points)):
            self.base_node.add(points[i])

    def display(self):
        self.base_node.display()


points = [[3, 6], [17, 15], [13, 15], [6, 12], [9, 1], [2, 7], [10, 19]]
tree = KDTree()
tree.create(points)
tree.display()
