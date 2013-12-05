#!/bin/bash
echo "Generating random ints..."
python RandomInts.py > random.txt
echo "Performing K-means clustering"
mpirun -np 8 java ParallelKMeans points 4 1 random.txt

