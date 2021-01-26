#!/usr/bin/env python3

from keras.datasets.mnist import load_data
from keras.models import Sequential, load_model

import matplotlib.pyplot as plt
import os
import numpy as np

def create_folder(folder_name):
    if not os.path.exists(folder_name):
        os.mkdir(folder_name)


def load_real_data():
    # load data
    (trainX, _), (_, _) = load_data()
    X = np.expand_dims(trainX, axis=-1)
    X = X.astype('float32')
    X = X / 255.0

    return X


def generate_real_samples(data, n_samples):
    ix = np.random.randint(0, data.shape[0], n_samples)
    X = data[ix]
    ys = np.ones((n_samples, 1))

    return X, ys

def generate_latent_points(latent_dim, n_samples):
    # generate points in the latent space
    x_input = np.random.randn(latent_dim * n_samples)
    # reshape into a batch of inputs for the network
    x_input = x_input.reshape(n_samples, latent_dim)
    return x_input


def generate_fake_samples_w_model(g_model, latent_dim, n_samples):
    # generate points in latent space
    x_input = generate_latent_points(latent_dim, n_samples)
    # predict outputs
    X = g_model.predict(x_input)
    # create 'fake' class labels (0)
    y = np.zeros((n_samples, 1))
    return X, y

def save_image_benchmark(g_model):
    global image_num, const_latent_points

    fake_data = g_model.predict(const_latent_points)

    for i in range(25):
        # define subplot
        plt.subplot(5, 5, 1 + i)
        # turn off axis
        plt.axis('off')
        # plot raw pixel data
        plt.imshow(fake_data[i], cmap='gray_r')

    filename = "images/gan_images_%05d.png" % (image_num)
    image_num += 1

    plt.savefig(filename)


model_path = "models/g_model_00320.h5"

latent_dim = 30

g_model = load_model(model_path)
g_model.summary()


# Generate a bunch of fake numbers in a grid

data = load_real_data()

fake_images, _ = generate_fake_samples_w_model(g_model, latent_dim, 225)

for i in range(225):
    # define subplot
    plt.subplot(15, 15, 1 + i)
    # turn off axis
    plt.axis('off')
    # plot raw pixel data
    plt.imshow(fake_images[i], cmap='gray_r')

plt.show()


# Try making an animation of transforming numbers

# latent_point = generate_latent_points(latent_dim, 1)
# create_folder("transforming_images")

# for i in range(100):
#     direction = generate_latent_points(latent_dim, 1) * 0.5
#     latent_point += direction

#     image = g_model.predict(latent_point)

#     if i % 20 == 0:
#         direction = generate_latent_points(latent_dim, 1) * 0.5

#     for j in range(1):
#         # define subplot
#         plt.subplot(1, 1, 1 + j)
#         # turn off axis
#         plt.axis('off')
#         # plot raw pixel data
#         plt.imshow(image[j], cmap='gray_r')

#     filename = "transforming_images/gan_images_%04d.png" % (i)

#     plt.savefig(filename)


# Generate fake and real images together to test
# if people can tell the difference

# data = load_real_data()
# types = []

# images = np.empty((0, 28, 28, 1))

# for i in range(20):
#     if np.random.uniform() < 0.5:
#         types.append(1)
#         image, _ = generate_real_samples(data, 1)
#         images = np.append(images, image, axis=0)
#     else:
#         types.append(0)
#         image, _ = generate_fake_samples_w_model(g_model, latent_dim, 1)
#         images = np.append(images, image, axis=0)

# print(types)

# for j in range(20):
#         # define subplot
#         plt.subplot(1, 20, 1 + j)
#         # turn off axis
#         plt.axis('off')
#         # plot raw pixel data

#         if types[j] == 0:
#             plt.imshow(images[j], cmap='gray_r')
#         else:
#             plt.imshow(images[j], cmap='gray_r')

# plt.show()