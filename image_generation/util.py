"""
Created on 10/7/22 by Ethan Frank

Utility functions
"""

import numpy as np


def float_image_to_uint8(img):
    """
    Converts a float image in the range (-1, 1) (Values that a neural network likes to take in)
    to a uint8 image in the range (0, 255)
    """
    return ((np.clip(img, -1, 1) + 1) * 127.5).astype(np.uint8)


def unit8_image_to_float(img):
    """
    Converts a uint8 image in the range (0, 255) to a float image in the range(-1, 1)
    """
    return img / 127.5 - 1


def get_gaussian_noise(img_shape, std_dev):
    """Returns gaussian noise of a certain shape and std dev, centered at 0"""
    return np.random.normal(0, std_dev, size=img_shape)
