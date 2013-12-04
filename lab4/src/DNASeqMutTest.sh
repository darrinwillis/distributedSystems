#!/bin/bash
echo "Generating random mutated dna..."
python MutateDNA.py > randomDNA.txt
echo "Performing K-means clustering"
java SequentialKMeans dna 3 0  randomDNA.txt

