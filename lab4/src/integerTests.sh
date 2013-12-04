#!/bin/bash
echo "Generating random ints..."
python Randoms.py
echo "Performing K-means clustering"
java SequentialKMeans random.txt

