#!/bin/bash
ssh unix5.andrew.cmu.edu -f 'cd private/15440/distributedSystems/lab3/src/;
java Monitor startMaster&;exit 1;echo "Exited"'