#!/bin/bash
echo "Generating random DNA..."
python RandomDNA.py > randomDNA.txt
echo "Performing K-means clustering"
java SequentialKMeans dna 3 0  randomDNA.txt

