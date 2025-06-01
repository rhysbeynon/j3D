# j3D Launcher Shortcuts

This directory contains shortcuts to launch the j3D Launcher application, which provides access to both the j3D Engine and jEDIT Level Editor.

## Launch Options:

1. **Terminal:**
   - From anywhere, type: `j3D`
   - From the j3D directory, run: `./launch_j3d.sh`

2. **Desktop:**
   - Double-click the "j3D" shortcut on your Desktop

3. **Applications:**
   - Launch from Applications folder: ~/Applications/j3D.app

4. **VS Code:**
   - Press Cmd+Shift+B (or access through Terminal → Run Build Task)
   - Select "Run j3D Launcher"

5. **Gradle:**
   - From the j3D directory, run: `./gradlew run`

## What's Available:
The launcher provides access to:
- **j3D Engine**: The main 3D game engine
- **jEDIT Level Editor**: The 2D level editor for creating 3D levels

## Notes:
- Level files are saved with the ".j3dl" extension
- The launcher ensures proper macOS threading for OpenGL applications
- All applications launched through this system will have the correct JVM arguments

## First-Time Setup:
After creating these shortcuts, you may need to:
1. Restart your terminal to use the `j3D` command
2. Right-click on the Desktop shortcut and choose "Open" the first time to bypass macOS security

Enjoy using j3D!
