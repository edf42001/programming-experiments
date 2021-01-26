#!/usr/bin/env python3

from keras.datasets.mnist import load_data
from keras.models import Sequential, load_model
from keras.optimizers import Adam
from keras.layers import Dense, Conv2D, Flatten, Dropout, LeakyReLU, Reshape, Conv2DTranspose

import matplotlib.pyplot as plt
import os
import numpy as np

image_num = 0  # To name image folders easier
const_latent_points = None  # For generating consistent visuals

def define_discriminator(in_shape=(28, 28, 1)):
    model = Sequential()
    model.add(Conv2D(64, (3,3), strides=(2, 2), padding='same', input_shape=in_shape))
    model.add(LeakyReLU(alpha=0.2))
    model.add(Dropout(0.4))
    model.add(Conv2D(64, (3,3), strides=(2, 2), padding='same'))
    model.add(LeakyReLU(alpha=0.2))
    model.add(Dropout(0.4))
    model.add(Flatten())
    model.add(Dense(1, activation='sigmoid'))
    opt = Adam(lr=0.0002, beta_1=0.5)
    model.compile(loss='binary_crossentropy', optimizer=opt, metrics=['accuracy'])
    
    return model

def define_generator(latent_dim, len_conv=128):
    model = Sequential()
    # foundation for 7x7 image
    n_nodes = len_conv * 7 * 7
    model.add(Dense(n_nodes, input_dim=latent_dim))
    model.add(LeakyReLU(alpha=0.2))
    model.add(Reshape((7, 7, len_conv)))
    # upsample to 14x14
    model.add(Conv2DTranspose(len_conv, (4,4), strides=(2,2), padding='same'))
    model.add(LeakyReLU(alpha=0.2))
    # upsample to 28x28
    model.add(Conv2DTranspose(len_conv, (4,4), strides=(2,2), padding='same'))
    model.add(LeakyReLU(alpha=0.2))
    model.add(Conv2D(1, (7,7), activation='sigmoid', padding='same'))
    return model


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


def generate_fake_samples(n_samples):
    # generate uniform random numbers in [0,1]
    X = np.random.rand(28 * 28 * n_samples)
    # reshape into a batch of grayscale images
    X = X.reshape((n_samples, 28, 28, 1))
    # generate 'fake' class labels (0)
    ys = np.zeros((n_samples, 1))
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

def train_discriminator(model, data, n_iter=100, n_batch=256):
    half_batch = int(n_batch / 2)
    # manually enumerate epochs
    for i in range(n_iter):
        # get randomly selected 'real' samples
        X_real, y_real = generate_real_samples(data, half_batch)
        # update discriminator on real samples
        _, real_acc = model.train_on_batch(X_real, y_real)
        # generate 'fake' examples
        X_fake, y_fake = generate_fake_samples(half_batch)
        # update discriminator on fake samples
        _, fake_acc = model.train_on_batch(X_fake, y_fake)
        # summarize performance
        print('>%d real=%.0f%% fake=%.0f%%' % (i+1, real_acc*100, fake_acc*100))


def define_gan(g_model, d_model):
    d_model.trainable = False

    model = Sequential()
    model.add(g_model)
    model.add(d_model)

    opt = Adam(lr=0.0002, beta_1=0.5)
    model.compile(loss='binary_crossentropy', optimizer=opt)
    return model


def train_gan(g_model, latent_dim, n_epochs=100, n_batch=256):
    # manually enumerate epochs
    for i in range(n_epochs):
        # prepare points in latent space as input for the generator
        x_gan = generate_latent_points(latent_dim, n_batch)
        # create inverted labels for the fake samples
        y_gan = np.ones((n_batch, 1))
        # update the generator via the discriminator's error
        gan_model.train_on_batch(x_gan, y_gan)

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


def save_generator_model(g_model):
    global image_num

    filename = "models/g_model_%05d.h5" % (image_num)
    g_model.save(filename)


def train(g_model, d_model, gan_model, dataset, latent_dim, n_epochs=100, n_batch=256):
    bat_per_epo = int(dataset.shape[0] / n_batch)
    half_batch = int(n_batch / 2)
    # manually enumerate epochs
    for i in range(n_epochs):
        # enumerate batches over the training set
        for j in range(bat_per_epo):
            # get randomly selected 'real' samples
            X_real, y_real = generate_real_samples(dataset, half_batch)
            # generate 'fake' examples
            X_fake, y_fake = generate_fake_samples_w_model(g_model, latent_dim, half_batch)
            # create training set for the discriminator
            X, y = np.vstack((X_real, X_fake)), np.vstack((y_real, y_fake))

            # update discriminator model weights
            d_loss, _ = d_model.train_on_batch(X, y)

            X_gan = generate_latent_points(latent_dim, n_batch)
            # create inverted labels for the fake samples
            y_gan = np.ones((n_batch, 1))
            # update the generator via the discriminator's error
            g_loss = gan_model.train_on_batch(X_gan, y_gan)
            # summarize loss on this batch
            print('>%d, %d/%d, d=%.3f, g=%.3f' % (i+1, j+1, bat_per_epo, d_loss, g_loss))

            # Periodically check performance and save model
            if j % 5 == 0:
                save_image_benchmark(g_model)

        if i % 1 == 0:
            save_generator_model(g_model)


load = False
model_path = "batch_one_10_epochs/models/g_model_0470.h5"

latent_dim = 30

# Create a constant set of points
const_latent_points = generate_latent_points(latent_dim, 25)

d_model = define_discriminator()
if load:
    g_model = load_model(model_path)
else:
    g_model = define_generator(latent_dim, len_conv=64)
gan_model = define_gan(g_model, d_model)

d_model.summary()
g_model.summary()
gan_model.summary()

data = load_real_data()

create_folder("images")
create_folder("models")

train(g_model, d_model, gan_model, data, latent_dim, n_epochs=100)
