<div align="center">
  <img src="src/main/resources/j3D_LIGHT.png" alt="j3D Engine Logo" width="250">
</div>

## Overview

j3D is a lightweight, Java-based 3D game engine built with LWJGL and OpenGL. 

## Features

- **Modern OpenGL Integration**: Utilizes LWJGL for efficient OpenGL bindings
- **3D Model Loading**: Import and render OBJ format 3D models
- **Texture Support**: Load and apply textures to 3D models
- **Lighting System**: Primitive lighting with ambient, diffuse, and specular components
- **Camera Controls**: First-person camera with freecam for development
- **Scene Management**: Organize and manage entities in scene graphs
- **Performance Optimization**: Efficient rendering pipeline and resource management
- **Cross-Platform**: Works on any platform supporting Java and OpenGL

## Requirements

- Java 11 or higher
- System that supports OpenGL (most of them)
- Gradle

## Getting Started

### Quick Start
To launch the j3D application suite:

```bash
# Using Gradle (recommended)
./gradlew run

# Using the shell script
./launch_j3d.sh
```

The launcher provides access to:
- **j3D Engine**: The main 3D game engine
- **jEDIT Level Editor**: 2D level editor for creating 3D levels

### macOS Users
The launcher automatically handles the required `-XstartOnFirstThread` JVM argument for proper OpenGL context creation on macOS.

## Project Structure

- `src/main/java/com/discardsoft/j3D/` - Core engine and game logic
- `src/main/resources/` - Models, textures, and shaders
- `docs/` - Documentation and diagrams

## Documentation

For detailed documentation, please refer to the [j3D Manual](j3D_manual.md).

## License

2025 DISCVRD. Use freely. 

## Credits

Rhys Beynon
