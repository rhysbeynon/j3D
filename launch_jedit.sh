#!/bin/zsh
# Script to launch jEDIT application

# Go to the j3D directory (where this script is located)
cd "$(dirname "$0")"

# Run the jEDIT launcher via Gradle
./gradlew runJEditLauncher
