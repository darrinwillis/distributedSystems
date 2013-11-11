#!/bin/bash
echo "Ending old system"
./stopSystem.sh
echo "Starting System"
echo "Removing old scripts directory"
rm -r scripts 2>&1
echo "Making new scripts directory"
mkdir scripts
echo "Generating Scripts"
java Monitor startSystem
chmod +x scripts/*.sh
echo "Running master script"
./scripts/masterScript.sh
echo "Running node scripts"
for each in ./scripts/nodeScript*.sh;
do
    echo "Starting node: ${each}"
    bash $each
done ;
echo "System is up"
