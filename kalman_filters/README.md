# kalman_filters

Experiments with kalman filters to figure out how they tick

In this instance, I was seeing if fusing two gyros in a kalman filter is mathematically equivalent to averaging the gyro measurements first and then fusing, or doing two predict/fuse steps one at a time. For the case when the sensor noise variance is the same and the sensors are uncorrelated, this appears to be the case

## Basic usage
Run `./two_gyros_test.py` to visualize the different methods
