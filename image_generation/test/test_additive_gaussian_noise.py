"""
Created on 10/8/22 by Ethan Frank

Test how we can mathematically apply gaussian noise twice to an image, in one step. (How do gaussians sum?)
"""
import numpy as np
import cv2

from generate_circle_images import make_circle
from util import float_image_to_uint8, unit8_image_to_float, get_gaussian_noise

if __name__ == "__main__":
    np.random.seed(1)

    s1 = 0.5
    s2 = 1
    s3 = np.sqrt(s1**2 + s2**2)  # Variances add, but std devs need to use pythagorean theorem.

    shape = (256, 256, 3)
    image = unit8_image_to_float(make_circle(shape[:2]))
    noise_image_1 = image + get_gaussian_noise(shape, s1)
    noise_image_2 = image + get_gaussian_noise(shape, s1)
    noise_image_1_2 = image + get_gaussian_noise(shape, s1) + get_gaussian_noise(shape, s2)
    noise_image_3 = image + get_gaussian_noise(shape, s3)

    combined = np.hstack([float_image_to_uint8(img) for img in (image, noise_image_1, noise_image_2, noise_image_1_2, noise_image_3)])
    cv2.imshow("Circle w/ noise", combined)
    cv2.waitKey(0)
