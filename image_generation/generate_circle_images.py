"""
Created on 10/4/22 by Ethan Frank

Code to generate a random image of a colored circle
"""


import cv2
import numpy as np


def make_circle(image_size):
    img = np.full((image_size[0], image_size[1], 3), 127, dtype=np.uint8)

    # convert data types int64 to int https://stackoverflow.com/questions/60484383/typeerror-scalar-value-for-argument-color-is-not-numeric-when-using-opencv
    color = tuple((int(c) for c in np.random.randint(0, 255, size=(3, ))))

    img = cv2.circle(img, (image_size[0] // 2, image_size[1] // 2),
                     radius=int(image_size[0] * 0.3), color=color, thickness=-1)

    return img


if __name__ == "__main__":
    image_size = (64, 64)

    for _ in range(100):
        img = make_circle(image_size)

        cv2.imshow("Random Circle", img)
        cv2.waitKey(30)
