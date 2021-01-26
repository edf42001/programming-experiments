#!/usr/bin/env python3
import glob
import os
import cv2
from datetime import datetime

image_folder = "/home/edf42001/images_1/images"
video_path = "gan_numbers_"  + datetime.now().strftime("%m_%d_%H_%M") + ".avi"
fps = 20

files = glob.glob(image_folder + "/*")
files.sort()

frame_array = []
for i in range(len(files)):
    # reading each file
    img = cv2.imread(files[i])
    height, width, layers = img.shape
    size = (width, height)
    
    #inserting the frames into an image array
    frame_array.append(img)

video_out = cv2.VideoWriter(video_path, cv2.VideoWriter_fourcc(*'DIVX'), fps, size)

for frame in frame_array:
    video_out.write(frame)
video_out.release()
