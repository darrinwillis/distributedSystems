#!/bin/bash
echo "Generating random ints..."
python RandomInts.py $1 1000 > random.txt
echo "Performing K-means clustering"
mpirun -np 6 --host ghc40 java ParallelKMeans points 4 1 random.txt

