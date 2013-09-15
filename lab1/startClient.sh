#!/bin/bash
echo "Connecting to Server $1"
java -Djava.security.policy=client.policy ProcessManagerClient $1

