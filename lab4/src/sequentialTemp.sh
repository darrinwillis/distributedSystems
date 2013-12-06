#!/bin/bash
python RandomInts.py $1 1000 > random.txt
java SequentialKMeans points 4 1 random.txt

