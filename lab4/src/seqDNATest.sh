#!/bin/bash
echo "Generating random DNA..."
python RandomDNA.py 10000 1000 > randomDNA.txt
echo "Performing K-means clustering"
java SequentialKMeans dna 3 2  randomDNA.txt

