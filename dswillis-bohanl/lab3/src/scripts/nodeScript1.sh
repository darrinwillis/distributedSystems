#!/bin/bash
ssh unix1.andrew.cmu.edu -f 'cd private/15440/distributedSystems/lab3/src/;
java NodeServer &;exit 1;echo "Exited"'