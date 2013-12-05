#!/bin/bash
echo "Generating random ints..."
python RandomInts.py 1000000 10000 > random.txt
echo "Performing K-means clustering"
java SequentialKMeans points 4 1 random.txt

