#!/bin/bash
echo 'Starting'
for i in 1000 1500 2000 2500 3000 3500 4000 4500 5000
do
    echo ""
    echo "\nTesting for $1"
    for j in 1 2 3
    do
        ./parallelTempDNA.sh $i
    done
done

