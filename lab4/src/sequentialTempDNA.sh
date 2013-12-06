#!/bin/bash
python RandomDNA.py $1 1000 > random.txt
java SequentialKMeans dna 3 2 random.txt
#mpirun -np 4 --host ghc41 java ParallelKMeans dna 3 2 random.txt

