# j3D Engine Manual
**Version 0.06a | April 23, 2025**

<div align="center">
  <img src="src/main/resources/j3D_DARK.png" width="300" alt="j3D Logo">
</div>

## Table of Contents

1. [Introduction](#introduction)
2. [Engine Architecture](#engine-architecture)
3. [Core Components](#core-components)
4. [Entity System](#entity-system)
5. [Scene Management](#scene-management)
6. [Rendering System](#rendering-system)
7. [Input Handling](#input-handling)
8. [Utility Classes](#utility-classes)
9. [Character Controller](#character-controller)
10. [Best Practices](#best-practices)
11. [Future Development](#future-development)

## Introduction

j3D is a lightweight 3D game engine built with LWJGL, OpenGL, JOML, and other supporting libraries. It provides a simple yet powerful framework for creating 3D applications and games.

This manual documents the refactored architecture of the engine, highlighting the improvements made to enhance scalability, maintainability, and performance.

### Key Features

- Scene-based rendering system
- Entity management
- First-person camera controls
- Character controller with physics-based movement
- Basic lighting system (ambient, diffuse, specular)
- OBJ model loading
- Texture management
- Shader system for custom rendering effects

## Engine Architecture

The j3D engine follows a component-based architecture with clear separation of concerns:

```
com.discardsoft.j3D
├── Main                  # Application entry point
├── core                  # Core engine systems
│   ├── EngineManager     # Main game loop and timing
│   ├── IGameLogic        # Game implementation interface
│   ├── ObjectLoader      # Resource loading
│   ├── RenderManager     # Rendering pipeline
│   ├── ShaderManager     # Shader program handling
│   ├── WindowManager     # Window and input management
│   ├── entity            # Entity component system
│   ├── scene             # Scene management
│   └── utils             # Helper utilities
└── game                  # Game implementation
    └── TestGame          # Example game implementation
```

## Core Components

### EngineManager

The EngineManager class is the core of the engine, managing the game loop timing, FPS calculation, and coordinating initialization, updates, rendering, and cleanup processes.

**Key Methods:**
- `init()`: Initializes engine components
- `start()`: Begins the game loop
- `run()`: Executes the main game loop with fixed timestep updates
- `cleanup()`: Releases all resources

### IGameLogic

This interface defines the contract that any game implementation must follow to operate within the engine's lifecycle.

**Required Methods:**
- `init()`: Initialize game resources
- `input()`: Process user input
- `update()`: Update game state
- `render()`: Render the game scene
- `cleanup()`: Release game resources

### WindowManager

Handles window creation, input processing, and viewport management through GLFW.

**Key Features:**
- Window creation and configuration
- Mouse and keyboard input handling
- Viewport and projection management

## Entity System

The entity system provides the fundamental objects that make up a scene:

### Entity

Represents a renderable object in 3D space with:
- Position
- Rotation
- Scale
- 3D Model reference

**Key Methods:**
- `incrementPosition(x, y, z)`: Move the entity
- `incrementRotation(x, y, z)`: Rotate the entity
- `setPosition(x, y, z)`: Set absolute position
- `setRotation(x, y, z)`: Set absolute rotation

### Model

Represents a 3D model with:
- Vertex Array Object (VAO) ID 
- Vertex count
- Texture reference

### Camera

Provides a view into the 3D world with:
- Position
- Rotation
- First-person movement controls

**Key Methods:**
- `movePosition(x, y, z)`: Move relative to camera's orientation
- `rotateCamera(deltaYaw, deltaPitch)`: Rotate the camera

### Light

Defines a light source with:
- Position
- Color
- Ambient light intensity

### Player

Represents a player character in the game world with:
- Physics-based movement with inertia
- First-person camera at eye level
- Collision detection via a bounding capsule
- Support for both physics-based and free camera modes

**Key Methods:**
- `update(deltaTime)`: Update player position based on physics and input
- `setMovementInput(forwardMovement, sidewaysMovement)`: Set movement direction
- `toggleFreeCamera()`: Switch between player-bound and free camera modes
- `setPosition(x, y, z)`: Set absolute player position (feet position)
- `getPosition()`: Get current player position
- `getVelocity()`: Get player's current velocity vector
- `getPlayerCamera()`: Get the camera position (eye level)

## Scene Management

The scene system organizes entities for efficient management:

### TestScene

A concrete scene implementation that:
- Manages collections of entities
- Provides central access to the scene lighting
- Updates all entities in batch

**Key Methods:**
- `initialize()`: Set up the initial scene contents
- `addEntity(entity)`: Add an entity to the scene
- `update(deltaTime)`: Update all entities with time-based movement
- `getEntities()`: Get all entities for rendering
- `getLight()`: Get the scene's light source

## Rendering System

The rendering system handles the display of 3D objects:

### RenderManager

Manages the OpenGL rendering process:
- Initializes shaders
- Sets uniform variables
- Renders entities and entire scenes

**Key Methods:**
- `render(scene, camera)`: Render an entire scene
- `render(entity, camera, light)`: Render a single entity (legacy)

### ShaderManager

Handles GLSL shader programs:
- Compiles vertex and fragment shaders
- Links shader programs
- Sets uniform variables

## Input Handling

Input handling is primarily managed through the WindowManager class, which provides methods to:
- Check if a key is pressed
- Check for buffered key presses (pressed once)
- Process mouse movement

## Utility Classes

### Utils

General utility methods:
- Buffer management for OpenGL
- Resource loading

### Transformation

Matrix calculations for 3D transformations:
- Create transformation matrices for entities
- Create view matrices for cameras

### Settings

Configurable engine settings:
- Development mode flags
- Window properties
- Camera controls

### Consts

Engine-wide constants:
- Version information
- Rendering constants

### LoadModel

Helper methods for loading 3D models:
- Load OBJ models with textures
- Provide primitive shapes

## Character Controller

The j3D engine provides a character controller system for implementing player movement and interaction:

### Player Controller

The Player class serves as a character controller with the following features:

#### Physics-Based Movement
- Momentum and inertia simulation
- Maximum velocity capping
- Delta-time independent movement

#### Camera Positioning
- Eye-level camera positioning
- First-person perspective
- Smooth movement

#### Free Camera Mode
- Toggle between character-bound and free-floating camera
- Camera position and rotation preservation when switching modes
- Development-friendly camera controls

#### Implementation Example

```java
// Create a player at position (0,0,0)
Player player = new Player(new Vector3f(0, 0, 0));

// Handle keyboard input for player movement
public void handleInput() {
    // Get movement input (-1, 0, or 1 for each axis)
    float forwardInput = 0;
    float sidewaysInput = 0;
    
    if (window.isKeyPressed(GLFW.GLFW_KEY_W)) forwardInput = 1;
    if (window.isKeyPressed(GLFW.GLFW_KEY_S)) forwardInput = -1;
    if (window.isKeyPressed(GLFW.GLFW_KEY_A)) sidewaysInput = -1;
    if (window.isKeyPressed(GLFW.GLFW_KEY_D)) sidewaysInput = 1;
    
    // Apply input to player movement
    player.setMovementInput(forwardInput, sidewaysInput);
    
    // Toggle free camera with F key
    if (window.isKeyPressed(GLFW.GLFW_KEY_F)) {
        player.toggleFreeCamera();
    }
}

// Update player in game loop
public void update(float deltaTime) {
    player.update(deltaTime);
}

// In render method, use player's camera
public void render() {
    renderer.render(scene, player.getCamera());
}
```

## Best Practices

When working with the j3D engine, follow these best practices:

### Scene Management

1. **Use the scene-based approach**: Add entities to scenes and render entire scenes rather than individual entities.
2. **Organize related entities**: Group related entities together in appropriate scenes.

### Entity Creation

1. **Reuse models**: Share model data between similar entities to reduce memory usage.
2. **Clean up unused resources**: Call cleanup methods when entities are no longer needed.

### Performance Optimization

1. **Batch similar entities**: Keep entities with the same model/texture together for more efficient rendering.
2. **Use delta time**: Scale animations and movements by delta time for consistent speed.

### Code Organization

1. **Follow naming conventions**: Use the established naming patterns for consistency.
2. **Document with JavaDoc**: Add JavaDoc comments to describe classes, methods, and parameters.

### Player Implementation

1. **Use the Player class for first-person games**: The Player class provides a complete character controller with physics-based movement.
2. **Scale movement by delta time**: Use the provided update method with proper delta time to ensure consistent movement across different frame rates.
3. **Use free camera mode for development**: Toggle free camera mode during development to easily navigate and debug scenes.
4. **Customize physics parameters**: Modify player constants to adjust movement feel (inertia, maximum velocity, etc.)

## Future Development

The j3D engine has several planned areas for future enhancement:

### Scene Management Improvements

- Scene transition system
- Hierarchical scene graph
- Improved entity organization

### Rendering Enhancements

- Multiple shader support
- Post-processing effects
- Shadow mapping
- Particle systems

### Physics Integration

- Enhanced collision detection
- Rigid body physics
- Constraint systems
- Advanced character controllers

### Asset Management

- Centralized resource cache
- Asynchronous loading
- Level loading system

### Entity Component System

- Full ECS architecture
- Component-based entities
- Systems for processing components

---

© 2025 DiscardSoft