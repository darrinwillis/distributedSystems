#!/bin/bash
echo "Setting up master server"
ssh unix2.andrew.cmu.edu -f '
cd private/15440/distributedSystems/lab3/src/;
java Monitor &;exit 1; echo "Exited"'
