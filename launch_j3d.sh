#!/bin/zsh
# Script to launch j3D Launcher application
# The launcher provides access to both the j3D Engine and jEDIT Level Editor

# Go to the j3D directory (where this script is located)
cd "$(dirname "$0")"

# Run the main j3D launcher via Gradle
./gradlew run
