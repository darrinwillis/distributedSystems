#!/bin/bash
echo 'Starting'
for i in 100 500 1000 5000 10000 50000 100000 500000 1000000
do
    echo ""
    echo "\nTesting for $1"
    for j in 1 2 3
    do
        ./parallelTemp.sh $i
    done
done

