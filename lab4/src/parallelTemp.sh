#!/bin/bash
python RandomInts.py $1 1000 > random.txt
mpirun -np 12 --host ghc41,ghc43,ghc50,ghc45,ghc52,ghc25,ghc26 java ParallelKMeans points 4 1 random.txt

