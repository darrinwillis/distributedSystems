#!/bin/bash
echo "Setting up rmi server"
java -Djava.rmi.server.codebase=https://unix.andrew.cmu.edu/usr18/dswillis/private/15440/distributedSystems/lab1 ProcessDelegationServer &

