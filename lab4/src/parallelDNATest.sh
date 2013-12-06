#!/bin/bash
echo "Generating random ints..."
python RandomDNA.py 10000 1000 > random.txt
echo "Performing K-means clustering"
mpirun -np 6 --host ghc41 java ParallelKMeans dna 3 2 random.txt

