"""
Created on 10/7/22 by Ethan Frank

Function to add gaussian noise to an image of a circle
"""
import numpy as np
import cv2

from generate_circle_images import make_circle
from util import float_image_to_uint8, unit8_image_to_float, get_gaussian_noise


if __name__ == "__main__":
    np.random.seed(1)

    img = make_circle((256, 256))

    images = []
    for noise in [0.01, 0.05, 0.1, 0.5, 1, 5]:
        noise_img = unit8_image_to_float(img)
        noise_img = noise_img + get_gaussian_noise(noise_img.shape, noise)
        noise_img = float_image_to_uint8(noise_img)

        images.append(noise_img)

    composite_img = np.hstack(images)
    cv2.imshow("Gaussian noised circle", composite_img)
    cv2.waitKey(0)
