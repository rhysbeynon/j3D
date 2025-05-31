# j3D Engine Manual
**Version 0.07a | May 31, 2025**

<div align="center">
  <img src="src/main/resources/j3D_DARK.png" width="300" alt="j3D Logo">
</div>

## Table of Contents

1. [Introduction](#introduction)
2. [Engine Architecture](#engine-architecture)
3. [Core Components](#core-components)
4. [Entity System](#entity-system)
   - [Billboarding System](#billboarding-system)
   - [Terrain System](#terrain-system)
5. [Scene Management](#scene-management)
6. [Rendering System](#rendering-system)
7. [Input Handling](#input-handling)
8. [User Interface System](#user-interface-system)
9. [Debug Tools](#debug-tools)
10. [Utility Classes](#utility-classes)
11. [Character Controller](#character-controller)
12. [Best Practices](#best-practices)
13. [Future Development](#future-development)
14. [Advanced Development Guide](#advanced-development-guide)

## Introduction

j3D is a lightweight 3D game engine built with LWJGL, OpenGL, JOML, and other supporting libraries. It provides a simple yet powerful framework for creating 3D applications and games.

This manual documents the refactored architecture of the engine, highlighting the improvements made to enhance scalability, maintainability, and performance.

### Key Features

- Scene-based rendering system
- Entity management
- First person camera controls
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
│   │   ├── Camera        # Camera entity
│   │   ├── Entity        # Base entity class
│   │   ├── Light         # Light entity
│   │   ├── Model         # 3D model data
│   │   ├── Player        # Player character controller
│   │   ├── Texture       # Texture data
│   │   └── terrain       # Terrain system
│   │       ├── Terrain   # Terrain generation
│   │       └── TerrainEntity # Terrain entity wrapper
│   ├── scene             # Scene management
│   │   ├── BaseScene     # Abstract scene base class
│   │   ├── IScene        # Scene interface
│   │   └── TestScene     # Concrete scene implementation
│   ├── ui                # User interface system
│   │   ├── UIElement     # Base UI element
│   │   ├── UIManager     # UI management
│   │   ├── Panel         # UI panel component
│   │   ├── Menu          # Menu system
│   │   └── PauseMenu     # Pause menu implementation
│   └── utils             # Helper utilities
│       ├── Consts        # Engine constants
│       ├── DebugHUD      # Debug information display
│       ├── LoadModel     # Model loading utilities
│       ├── Settings      # Engine settings
│       ├── TextRenderer  # Text rendering
│       ├── Transformation # Matrix transformations
│       └── Utils         # General utilities
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
- `init()`: Initializes game resources
- `input()`: Process user input
- `update()`: Update game state
- `render()`: Render the game scene
- `cleanup()`: Release game resources

### WindowManager

Handles window creation, input processing, and viewport management with GLFW.

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
- Billboard flags (Y axis and Full)

**Key Methods:**
- `incrementPosition(x, y, z)`: Move the entity
- `incrementRotation(x, y, z)`: Rotate the entity
- `setPosition(x, y, z)`: Set absolute position
- `setRotation(x, y, z)`: Set absolute rotation
- `setBillboardY(boolean)`: Enable Y axis (horizontal) billboarding
- `setBillboardFull(boolean)`: Enable full billboarding
- `setHasTransparentTexture(boolean)`: Set transparency flag for proper rendering

### Billboarding System

The billboarding system allows entities to automatically orient themselves toward the camera, which is useful for vegetation, particles, and other 2D elements in a 3D world.

#### Types of Billboarding

j3D supports two types of billboarding:

1. **Y axis Billboarding**: The entity rotates only around the Y axis to face the camera, maintaining its vertical orientation. This is ideal for trees, grass, and other objects that should remain upright.

2. **Full Billboarding**: The entity completely faces the camera from all angles. This is suitable for particles, icons, and other elements that should always be fully visible to the player.

#### Using Billboarding

To create billboarded entities:

```java
// Create a standard entity first
Entity entity = new Entity(
    model,
    new Vector3f(x, y, z),  // position
    new Vector3f(0, 0, 0),  // rotation (will be managed by billboarding)
    new Vector3f(1, 1, 1)   // scale
);

// For Y axis billboarding (horizontal rotation only)
entity.setBillboardY(true);

// Or for full billboarding (always facing camera)
entity.setBillboardFull(true);

// Most billboarded entities have transparent textures
entity.setHasTransparentTexture(true);
```

#### Implementation Details

The billboarding system automatically calculates the correct orientation based on the camera's position:

- Y axis billboarding calculates the angle between the entity and camera in the XZ plane
- Full billboarding creates a complete look-at matrix so the entity always faces the camera

Billboarded entities are automatically rendered with proper transparency if the `hasTransparentTexture` flag is set.

### Terrain System

The j3D engine includes a robust terrain system for creating large-scale ground surfaces and landscapes.

#### Terrain Generation

The terrain system generates terrain meshes from height data and applies textures:

```java
// Create a terrain with specified parameters
Terrain terrain = new Terrain(
    512.0f,                    // terrain size (width and depth)
    64,                        // grid resolution (64x64 vertices)
    0.0f,                      // base height (Y position)
    new Vector3f(-256, 0, -256), // position (centered at origin)
    objectLoader,              // object loader instance
    "src/main/resources/textures/ground2.png", // texture path
    64.0f                      // texture repeat factor
);
```

#### TerrainEntity

TerrainEntity wraps the terrain data into an entity that can be added to scenes:

```java
// Create a terrain entity from terrain data
TerrainEntity terrainEntity = new TerrainEntity(terrain);

// Add terrain to the scene
scene.addEntity(terrainEntity);
```

**Key Features:**
- **Procedural Generation**: Generate terrain meshes with customizable resolution
- **Texture Mapping**: Apply textures with configurable repeat factors
- **Scene Integration**: Seamlessly integrate with the entity/scene system
- **Performance Optimized**: Efficient mesh generation and rendering

### Model

Represents a 3D model with:
- Vertex Array Object (VAO) ID 
- Vertex count
- Texture reference

### Camera

Provides a view into the 3D world with:
- Position
- Rotation
- First person movement controls

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
- First person camera at eye level
- Collision detection via a bounding capsule
- Support for both physics-based and free camera modes

**Key Methods:**
- `update(deltaTime)`: Update player position based on physics and input
- `setMovementInput(forwardMovement, sidewaysMovement)`: Set movement direction
- `toggleFreeCamera()`: Switch between player-bound and free camera modes
- `setPosition(x, y, z)`: Set absolute player position (feet position)
- `getPosition()`: Get current player position
- `getVelocity()`: Get player's current velocity vector
- `getCamera()`: Get the player's camera instance
- `getBoundingEntity()`: Get the player's collision entity

#### Global Camera Access

The Main class provides global access to the current camera:

```java
// Get the current camera from anywhere in the application
Camera currentCamera = Main.getCurrentCamera();

// This method returns the player's camera if the game and player are initialized
// Returns null if either is not available
```

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

The j3D engine provides comprehensive input handling through the WindowManager class, supporting both keyboard and mouse input for games and applications.

### Keyboard Input

The WindowManager provides several methods for handling keyboard input:

#### Basic Key Detection

```java
// Check if a key is currently being held down
if (window.isKeyPressed(GLFW.GLFW_KEY_W)) {
    // Move forward while W is held
    player.moveForward();
}

// Check for single key press (buffered input)
if (window.isKeyPressedBuffered(GLFW.GLFW_KEY_SPACE)) {
    // Jump only once per key press
    player.jump();
}
```

**Key Methods:**
- `isKeyPressed(int keyCode)`: Returns true while a key is held down
- `isKeyPressedBuffered(int keyCode)`: Returns true only once per key press
- Built-in support for force quit with backslash key

### Mouse Input

The mouse input system handles both movement and button presses:

#### Mouse Movement

```java
// Process mouse movement for camera control
Vector2f mouseMovement = window.processMouseMovement();
camera.processMouseMovement(mouseMovement.x, mouseMovement.y);
```

#### Mouse Buttons

```java
// Check if left mouse button is pressed
if (window.isMouseButtonPressed()) {
    // Handle continuous input (e.g., shooting)
    handleShooting();
}

// Check for single click events
if (window.isMouseButtonClicked()) {
    // Handle one-time actions (e.g., menu selection)
    selectMenuItem();
}
```

#### Scroll Wheel

```java
// Handle scroll wheel input
if (window.hasScrolled()) {
    double scrollOffset = window.getScrollOffset();
    // Zoom camera or change weapon
    camera.adjustZoom(scrollOffset);
}
```

### Cursor Management

The engine automatically manages cursor capture for camera control:

```java
// Release cursor (makes it visible, stops camera control)
window.releaseCursor();

// Capture cursor for camera control
window.captureCursor();

// Check cursor state
if (window.isCursorCaptured()) {
    // Camera control is active
}

if (window.isWaitingForClick()) {
    // Cursor released, waiting for click to recapture
}
```

### Window Focus Management

The engine handles window focus automatically:

```java
// Check if window has focus
if (window.isWindowFocused()) {
    // Process input normally
} else {
    // Window is not focused, pause game or reduce processing
}

// Add listeners for focus events
window.addWindowFocusListener(focused -> {
    if (focused) {
        System.out.println("Window gained focus");
    } else {
        System.out.println("Window lost focus");
    }
});
```

### Input Best Practices

1. **Use buffered input for discrete actions**: Use `isKeyPressedBuffered()` for actions that should only trigger once per key press
2. **Handle window focus**: The engine automatically pauses cursor capture when the window loses focus
3. **Mouse sensitivity**: Configure mouse sensitivity in the Settings class
4. **Raw mouse input**: The engine automatically uses raw mouse input when available for better camera control

### Advanced Input Features

The WindowManager also provides:
- Automatic cursor capture/release on window focus/unfocus
- Event listeners for cursor capture and window focus changes
- Support for raw mouse motion when available
- Configurable mouse sensitivity through Settings

## User Interface System

The j3D engine provides a comprehensive UI system for creating game interfaces, menus, and HUD elements.

### UI Architecture

The UI system is built around the following components:

#### UIElement

The base class for all UI components:

```java
/**
 * Base UI element with position, size, and parent-child relationships
 */
public abstract class UIElement {
    protected Vector2f position;    // Normalized screen coordinates (0-1)
    protected Vector2f size;        // Normalized size
    protected UIElement parent;     // Parent element
    protected List<UIElement> children; // Child elements
    
    // Core methods
    public abstract void render();
    public abstract void update(float deltaTime);
    public abstract boolean handleInput(WindowManager window);
}
```

#### UIManager

Manages all UI elements and coordinates rendering:

```java
// Create and use a UI manager
UIManager uiManager = new UIManager();

// Add UI elements
uiManager.addElement(panel);
uiManager.addElement(menu);

// Update and render in game loop
uiManager.update(deltaTime);
uiManager.render();
```

### UI Components

#### Panel

Rectangular UI elements for backgrounds and containers:

```java
// Create a panel with position, size, and color
Panel panel = new Panel(
    new Vector2f(0.1f, 0.1f),      // position (10% from top-left)
    new Vector2f(0.8f, 0.8f),      // size (80% of screen)
    new Vector4f(0.2f, 0.2f, 0.2f, 0.8f) // color (dark gray, 80% opacity)
);
```

#### Menu System

Create interactive menus with buttons and navigation:

```java
// Create a main menu
Menu mainMenu = new Menu(
    new Vector2f(0.5f, 0.5f),      // centered position
    new Vector2f(0.4f, 0.6f)       // menu size
);

// Add menu items with callbacks
mainMenu.addMenuItem("Start Game", () -> {
    gameState.setState(GameState.PLAYING);
});

mainMenu.addMenuItem("Settings", () -> {
    showSettingsMenu();
});

mainMenu.addMenuItem("Exit", () -> {
    System.exit(0);
});
```

#### PauseMenu

A specialized menu for game pause functionality:

```java
// Create and show pause menu
PauseMenu pauseMenu = new PauseMenu();
pauseMenu.setVisible(true);

// Handle pause menu input
if (window.isKeyPressedBuffered(GLFW.GLFW_KEY_ESCAPE)) {
    pauseMenu.toggle();
}
```

### UI Input Handling

UI elements can capture and handle input events:

```java
// Override handleInput in custom UI elements
@Override
public boolean handleInput(WindowManager window) {
    // Check if mouse is over this element
    if (isMouseOver(window.getMousePosition())) {
        // Handle click
        if (window.isMouseButtonPressed(GLFW.GLFW_MOUSE_BUTTON_LEFT)) {
            onClick();
            return true; // Input consumed
        }
    }
    return false; // Input not consumed
}
```

## Debug Tools

The j3D engine includes comprehensive debugging tools to assist with development and troubleshooting.

### DebugHUD

The DebugHUD displays real-time information about the game state:

```java
// Create and initialize debug HUD
DebugHUD debugHUD = new DebugHUD(window);

// Render debug information in the game loop
debugHUD.render(
    currentFps,           // Current FPS
    player,              // Player entity for position/velocity info
    scene,               // Scene for entity count
    cameraMoveSpeed      // Current camera movement speed
);
```

**Debug Information Displayed:**
- **FPS**: Real-time frames per second
- **Position**: Player position coordinates
- **Velocity**: Player movement velocity vector
- **Camera Rotation**: Camera pitch and yaw angles
- **Entity Count**: Number of entities in the current scene
- **Camera Speed**: Current movement speed setting

### TextRenderer

The TextRenderer provides functionality for drawing text on screen:

```java
// Create text renderer
TextRenderer textRenderer = new TextRenderer();

// Render text at specific positions
textRenderer.renderText(
    "Debug Information",     // text to render
    10,                     // x position (pixels)
    10,                     // y position (pixels)
    new Vector3f(1, 1, 1)   // color (white)
);
```

### Visual Debug Features

#### Wireframe Mode

Toggle wireframe rendering to inspect mesh structure:

```java
// Toggle wireframe mode
if (window.isKeyPressedBuffered(GLFW.GLFW_KEY_APOSTROPHE)) {
    wireframeMode = !wireframeMode;
    GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, 
        wireframeMode ? GL11.GL_LINE : GL11.GL_FILL);
}
```

#### Free Camera Mode

Use the player's free camera mode for scene inspection:

```java
// Toggle free camera for development
if (window.isKeyPressedBuffered(GLFW.GLFW_KEY_F)) {
    player.toggleFreeCamera();
}
```

## Utility Classes

### Utils

General utility methods for the engine:
- Buffer management for OpenGL data transfer
- Resource loading and file handling
- Memory management utilities

### Transformation

Matrix calculations for 3D transformations:
- `createTransformationMatrix()`: Create transformation matrices for entities
- `createViewMatrix()`: Create view matrices for cameras
- `updateProjectionMatrix()`: Update projection matrices for rendering

### Settings

Configurable engine settings stored as constants:

**Development Settings:**
- `DEV`: Development mode flag (enables debug features)

**Window Settings:**
- `RESIZABLE`: Window resizability flag
- `VSYNC`: Vertical synchronization flag

**Camera Settings:**
- `CAMERA_MOVE_SPEED`: Speed multiplier for camera movement
- `MOUSE_SENSITIVITY`: Sensitivity multiplier for mouse movement
- `FOV`: Field of view angle in radians

**UI Settings:**
- `UI_SCALE`: Global scale multiplier for UI elements

```java
// Example usage of settings
if (Settings.DEV) {
    // Enable development features
    enableWireframeMode();
}

// Use camera settings
float moveSpeed = Settings.CAMERA_MOVE_SPEED * deltaTime;
```

### Consts

Engine-wide constants for version information and rendering:

**Version and Branding:**
- `VERSION`: Engine version string ("v0.07a")
- `AUTHOR`: Author/company name ("DiscardSoft")
- `AUTHOR_STYLIZED`: Stylized author name ("DISCVRD")
- `TITLE`: Engine name ("j3D")

**Rendering Constants:**
- `Z_NEAR`: Near clipping plane distance (0.001f)
- `Z_FAR`: Far clipping plane distance (10000f)

### LoadModel

Helper methods for loading 3D models and creating primitive shapes:
- `loadOBJModel()`: Load OBJ models with textures
- `model()`: Load predefined primitive models by name
- Built-in primitives: "P_Cube", "P_Sphere_Medium", "P_Sphere_Small", "suzanne"

```java
// Load a built-in primitive model
Model cubeModel = LoadModel.model("P_Cube");

// Load a custom OBJ model
Model customModel = LoadModel.loadOBJModel("/models/character.obj");
```

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
- First person perspective
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

### Creating Tools

1. **Create ReUsable and improvable tools**: Think of the future of these tools while creating them. Avoid developing unique systems for simple things. Instead, build on existing features before adding to the pile so to speak.
2. **Build tools for dumb people**: When you create tools and methods for repeated use, you should attempt to create them usable by less technically inclined, sleep deprived, engineers. Simple and easy to comprehend.
3. **Reuasable**: If you make a function and only use it once, it shouldnt need its own class. Keep methods to their own class and try to build on currently existing classes if the subject matter is similar. Only create new classes if its a completely foreign topic that hasn'e been implemented yet.

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

1. **Use the Player class for first person games**: The Player class provides a complete character controller with physics-based movement.
2. **Scale movement by delta time**: Use the provided update method with proper delta time to ensure consistent movement across different frame rates.
3. **Use free camera mode for development**: Toggle free camera mode during development to easily navigate and debug scenes.
4. **Customize physics parameters**: Modify player constants to adjust movement feel (inertia, maximum velocity, etc.)

## Future Development

The j3D engine has several planned areas for future enhancement:

### Scene Management Improvements

- Scene transition system
- Hierarchical scene graph
- Improved entity organization
- Dynamic loading/unloading of scenes

### Rendering Enhancements

- Multiple shader support for different material types
- Post-processing effects pipeline
- Shadow mapping system
- Instanced rendering for better performance
- Level of Detail (LOD) system

### Physics Integration

- Enhanced collision detection beyond simple ground collision
- Rigid body physics system
- Constraint systems for joints and connections
- Advanced character controllers with slope handling

### Asset Management

- Centralized resource cache with reference counting
- Asynchronous loading system
- Automatic resource optimization
- Model and texture compression support

### User Interface Improvements

- Layout management system
- Animation and transition effects for UI elements
- Rich text rendering support
- Input focus and navigation system

### Audio System

- 3D positional audio
- Audio streaming for music and large sound files
- Audio effects and filters
- Dynamic audio mixing

### Networking Support

- Client-server architecture
- Entity synchronization
- Network prediction and lag compensation
- Multiplayer framework

### Development Tools

- In-engine scene editor
- Real-time asset hot-reloading
- Performance profiler
- Visual debugging tools

## Engine Internals

### Game Loop Implementation

The j3D engine implements a fixed timestep game loop which ensures consistent physics calculations and frame-independent gameplay mechanics.

```java
// Main game loop in EngineManager
public void run() {
    isRunning = true;

    int frames = 0;
    long frameCounter = 0;
    long lastTime = System.nanoTime();
    double unprocessedTime = 0.0;

    // Main game loop
    while(isRunning) {
        boolean render = false;
        long startTime = System.nanoTime();
        long passedTime = startTime - lastTime;
        lastTime = startTime;

        // Accumulate time since last frame
        unprocessedTime += passedTime / (double) NANOSECOND;
        frameCounter += passedTime;

        // Always process input
        input();

        // Update game state at fixed intervals
        while(unprocessedTime > frameTime) {
            render = true;
            unprocessedTime -= frameTime;

            // Handle window close request
            if (window.windowShouldClose()) {
                stop();
            }
        }

        // Render if needed
        if(render) {
            update();
            render();
            frames++;
        }
    }

    cleanup();
}
```

**Key Benefits:**

- **Frame-Independent Physics**: Calculations remain consistent regardless of the frame rate, preventing physics anomalies on different hardware
- **Predictable Gameplay**: Game logic operates at fixed time steps, ensuring deterministic behavior
- **Efficient Rendering**: Rendering occurs only when necessary, conserving processing power
- **Smooth Motion**: Objects move smoothly even when frame rates fluctuate

### Memory Management

The j3D engine implements careful memory management practices to optimize performance and prevent leaks.

#### Buffer Management

j3D uses direct memory buffers for efficient communication with the GPU:

```java
// Creating a buffer for vertex data
FloatBuffer vertexBuffer = Utils.storeDataInFloatBuffer(vertices);
GL15.glBufferData(GL15.GL_ARRAY_BUFFER, vertexBuffer, GL15.GL_STATIC_DRAW);
MemoryUtil.memFree(vertexBuffer);  // Explicitly free the buffer memory
```

**Key Concepts:**

- **Direct Buffers**: Used for vertex data, indices, and uniform variables
- **Manual Memory Management**: Resources are explicitly freed when no longer needed
- **Buffer Pooling**: For frequently allocated buffers to reduce garbage collection pressure

#### Resource Lifecycle

All engine resources follow a clear lifecycle to prevent memory leaks:

1. **Allocation**: Resources are allocated during initialization or when first needed
2. **Usage**: Resources are used for rendering or processing
3. **Cleanup**: Resources are explicitly freed when no longer needed

```java
// Example cleanup method from RenderManager
public void cleanup() {
    if (shader != null) {
        shader.cleanup();
    }
}
```

### Coordinate System

j3D uses a right-handed coordinate system with the following conventions:

- **X axis**: Positive to the right
- **Y axis**: Positive upward
- **Z axis**: Positive toward the viewer (out of the screen)

**Standard Units:**

- **Distance**: 1 unit roughly corresponds to 1 meter
- **Rotation**: Measured in radians
- **Time**: Measured in seconds

### Asset Pipeline

The asset pipeline defines how external resources are imported, processed, and accessed.

#### Supported Asset Types

- **3D Models**: OBJ format with material support
- **Textures**: PNG, JPEG, and TGA formats with automatic mipmapping
- **Shaders**: GLSL vertex and fragment shaders

#### Asset Loading Process

```java
// Loading a model with the ObjectLoader
Model model = objectLoader.loadOBJModel("/models/character.obj");

// Loading a texture
Texture texture = textureLoader.loadTexture("/textures/character_diffuse.png");

// Applying the texture to the model
Entity character = new Entity(model, position, rotation, scale);
character.getModel().setTexture(texture);
```

## Rendering System

### Shader Architecture

j3D uses GLSL shaders for flexible, GPU-accelerated rendering. The ShaderManager class provides an interface to compile, link, and use shader programs.

#### Vertex Shader

The vertex shader transforms vertex positions from model space to clip space and passes other attributes to the fragment shader.

```glsl
// Basic vertex shader (vertex.glsl)
#version 330

layout (location=0) in vec3 position;
layout (location=1) in vec2 texCoord;
layout (location=2) in vec3 normal;

out vec2 outTexCoord;
out vec3 outNormal;
out vec3 outFragPos;

uniform mat4 transformationMatrix;
uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;

void main()
{
    // Transform vertex position to world space
    vec4 worldPos = transformationMatrix * vec4(position, 1.0);
    
    // Output position in clip space
    gl_Position = projectionMatrix * viewMatrix * worldPos;
    
    // Pass texture coordinates to fragment shader
    outTexCoord = texCoord;
    
    // Pass normal to fragment shader
    outNormal = normalize(mat3(transformationMatrix) * normal);
    
    // Pass fragment position for lighting calculations
    outFragPos = worldPos.xyz;
}
```

#### Fragment Shader

The fragment shader determines the final color of each pixel, handling texturing and lighting calculations.

```glsl
// Basic fragment shader (fragment.glsl)
#version 330

in vec2 outTexCoord;
in vec3 outNormal;
in vec3 outFragPos;

out vec4 fragColor;

uniform sampler2D textureSampler;
uniform vec3 lightPosition;
uniform vec3 lightColor;
uniform float ambientLight;
uniform vec3 cameraPosition;
uniform bool useTransparency;

void main()
{
    // Sample texture color
    vec4 textureColor = texture(textureSampler, outTexCoord);
    
    // Handle transparency if enabled
    if (useTransparency && textureColor.a < 0.5) {
        discard;
    }
    
    // Calculate lighting
    vec3 normal = normalize(outNormal);
    vec3 lightDir = normalize(lightPosition - outFragPos);
    
    // Diffuse lighting
    float diff = max(dot(normal, lightDir), 0.0);
    vec3 diffuse = diff * lightColor;
    
    // Ambient lighting
    vec3 ambient = ambientLight * lightColor;
    
    // Calculate specular lighting
    vec3 viewDir = normalize(cameraPosition - outFragPos);
    vec3 reflectDir = reflect(-lightDir, normal);
    float spec = pow(max(dot(viewDir, reflectDir), 0.0), 32);
    vec3 specular = 0.5 * spec * lightColor;
    
    // Combine lighting with texture color
    vec3 result = (ambient + diffuse + specular) * textureColor.rgb;
    fragColor = vec4(result, textureColor.a);
}
```

#### Using Custom Shaders

The ShaderManager provides a flexible interface for creating and using custom shaders:

```java
// Creating and initializing a shader program
ShaderManager shader = new ShaderManager();
shader.createVertexShader(Utils.loadResource("/shaders/custom_vertex.glsl"));
shader.createFragmentShader(Utils.loadResource("/shaders/custom_fragment.glsl"));
shader.link();

// Creating uniform variables
shader.createUniform("transformationMatrix");
shader.createUniform("viewMatrix");
shader.createUniform("projectionMatrix");

// Using the shader program
shader.bind();
shader.setUniform("transformationMatrix", transformationMatrix);
// ... set other uniforms and render
shader.unbind();
```

### Material System

Materials in j3D define how surfaces appear when rendered, encapsulating properties like texture, color, and light response.

#### Basic Material Properties

- **Diffuse Texture**: The base color texture of the surface
- **Ambient Factor**: How much ambient light the material reflects
- **Diffuse Factor**: How much diffuse light the material reflects
- **Specular Factor**: How much specular light the material reflects
- **Shininess**: How concentrated specular highlights appear

```java
// Creating a material with custom properties
Material material = new Material();
material.setTexture(textureLoader.loadTexture("/textures/metal.png"));
material.setAmbientFactor(0.1f);
material.setDiffuseFactor(0.8f);
material.setSpecularFactor(1.0f);
material.setShininess(32.0f);

// Applying the material to an entity
entity.setMaterial(material);
```

### Rendering Pipeline

The rendering pipeline processes scenes in stages to ensure efficient and correct rendering.

#### Pipeline Stages

1. **Preparation**: Clear buffers and set up rendering state
2. **Scene Setup**: Apply camera and global settings
3. **Sorting**: Sort entities by transparency and distance
4. **Opaque Pass**: Render solid objects front-to-back
5. **Transparent Pass**: Render transparent objects back-to-front
6. **Post-Processing**: Apply any global effects (future enhancement)
7. **UI Rendering**: Render 2D user interface elements

#### Transparent Object Handling

Transparent objects require special handling to render correctly:

```java
// Mark an entity as having a transparent texture
entity.setHasTransparentTexture(true);

// In the render method, transparent entities are sorted and rendered last
if (entity.hasTransparentTexture()) {
    transparentEntities.add(entity);
} else {
    // Render opaque entity immediately
    renderEntity(entity, camera, light);
}

// After all opaque entities, sort transparent entities by distance
transparentEntities.sort(Comparator.comparingDouble(e -> 
    Vector3f.distanceSquared(e.getPosition(), camera.getPosition())));

// Render transparent entities from back to front
Collections.reverse(transparentEntities);
for (Entity entity : transparentEntities) {
    renderEntity(entity, camera, light);
}
```

## Lighting System

The j3D engine supports a flexible lighting system to create realistic scenes.

### Light Types

#### Point Light

Point lights emit light in all directions from a single position, simulating light bulbs, lanterns, etc.

```java
// Create a point light at a specific position
Light pointLight = new Light(
    new Vector3f(5.0f, 3.0f, 2.0f),  // position
    new Vector3f(1.0f, 0.9f, 0.8f),  // color (warm white)
    0.1f  // ambient intensity
);

// Add the light to the scene
scene.setLight(pointLight);
```

#### Directional Light (Future Implementation)

Directional lights simulate distant light sources like the sun, where all light rays are considered parallel.

### Ambient Lighting

Ambient lighting provides a base level of illumination to prevent completely dark areas.

```java
// Set ambient light intensity for a scene
light.setAmbientIntensity(0.2f);
```

### Specular Highlights

Specular highlights create shiny reflections on surfaces based on the view angle.

## Debug Tools

The j3D engine includes several debugging tools to help identify and resolve issues.

### Visual Debugging

#### Wireframe Mode

Toggle wireframe rendering to inspect mesh structure:

```java
// Toggle wireframe mode in the game logic class
if (window.isKeyPressedBuffered(GLFW.GLFW_KEY_APOSTROPHE)) {
    wireframeMode = !wireframeMode;
    GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, wireframeMode ? GL11.GL_LINE : GL11.GL_FILL);
    if (wireframeMode) {
        GL11.glDisable(GL11.GL_CULL_FACE);
    } else {
        GL11.glEnable(GL11.GL_CULL_FACE);
    }
}
```

#### Debug HUD

The debug HUD displays real-time information about the game state:

```java
// Create and configure the debug HUD
debugHUD = new DebugHUD(window);

// Update debug HUD information
debugHUD.addInfo("FPS", String.valueOf(currentFps));
debugHUD.addInfo("Position", player.getPosition().toString());
debugHUD.addInfo("Camera Rot", player.getCamera().getRotation().toString());
```

**Available Information:**

- **FPS**: Current frames per second
- **Position**: Player or camera position coordinates
- **Rotation**: Camera rotation angles
- **Velocity**: Player movement velocity
- **Custom Values**: Any game-specific debug information

### Performance Monitoring

Monitor performance to identify bottlenecks:

```java
// Get current FPS from the engine manager
int currentFps = (int) EngineManager.getFps();

// Log when FPS drops below a threshold
if (currentFps < 30) {
    System.out.println("Performance warning: Low FPS detected");
}
```

## User Interface System

j3D provides a simple UI system for creating game interfaces.

### UI Components

#### Panels

Panels are rectangular UI elements that can contain text or images.

```java
// Create a panel with position, size, and color
Panel panel = new Panel(
    new Vector2f(0.5f, 0.5f),  // center position (normalized 0-1)
    new Vector2f(0.4f, 0.3f),  // size (normalized 0-1)
    new Vector4f(0.2f, 0.2f, 0.2f, 0.8f)  // color (RGBA)
);

// Add the panel to the UI manager
uiManager.addElement(panel);
```

#### Menu System

Create interactive menus for game navigation:

```java
// Create a menu
Menu mainMenu = new Menu();

// Add buttons to the menu
mainMenu.addButton("Start Game", () -> startGame());
mainMenu.addButton("Options", () -> showOptions());
mainMenu.addButton("Exit", () -> exitGame());

// Show the menu
uiManager.addElement(mainMenu);
```

### Handling UI Input

UI elements can respond to mouse clicks and keyboard input:

```java
// In the UI element's update method
public void update(WindowManager window) {
    if (isMouseOver(window.getMouseX(), window.getMouseY())) {
        if (window.isMouseButtonPressed(GLFW.GLFW_MOUSE_BUTTON_1)) {
            onClick();
        }
    }
}
```

## Physics and Collision

The j3D engine includes basic physics and collision detection systems.

### Physics-Based Movement

The Player class implements physics-based movement with momentum and inertia:

```java
// In the Player.update() method
// Apply movement input to velocity with inertia - frame rate independent
float frameInertia = (float) Math.pow(INERTIA_FACTOR, deltaTime * 60.0f);
            
// Apply horizontal movement
velocity.x = velocity.x * frameInertia + movementInput.x * MOVEMENT_SPEED;
velocity.z = velocity.z * frameInertia + movementInput.z * MOVEMENT_SPEED;
```

**Key Physics Parameters:**

- **Movement Speed**: Base movement velocity
- **Inertia Factor**: How quickly the player accelerates and decelerates (0-1)
- **Gravity**: Downward acceleration for jumping and falling
- **Max Velocity**: Maximum movement speed cap

### Collision Detection (Example Implementation)

> **Status**: Not implemented in j3D - Example code only

j3D supports several collision detection techniques:

#### Bounding Volumes (Example Implementation)

> **Status**: Not implemented - Example code only

Simplified shapes that approximate the entity for faster collision detection:

```java
// Create a bounding sphere for an entity
BoundingSphere sphere = new BoundingSphere(
    entity.getPosition(),  // center
    2.0f  // radius
);

// Check for collision with another sphere
if (sphere.intersects(otherSphere)) {
    // Handle collision
}
```

#### Ray Casting (Example Implementation)

> **Status**: Not implemented - Example code only

Ray casting can detect collisions along a line, useful for selections and weapons:

```java
// Create a ray from the camera position in the view direction
Ray ray = new Ray(
    camera.getPosition(),  // origin
    camera.getViewDirection()  // direction
);

// Test for intersection with entities
for (Entity entity : scene.getEntities()) {
    if (ray.intersects(entity.getBoundingVolume())) {
        // Entity was hit by the ray
        handleRayHit(entity);
    }
}
```

## Audio System (Example Implementation)

> **Status**: Not implemented in j3D - Example code only

The j3D engine includes a basic audio system for game sound effects and music.

### Audio Components (Example Implementation)

> **Status**: Not implemented - Example code only

#### Sound Sources (Example Implementation)

> **Status**: Not implemented - Example code only

Sound sources represent individual sounds that can be played in the game:

```java
// Create a sound source
SoundSource soundSource = new SoundSource("explosion.wav");
soundSource.setPosition(entity.getPosition());  // 3D position
soundSource.setGain(0.8f);  // volume
soundSource.setLooping(false);  // play once

// Play the sound
soundSource.play();

// Update position in the game loop
soundSource.setPosition(entity.getPosition());
```

#### Audio Listener (Example Implementation)

> **Status**: Not implemented - Example code only

The audio listener represents the player's position and orientation for 3D audio:

```java
// Update the audio listener position and orientation to match the camera
audioManager.setListenerPosition(camera.getPosition());
audioManager.setListenerOrientation(camera.getViewDirection(), camera.getUpVector());
```

## Terrain System (Mixed Implementation)

> **Note**: j3D has a basic terrain system implemented, but the examples below show enhanced features that are not yet implemented.

### Height Map Terrain (Example Enhancement)

> **Status**: Enhanced version - Example code only

Height map terrains use a 2D grid of height values to define the surface:

```java
// Create a terrain from a height map image
Terrain terrain = new Terrain(
    0, 0,  // grid position
    128,  // size
    16,  // max height
    "heightmap.png"  // height map image
);

// Add the terrain to the scene
scene.addTerrain(terrain);
```

### Terrain Texturing (Example Enhancement)

> **Status**: Not implemented - Example code only

Multiple textures can be blended on terrain based on height and slope:

```java
// Add textures to the terrain
terrain.addTexture("grass.png", 0.0f, 0.3f);  // low areas
terrain.addTexture("rock.png", 0.3f, 0.7f);  // mid areas
terrain.addTexture("snow.png", 0.7f, 1.0f);  // high areas
```

## Development Tools (Example Implementation)

> **Status**: Not implemented in j3D - Example code only

### Profiling and Optimization (Example Implementation)

> **Status**: Not implemented - Example code only

The j3D engine includes tools to help optimize game performance:

```java
// Start profiling a section of code
Profiler.start("Physics Update");

// Run the code to profile
physicsSystem.update(deltaTime);

// End profiling and record the time
Profiler.end("Physics Update");

// Later, retrieve profiling results
float physicsTime = Profiler.getAverageTime("Physics Update");
System.out.println("Physics update time: " + physicsTime + "ms");
```

### Saving and Loading Game State (Example Implementation)

> **Status**: Not implemented - Example code only

Save and restore game state for persistence between sessions:

```java
// Save game state to a file
SaveManager.saveGameState("savegame.dat", gameState);

// Load game state from a file
GameState loadedState = SaveManager.loadGameState("savegame.dat");
```

## Advanced Development Guide

For developers looking to extend j3D or add advanced features to their games.

> **Important Note**: The following sections contain example implementations and theoretical code that demonstrate how various advanced features could be added to the j3D engine. These features are **not currently implemented** in the base engine and are provided as educational examples and starting points for custom development.

### Custom Entity Creation (Example Implementation)

> **Status**: Example code - demonstrates how to extend the existing Entity class

Creating custom entity types allows you to encapsulate specific behaviors and appearance:

```java
/**
 * A specialized entity for character representation.
 */
public class CharacterEntity extends Entity {
    
    /** Character health points */
    private float health;
    
    /** Character movement speed */
    private float speed;
    
    /**
     * Creates a new character entity.
     * 
     * @param model The 3D model
     * @param position Initial position
     * @param rotation Initial rotation
     * @param scale Size scaling
     * @param health Initial health
     * @param speed Movement speed
     */
    public CharacterEntity(Model model, Vector3f position, Vector3f rotation, 
                          Vector3f scale, float health, float speed) {
        super(model, position, rotation, scale);
        this.health = health;
        this.speed = speed;
    }
    
    /**
     * Updates the character state.
     * 
     * @param deltaTime Time elapsed since last update
     */
    public void update(float deltaTime) {
        // Custom character behavior here
    }
    
    /**
     * Applies damage to the character.
     * 
     * @param amount Damage amount
     * @return True if the character died from this damage
     */
    public boolean takeDamage(float amount) {
        health -= amount;
        return health <= 0;
    }
}
```

### Working with Scene Graph (Example Implementation)

> **Status**: Not implemented in j3D - Example code only

The scene graph system allows organizing entities in a logical hierarchy:

```java
// Create a parent entity
Entity parent = new Entity(parentModel, parentPosition, parentRotation, parentScale);

// Create child entities
Entity child1 = new Entity(childModel, childPosition, childRotation, childScale);
Entity child2 = new Entity(childModel, childPosition, childRotation, childScale);

// Attach children to parent
parent.addChild(child1);
parent.addChild(child2);

// When the parent transforms, children will transform relative to it
parent.setPosition(newPosition);  // Children move with parent
parent.setRotation(newRotation);  // Children rotate around parent
```

### Implementing Game State Management (Example Implementation)

> **Status**: Not implemented in j3D - Example code only

Game state management allows controlling the flow between different screens or modes:

```java
/**
 * Simple game state enum
 */
public enum GameState {
    MAIN_MENU,
    PLAYING,
    PAUSED,
    GAME_OVER
}

/**
 * State manager implementation
 */
public class StateManager {
    /** Current game state */
    private GameState currentState;
    
    /** Previous game state for returning from paused */
    private GameState previousState;
    
    /**
     * Sets a new game state.
     */
    public void setState(GameState newState) {
        if (newState == GameState.PAUSED) {
            // Remember the state we're pausing from
            previousState = currentState;
        }
        
        currentState = newState;
        
        // Update UI or other systems based on state
        updateStateDependent();
    }
    
    /**
     * Returns from paused state to previous state.
     */
    public void resumeFromPause() {
        if (currentState == GameState.PAUSED && previousState != null) {
            currentState = previousState;
            updateStateDependent();
        }
    }
    
    /**
     * Updates systems that depend on game state.
     */
    private void updateStateDependent() {
        switch (currentState) {
            case MAIN_MENU:
                showMainMenu();
                break;
            case PLAYING:
                hideAllMenus();
                enableGameplay();
                break;
            case PAUSED:
                showPauseMenu();
                disableGameplay();
                break;
            case GAME_OVER:
                showGameOverScreen();
                disableGameplay();
                break;
        }
    }
}
```

### Advanced Input Handling (Example Implementation)

> **Status**: Example code - extends the existing WindowManager input system

The input system supports complex mapping for different control schemes:

```java
/**
 * Input mapping for configurable controls
 */
public class InputMapper {
    /** Maps action names to key codes */
    private final Map<String, Integer> keyBindings;
    
    /** Maps action names to mouse buttons */
    private final Map<String, Integer> mouseBindings;
    
    /**
     * Creates a new input mapper with default bindings.
     */
    public InputMapper() {
        keyBindings = new HashMap<>();
        mouseBindings = new HashMap<>();
        
        // Default key bindings
        keyBindings.put("MOVE_FORWARD", GLFW.GLFW_KEY_W);
        keyBindings.put("MOVE_BACKWARD", GLFW.GLFW_KEY_S);
        keyBindings.put("MOVE_LEFT", GLFW.GLFW_KEY_A);
        keyBindings.put("MOVE_RIGHT", GLFW.GLFW_KEY_D);
        keyBindings.put("JUMP", GLFW.GLFW_KEY_SPACE);
        
        // Default mouse bindings
        mouseBindings.put("SHOOT", GLFW.GLFW_MOUSE_BUTTON_LEFT);
        mouseBindings.put("AIM", GLFW.GLFW_MOUSE_BUTTON_RIGHT);
    }
    
    /**
     * Checks if an action is triggered by a key press.
     */
    public boolean isActionTriggered(String actionName, WindowManager window) {
        Integer keyCode = keyBindings.get(actionName);
        if (keyCode != null) {
            return window.isKeyPressed(keyCode);
        }
        
        Integer mouseButton = mouseBindings.get(actionName);
        if (mouseButton != null) {
            return window.isMouseButtonPressed(mouseButton);
        }
        
        return false;
    }
    
    /**
     * Rebinds a keyboard action to a different key.
     */
    public void rebindKeyAction(String actionName, int newKeyCode) {
        keyBindings.put(actionName, newKeyCode);
    }
}
```

### Particle System Integration (Example Implementation)

> **Status**: Not implemented in j3D - Example code only

Particle systems can be implemented for effects like fire, smoke, or magic:

```java
/**
 * Basic particle system for visual effects
 */
public class ParticleSystem {
    /** List of active particles */
    private final List<Particle> particles;
    
    /** Particle emission rate (particles per second) */
    private float emissionRate;
    
    /** Particle model (typically a quad with texture) */
    private final Model particleModel;
    
    /** Particle texture */
    private final Texture particleTexture;
    
    /** Emission point */
    private Vector3f position;
    
    /**
     * Updates all particles and emits new ones.
     */
    public void update(float deltaTime) {
        // Emit new particles based on emission rate
        int newParticles = (int)(emissionRate * deltaTime);
        for (int i = 0; i < newParticles; i++) {
            emitParticle();
        }
        
        // Update existing particles
        Iterator<Particle> it = particles.iterator();
        while (it.hasNext()) {
            Particle particle = it.next();
            particle.update(deltaTime);
            
            // Remove dead particles
            if (particle.getLifetime() <= 0) {
                it.remove();
            }
        }
    }
    
    /**
     * Renders all particles.
     */
    public void render(Camera camera) {
        for (Particle particle : particles) {
            // Create a billboarded entity for the particle
            Entity particleEntity = new Entity(
                particleModel, 
                particle.getPosition(),
                new Vector3f(),  // rotation will be handled by billboarding
                new Vector3f(particle.getSize(), particle.getSize(), particle.getSize())
            );
            
            // Make the particle always face the camera
            particleEntity.setBillboardFull(true);
            
            // Render with transparency
            particleEntity.setHasTransparentTexture(true);
            
            // Render the particle
            renderer.render(particleEntity, camera, light);
        }
    }
    
    /**
     * Emits a single particle.
     */
    private void emitParticle() {
        // Create particle with random velocity and lifetime
        Vector3f velocity = new Vector3f(
            (float)Math.random() * 2 - 1,  // -1 to 1
            (float)Math.random() * 3,      // 0 to 3 (upward bias)
            (float)Math.random() * 2 - 1   // -1 to 1
        ).normalize().mul(getRandomSpeed());
        
        float lifetime = getRandomLifetime();
        float size = getRandomSize();
        
        Particle particle = new Particle(position, velocity, lifetime, size);
        particles.add(particle);
    }
}
```

### Custom Shader Effects (Example Implementation)

> **Status**: Example code - extends the existing ShaderManager system

Creating custom shader effects allows for unique visual styles:

```java
/**
 * Toon (cel) shader effect
 */
public class ToonShaderEffect {
    /** The shader program */
    private final ShaderManager shader;
    
    /**
     * Creates and initializes the toon shader.
     */
    public ToonShaderEffect() throws Exception {
        // Create shader program
        shader = new ShaderManager();
        shader.createVertexShader(Utils.loadResource("/shaders/toon_vertex.glsl"));
        shader.createFragmentShader(Utils.loadResource("/shaders/toon_fragment.glsl"));
        shader.link();
        
        // Create uniform variables
        shader.createUniform("transformationMatrix");
        shader.createUniform("projectionMatrix");
        shader.createUniform("viewMatrix");
        shader.createUniform("lightPosition");
        shader.createUniform("lightColor");
        shader.createUniform("textureSampler");
        shader.createUniform("numLevels");
    }
    
    /**
     * Renders an entity with the toon shader effect.
     */
    public void render(Entity entity, Camera camera, Light light) {
        // Bind the shader program
        shader.bind();
        
        // Set transformation uniforms
        Matrix4f transformationMatrix = Transformation.createTransformationMatrix(
            entity.getPosition(), 
            entity.getRotation(), 
            entity.getScale()
        );
        shader.setUniform("transformationMatrix", transformationMatrix);
        shader.setUniform("projectionMatrix", window.getProjectionMatrix());
        shader.setUniform("viewMatrix", Transformation.createViewMatrix(camera));
        
        // Set lighting uniforms
        shader.setUniform("lightPosition", light.getPosition());
        shader.setUniform("lightColor", light.getColor());
        
        // Set toon shader specific uniforms
        shader.setUniform("numLevels", 3.0f);  // Number of shading levels
        
        // Bind the texture
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, entity.getModel().getTexture().getId());
        
        // Render the mesh
        GL30.glBindVertexArray(entity.getModel().getId());
        GL20.glEnableVertexAttribArray(0);
        GL20.glEnableVertexAttribArray(1);
        GL20.glEnableVertexAttribArray(2);
        GL11.glDrawElements(GL11.GL_TRIANGLES, entity.getModel().getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
        GL20.glDisableVertexAttribArray(0);
        GL20.glDisableVertexAttribArray(1);
        GL20.glDisableVertexAttribArray(2);
        GL30.glBindVertexArray(0);
        
        // Unbind texture and shader
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
        shader.unbind();
    }
}
```

### Animation System

An animation system allows for skeletal or keyframe animations:

```java
/**
 * Keyframe animation system for entities
 */
public class AnimationSystem {
    /** Map of animation names to animation data */
    private final Map<String, Animation> animations;
    
    /** Currently playing animation */
    private Animation currentAnimation;
    
    /** Playback time in current animation */
    private float animationTime;
    
    /** Target entity to animate */
    private final Entity entity;
    
    /** Whether the animation should loop */
    private boolean looping;
    
    /**
     * Creates a new animation system for an entity.
     */
    public AnimationSystem(Entity entity) {
        this.entity = entity;
        this.animations = new HashMap<>();
        this.animationTime = 0;
    }
    
    /**
     * Adds an animation to the system.
     */
    public void addAnimation(String name, Animation animation) {
        animations.put(name, animation);
    }
    
    /**
     * Plays an animation.
     */
    public void playAnimation(String name, boolean loop) {
        Animation animation = animations.get(name);
        if (animation != null) {
            currentAnimation = animation;
            animationTime = 0;
            looping = loop;
        }
    }
    
    /**
     * Updates the animation system.
     */
    public void update(float deltaTime) {
        if (currentAnimation != null) {
            // Update animation time
            animationTime += deltaTime;
            
            // Handle looping
            if (animationTime > currentAnimation.getDuration()) {
                if (looping) {
                    animationTime %= currentAnimation.getDuration();
                } else {
                    animationTime = currentAnimation.getDuration();
                }
            }
            
            // Get keyframe data for current time
            Keyframe keyframe = currentAnimation.getKeyframeAtTime(animationTime);
            
            // Apply keyframe transformation to entity
            entity.setPosition(keyframe.getPosition());
            entity.setRotation(keyframe.getRotation());
            entity.setScale(keyframe.getScale());
        }
    }
}
```

### Networking Basics

Basic networking can be implemented for multiplayer games:

```java
/**
 * Simple networking client for multiplayer games
 */
public class NetworkClient {
    /** Socket connection to server */
    private Socket socket;
    
    /** Data input stream */
    private DataInputStream input;
    
    /** Data output stream */
    private DataOutputStream output;
    
    /** Client thread for receiving messages */
    private Thread clientThread;
    
    /** Running flag for the client thread */
    private boolean running;
    
    /**
     * Connects to a multiplayer server.
     */
    public void connect(String address, int port) throws IOException {
        socket = new Socket(address, port);
        input = new DataInputStream(socket.getInputStream());
        output = new DataOutputStream(socket.getOutputStream());
        
        running = true;
        clientThread = new Thread(this::receiveMessages);
        clientThread.start();
    }
    
    /**
     * Sends a player position update to the server.
     */
    public void sendPlayerPosition(Vector3f position, Vector3f rotation) throws IOException {
        output.writeInt(MessageType.PLAYER_POSITION);
        output.writeFloat(position.x);
        output.writeFloat(position.y);
        output.writeFloat(position.z);
        output.writeFloat(rotation.x);
        output.writeFloat(rotation.y);
        output.writeFloat(rotation.z);
        output.flush();
    }
    
    /**
     * Receives messages from the server.
     */
    private void receiveMessages() {
        try {
            while (running) {
                int messageType = input.readInt();
                
                switch (messageType) {
                    case MessageType.PLAYER_JOINED:
                        handlePlayerJoined();
                        break;
                    case MessageType.PLAYER_LEFT:
                        handlePlayerLeft();
                        break;
                    case MessageType.PLAYER_POSITION:
                        handlePlayerPosition();
                        break;
                    // Handle other message types
                }
            }
        } catch (IOException e) {
            disconnect();
        }
    }
    
    /**
     * Disconnects from the server.
     */
    public void disconnect() {
        running = false;
        try {
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            // Ignore close exceptions
        }
    }
}
```

## Engine Extension Guide (Example Implementations)

> **Status**: Not implemented in j3D - Example code only

### Creating Custom Components (Example Implementation)

> **Status**: Not implemented - Example code only

The component system can be extended to add new capabilities:

```java
/**
 * Health component that can be added to entities
 */
public class HealthComponent {
    /** Entity this component belongs to */
    private final Entity entity;
    
    /** Current health points */
    private float currentHealth;
    
    /** Maximum health points */
    private final float maxHealth;
    
    /** Whether the entity is currently invulnerable */
    private boolean invulnerable;
    
    /** Remaining invulnerability time */
    private float invulnerabilityTime;
    
    /**
     * Creates a new health component.
     */
    public HealthComponent(Entity entity, float maxHealth) {
        this.entity = entity;
        this.maxHealth = maxHealth;
        this.currentHealth = maxHealth;
        this.invulnerable = false;
        this.invulnerabilityTime = 0;
    }
    
    /**
     * Updates the health component.
     */
    public void update(float deltaTime) {
        if (invulnerable) {
            invulnerabilityTime -= deltaTime;
            if (invulnerabilityTime <= 0) {
                invulnerable = false;
            }
        }
    }
    
    /**
     * Applies damage to the entity.
     * 
     * @return True if the entity died from this damage
     */
    public boolean takeDamage(float damage) {
        if (invulnerable) {
            return false;
        }
        
        currentHealth -= damage;
        
        if (currentHealth <= 0) {
            // Entity died
            return true;
        } else {
            // Make temporarily invulnerable
            setInvulnerable(1.0f);
            return false;
        }
    }
    
    /**
     * Makes the entity invulnerable for a period of time.
     */
    public void setInvulnerable(float seconds) {
        invulnerable = true;
        invulnerabilityTime = seconds;
    }
    
    /**
     * Heals the entity.
     */
    public void heal(float amount) {
        currentHealth = Math.min(currentHealth + amount, maxHealth);
    }
}
```

### Custom Resource Loaders (Example Implementation)

> **Status**: Not implemented - Example code only

Custom resource loaders can be created for new asset types:

```java
/**
 * Loader for heightmap terrain data
 */
public class HeightMapLoader {
    /**
     * Loads height data from an image file.
     * 
     * @param imagePath Path to the heightmap image
     * @param maxHeight Maximum terrain height
     * @return 2D array of height values
     */
    public static float[][] loadHeightmap(String imagePath, float maxHeight) throws Exception {
        BufferedImage image = ImageIO.read(HeightMapLoader.class.getResourceAsStream(imagePath));
        int width = image.getWidth();
        int height = image.getHeight();
        
        float[][] heightData = new float[width][height];
        
        for (int z = 0; z < height; z++) {
            for (int x = 0; x < width; x++) {
                // Get pixel RGB and convert to grayscale
                int rgb = image.getRGB(x, z);
                int r = (rgb >> 16) & 0xFF;
                int g = (rgb >> 8) & 0xFF;
                int b = rgb & 0xFF;
                
                // Calculate grayscale value (0-255)
                float grayscale = (r + g + b) / 3.0f;
                
                // Convert to height value
                heightData[x][z] = (grayscale / 255.0f) * maxHeight;
            }
        }
        
        return heightData;
    }
    
    /**
     * Generates a terrain mesh from height data.
     */
    public static Model generateTerrainMesh(float[][] heightData, float cellSize) {
        int gridWidth = heightData.length;
        int gridHeight = heightData[0].length;
        
        // Calculate vertex and index counts
        int vertexCount = gridWidth * gridHeight;
        int indexCount = (gridWidth - 1) * (gridHeight - 1) * 6; // 2 triangles per grid cell
        
        // Create arrays for vertex data
        float[] positions = new float[vertexCount * 3];
        float[] textureCoords = new float[vertexCount * 2];
        float[] normals = new float[vertexCount * 3];
        int[] indices = new int[indexCount];
        
        // Fill vertex arrays
        // ... (vertex generation code)
        
        // Create the model
        return ObjectLoader.loadToVAO(positions, textureCoords, normals, indices);
    }
}
```

### Scene Serialization (Example Implementation)

> **Status**: Not implemented - Example code only

Scene serialization allows saving and loading scenes:

```java
/**
 * Serializes scene data to JSON for persistence
 */
public class SceneSerializer {
    /**
     * Serializes a scene to JSON format.
     */
    public static String serializeScene(TestScene scene) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        
        // Create a serializable scene data structure
        SceneData sceneData = new SceneData();
        
        // Copy basic scene properties
        sceneData.setName(scene.getName());
        
        // Convert light data
        Light light = scene.getLight();
        if (light != null) {
            LightData lightData = new LightData();
            lightData.setPosition(serializeVector3f(light.getPosition()));
            lightData.setColor(serializeVector3f(light.getColor()));
            lightData.setAmbientIntensity(light.getAmbientIntensity());
            sceneData.setLight(lightData);
        }
        
        // Convert entity data
        List<EntityData> entityDataList = new ArrayList<>();
        for (Entity entity : scene.getEntities()) {
            EntityData entityData = new EntityData();
            entityData.setModelName(entity.getModel().getName());
            entityData.setPosition(serializeVector3f(entity.getPosition()));
            entityData.setRotation(serializeVector3f(entity.getRotation()));
            entityData.setScale(serializeVector3f(entity.getScale()));
            
            // Copy entity properties
            entityData.setHasTransparentTexture(entity.hasTransparentTexture());
            entityData.setBillboardY(entity.isBillboardY());
            entityData.setBillboardFull(entity.isBillboardFull());
            
            entityDataList.add(entityData);
        }
        sceneData.setEntities(entityDataList);
        
        // Serialize to JSON
        return mapper.writeValueAsString(sceneData);
    }
    
    /**
     * Deserializes a scene from JSON format.
     */
    public static TestScene deserializeScene(String json, ObjectLoader loader) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        SceneData sceneData = mapper.readValue(json, SceneData.class);
        
        // Create a new scene
        TestScene scene = new TestScene();
        scene.setName(sceneData.getName());
        
        // Set the light
        if (sceneData.getLight() != null) {
            LightData lightData = sceneData.getLight();
            Light light = new Light(
                deserializeVector3f(lightData.getPosition()),
                deserializeVector3f(lightData.getColor()),
                lightData.getAmbientIntensity()
            );
            scene.setLight(light);
        }
        
        // Create entities
        for (EntityData entityData : sceneData.getEntities()) {
            // Load the model
            Model model = loader.loadOBJModel("/models/" + entityData.getModelName() + ".obj");
            
            // Create the entity
            Entity entity = new Entity(
                model,
                deserializeVector3f(entityData.getPosition()),
                deserializeVector3f(entityData.getRotation()),
                deserializeVector3f(entityData.getScale())
            );
            
            // Set entity properties
            entity.setHasTransparentTexture(entityData.isHasTransparentTexture());
            entity.setBillboardY(entityData.isBillboardY());
            entity.setBillboardFull(entityData.isBillboardFull());
            
            // Add to scene
            scene.addEntity(entity);
        }
        
        return scene;
    }
}
```

## Performance Optimization Techniques (Example Implementations)

> **Status**: Not implemented in j3D - Example code only

### Culling Techniques (Example Implementation)

> **Status**: Not implemented - Example code only

Visual culling improves performance by skipping the rendering of objects not visible to the camera:

#### Frustum Culling (Example Implementation)

> **Status**: Not implemented - Example code only

```java
/**
 * Frustum culling to skip rendering off-screen entities
 */
public class FrustumCuller {
    /** The view frustum planes */
    private final FrustumPlanes frustum;
    
    /**
     * Updates the frustum planes from camera and projection matrices.
     */
    public void updateFrustum(Matrix4f projectionMatrix, Matrix4f viewMatrix) {
        Matrix4f projView = new Matrix4f(projectionMatrix).mul(viewMatrix);
        frustum.update(projView);
    }
    
    /**
     * Tests if an entity is visible in the frustum.
     */
    public boolean isEntityVisible(Entity entity) {
        // Create a bounding sphere around the entity
        Vector3f center = entity.getPosition();
        float radius = entity.getMaxExtent();
        
        // Test sphere against all six frustum planes
        return frustum.isSphereInside(center, radius);
    }
}
```

#### Occlusion Culling (Example Implementation)

> **Status**: Not implemented - Example code only

```java
/**
 * Occlusion culling to skip rendering of entities hidden behind others
 */
public class OcclusionCuller {
    /** List of potential occluders (large objects) */
    private final List<Entity> occluders;
    
    /**
     * Tests if an entity is occluded.
     */
    public boolean isOccluded(Entity entity, Camera camera) {
        // Sort occluders by distance to camera (closest first)
        occluders.sort(Comparator.comparingDouble(e -> 
            Vector3f.distanceSquared(e.getPosition(), camera.getPosition())));
        
        // Test against each occluder
        for (Entity occluder : occluders) {
            if (occluder == entity) {
                continue; // Skip self
            }
            
            if (isEntityOccludedBy(entity, occluder, camera)) {
                return true;
            }
        }
        
        return false;
    }
}
```

### Level of Detail (LOD) (Example Implementation)

> **Status**: Not implemented - Example code only

LOD systems dynamically adjust model complexity based on distance:

```java
/**
 * Level of Detail system for optimizing rendering
 */
public class LODSystem {
    /** Distance thresholds for LOD levels */
    private final float[] thresholds;
    
    /** Models for each LOD level */
    private final Model[] lodModels;
    
    /**
     * Gets the appropriate LOD model for a given distance.
     */
    public Model getModelForDistance(float distance) {
        // Determine LOD level based on distance
        int lodLevel = 0;
        
        for (int i = 0; i < thresholds.length; i++) {
            if (distance < thresholds[i]) {
                break;
            }
            lodLevel = i + 1;
        }
        
        // Clamp to available models
        lodLevel = Math.min(lodLevel, lodModels.length - 1);
        
        return lodModels[lodLevel];
    }
}
```

### Batch Rendering (Example Implementation)

> **Status**: Not implemented - Example code only

Batch rendering reduces draw calls by combining similar objects:

```java
/**
 * Batch renderer for similar entities
 */
public class BatchRenderer {
    /** Map of model IDs to entity lists */
    private final Map<Integer, List<Entity>> entityBatches;
    
    /**
     * Adds an entity to the appropriate batch.
     */
    public void addEntity(Entity entity) {
        int modelId = entity.getModel().getId();
        
        if (!entityBatches.containsKey(modelId)) {
            entityBatches.put(modelId, new ArrayList<>());
        }
        
        entityBatches.get(modelId).add(entity);
    }
    
    /**
     * Clears all batches.
     */
    public void clearBatches() {
        entityBatches.clear();
    }
    
    /**
     * Renders all batched entities.
     */
    public void renderBatches(Camera camera, Light light) {
        for (Map.Entry<Integer, List<Entity>> entry : entityBatches.entrySet()) {
            int modelId = entry.getKey();
            List<Entity> entities = entry.getValue();
            
            // Bind the model VAO and texture only once for this batch
            Model model = entities.get(0).getModel();
            GL30.glBindVertexArray(model.getId());
            GL20.glEnableVertexAttribArray(0);
            GL20.glEnableVertexAttribArray(1);
            GL20.glEnableVertexAttribArray(2);
            
            GL13.glActiveTexture(GL13.GL_TEXTURE0);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, model.getTexture().getId());
            
            // Render each entity in the batch
            for (Entity entity : entities) {
                Matrix4f transformationMatrix = Transformation.createTransformationMatrix(
                    entity.getPosition(), 
                    entity.getRotation(), 
                    entity.getScale()
                );
                
                shader.setUniform("transformationMatrix", transformationMatrix);
                GL11.glDrawElements(GL11.GL_TRIANGLES, model.getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
            }
            
            // Unbind the model VAO
            GL20.glDisableVertexAttribArray(0);
            GL20.glDisableVertexAttribArray(1);
            GL20.glDisableVertexAttribArray(2);
            GL30.glBindVertexArray(0);
        }
    }
}
```

---

© 2025 DiscardSoft