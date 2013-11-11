#!/bin/bash
echo "Starting System"
echo "Removing old scripts directory"
rm -r scripts 2>&1
echo "Making new scripts directory"
mkdir scripts
echo "Generating Scripts"
java Monitor startSystem
chmod +x scripts/*.sh
