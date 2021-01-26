# Script to run on aws ec2 gpu instance

set -e

echo "Training networks"
./written_digits_gan.py

echo "Converting to video"
./images_to_video.py

echo "Done"
