#!/usr/bin/env python3

import gym
import random
import numpy as np

from tensorflow.keras.models import Sequential
from tensorflow.keras.layers import Dense, Flatten
from tensorflow.keras.optimizers import Adam
from tensorflow.keras.callbacks import TensorBoard

from rl.agents import DQNAgent
from rl.policy import BoltzmannQPolicy
from rl.memory import SequentialMemory


# Create the model
def build_model(states, actions, layer_size=24, name="Sequential"):
    model = Sequential(name=name)
    model.add(Flatten(input_shape=(1, states)))
    model.add(Dense(layer_size, activation='relu'))
    model.add(Dense(layer_size, activation='relu'))
    model.add(Dense(actions, activation='linear'))
    return model


# Create the learning agent
def build_agent(model, actions):
    policy = BoltzmannQPolicy()
    memory = SequentialMemory(limit=50000, window_length=1)
    dqn = DQNAgent(model=model, memory=memory, policy=policy,
                    nb_actions=actions, nb_steps_warmup=10, target_model_update=1e-2)
    return dqn


layer_sizes = [24, 12, 6]  # Layer size to test

# Test different parameters
for size in layer_sizes:
    model_folder_name = "models"
    model_name = "model_%d" % size

    tensorboard_callback = TensorBoard(log_dir="./logs/" + model_name, histogram_freq=0, write_graph=False,
                                       write_images=False, update_freq="epoch", profile_batch=0)

    # Make the environment
    env = gym.make('CartPole-v0')
    states = env.observation_space.shape[0]
    actions = env.action_space.n

    # Create model
    model = build_model(states, actions, layer_size=size, name=model_name)
    model.summary()

    # Create agent
    dqn = build_agent(model, actions)
    dqn.compile(Adam(lr=1e-3), metrics=['mae'])

    # Load saved agent state
    # dqn.load_weights(model_folder_name + "/" + model_file_name)

    dqn.fit(env, nb_steps=50000, visualize=False, verbose=1,
            callbacks=[tensorboard_callback])

# scores = dqn.test(env, nb_episodes=30, visualize=False)

    dqn.save_weights(model_folder_name + "/" + model_name + ".h5f", overwrite=True)
# episodes = 10
# for episode in range(1, episodes+1):
#     state = env.reset()
#     done = False
#     score = 0

#     while not done:
#         env.render()
#         action = random.choice([0, 1])
#         n_state, reward, done, info = env.step(action)
#         score += reward

#     print("Episode: {} Score: {}".format(episode, score))
