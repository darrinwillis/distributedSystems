#!/bin/bash
echo "Setting up rmi server; ignore socket errors"
rmiregistry > /dev/null 2> /dev/null &
echo "Setting up delegation server"
java -Djava.rmi.server.codebase=https://unix.andrew.cmu.edu/usr18/dswillis/private/15440/distributedSystems/lab1 ProcessDelegationServer &

