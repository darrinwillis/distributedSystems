#!/bin/bash
echo "Generating random ints..."
python RandomDNA.py > randomDNA.txt
echo "Performing K-means clustering"
java SequentialKMeans dna 4 randomDNA.txt

