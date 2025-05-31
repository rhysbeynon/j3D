# j3D Quick Start Guide

This guide will help you get started with the j3D engine quickly, covering basic setup and common development tasks.

## Table of Contents
1. [Setup](#setup)
2. [Creating a Basic Game](#creating-a-basic-game)
3. [Adding Entities](#adding-entities)
4. [Creating a Custom Scene](#creating-a-custom-scene)
5. [Working with Input](#working-with-input)
6. [Custom Shaders](#custom-shaders)

## Setup

### Prerequisites
- Java JDK 21 or higher
- Gradle build system
- An IDE like IntelliJ IDEA or Eclipse

### Build and Run

To build and run the engine:

```bash
# Clone the repository (if you haven't already)
git clone https://github.com/discardsoft/j3D.git
cd j3D

# Build the project
./gradlew build

# Run the example game
./gradlew run
```

## Creating a Basic Game

Create a new class that implements `IGameLogic`:

```java
package com.discardsoft.j3D.game;

import com.discardsoft.j3D.Main;
import com.discardsoft.j3D.core.*;
import com.discardsoft.j3D.core.entity.Camera;
import com.discardsoft.j3D.core.scene.TestScene;

public class MyGame implements IGameLogic {
    private final RenderManager renderer;
    private final ObjectLoader loader;
    private final WindowManager window;
    private TestScene scene;
    private Camera camera;

    public MyGame() {
        renderer = new RenderManager();
        window = Main.getWindow();
        loader = new ObjectLoader();
        camera = new Camera();
    }

    @Override
    public void init() throws Exception {
        renderer.init();
        scene = new TestScene();
        scene.initialize();
    }

    @Override
    public void input() {
        // Process input here
    }

    @Override
    public void update() {
        // Update game state here
        scene.update(0.016f); // Use actual delta time in real code
    }

    @Override
    public void render() {
        window.setClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        renderer.render(scene, camera);
    }

    @Override
    public void cleanup() {
        scene.cleanup();
        renderer.cleanup();
        loader.cleanup();
    }
}
```

Then modify `Main.java` to use your new game class:

```java
// In Main.java
game = new MyGame(); // Instead of TestGame
```

## Adding Entities

To add a new entity to your scene:

```java
// In your scene initialization
Model cubeModel = LoadModel.model("P_Cube"); // Use a built-in primitive
Entity cube = new Entity(
    cubeModel,
    new Vector3f(0.0f, 0.0f, -5.0f),  // position
    new Vector3f(0.0f, 0.0f, 0.0f),   // rotation
    new Vector3f(1.0f, 1.0f, 1.0f)    // scale
);
scene.addEntity(cube);
```

## Creating a Custom Scene

Create a new scene class:

```java
package com.discardsoft.j3D.game;

import com.discardsoft.j3D.core.scene.BaseScene;
import com.discardsoft.j3D.core.entity.*;
import com.discardsoft.j3D.core.utils.LoadModel;
import org.joml.Vector3f;

public class MyScene extends BaseScene {
    @Override
    public void initialize() {
        // Add a cube
        Model cubeModel = LoadModel.model("P_Cube");
        Entity cube = new Entity(
            cubeModel,
            new Vector3f(0.0f, 0.0f, -5.0f),
            new Vector3f(0.0f, 0.0f, 0.0f),
            new Vector3f(1.0f, 1.0f, 1.0f)
        );
        addEntity(cube);
        
        // Add a sphere
        Model sphereModel = LoadModel.model("P_Sphere_Medium");
        Entity sphere = new Entity(
            sphereModel,
            new Vector3f(3.0f, 0.0f, -5.0f),
            new Vector3f(0.0f, 0.0f, 0.0f),
            new Vector3f(0.5f, 0.5f, 0.5f)
        );
        addEntity(sphere);
        
        // Set the light position
        getLight().setPosition(5.0f, 5.0f, 5.0f);
    }
    
    @Override
    public void update(float deltaTime) {
        // Custom update logic here
        getEntities().forEach(entity -> {
            entity.incrementRotation(0.0f, 45.0f * deltaTime, 0.0f);
        });
    }
}
```

## Working with Input

Handle keyboard and mouse input:

```java
// In your game class
private final Vector3f cameraInc = new Vector3f();

@Override
public void input() {
    cameraInc.set(0, 0, 0);
    
    // Keyboard input
    if (window.isKeyPressed(GLFW.GLFW_KEY_W)) {
        cameraInc.z = -1;
    } else if (window.isKeyPressed(GLFW.GLFW_KEY_S)) {
        cameraInc.z = 1;
    }
    
    if (window.isKeyPressed(GLFW.GLFW_KEY_A)) {
        cameraInc.x = -1;
    } else if (window.isKeyPressed(GLFW.GLFW_KEY_D)) {
        cameraInc.x = 1;
    }
    
    if (window.isKeyPressed(GLFW.GLFW_KEY_SPACE)) {
        cameraInc.y = 1;
    } else if (window.isKeyPressed(GLFW.GLFW_KEY_LEFT_SHIFT)) {
        cameraInc.y = -1;
    }
    
    // Mouse input for camera rotation
    Vector2f mouseMovement = window.processMouseMovement();
    camera.rotateCamera(mouseMovement.x, mouseMovement.y);
}

@Override
public void update() {
    float moveSpeed = 0.05f;
    camera.movePosition(
        cameraInc.x * moveSpeed, 
        cameraInc.y * moveSpeed, 
        cameraInc.z * moveSpeed
    );
    
    scene.update(0.016f); // Use actual deltaTime in real implementation
}
```

## Custom Shaders

Create custom shader files in `src/main/resources/shaders/`:

**custom_vertex.vs**:
```glsl
#version 400 core

layout (location=0) in vec3 position;
layout (location=1) in vec2 textureCoord;
layout (location=2) in vec3 normal;

out vec3 fragPosition;
out vec3 fragNormal;
out vec2 fragTextureCoord;

uniform mat4 transformationMatrix;
uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;

void main() {
    vec4 worldPosition = transformationMatrix * vec4(position, 1.0);
    
    gl_Position = projectionMatrix * viewMatrix * worldPosition;
    
    fragPosition = worldPosition.xyz;
    fragNormal = mat3(transformationMatrix) * normal;
    fragTextureCoord = textureCoord;
}
```

**custom_fragment.fs**:
```glsl
#version 400 core

in vec3 fragPosition;
in vec3 fragNormal;
in vec2 fragTextureCoord;

out vec4 fragColor;

uniform sampler2D textureSampler;
uniform vec3 lightPosition;
uniform vec3 lightColor;
uniform vec3 ambientLight;
uniform vec3 cameraPosition;

void main() {
    // Ambient component
    vec3 ambient = ambientLight;
    
    // Diffuse component
    vec3 normalizedNormal = normalize(fragNormal);
    vec3 lightDir = normalize(lightPosition - fragPosition);
    float diff = max(dot(normalizedNormal, lightDir), 0.0);
    vec3 diffuse = diff * lightColor;
    
    // Specular component
    vec3 viewDir = normalize(cameraPosition - fragPosition);
    vec3 reflectDir = reflect(-lightDir, normalizedNormal); 
    float spec = pow(max(dot(viewDir, reflectDir), 0.0), 32);
    vec3 specular = 0.5 * spec * lightColor;
    
    // Combine lighting with texture
    vec4 textureColor = texture(textureSampler, fragTextureCoord);
    fragColor = vec4(ambient + diffuse + specular, 1.0) * textureColor;
}
```

Then in your rendering code:

```java
// In your custom renderer or game initialization
shader = new ShaderManager();
shader.createVertexShader(Utils.loadResource("/shaders/custom_vertex.vs"));
shader.createFragmentShader(Utils.loadResource("/shaders/custom_fragment.fs"));
shader.link();

// Create all necessary uniforms
shader.createUniform("textureSampler");
shader.createUniform("transformationMatrix");
shader.createUniform("projectionMatrix");
shader.createUniform("viewMatrix");
shader.createUniform("lightPosition");
shader.createUniform("lightColor");
shader.createUniform("ambientLight");
shader.createUniform("cameraPosition");
```

---

This quick start guide covers the basics of working with j3D. For more detailed information, refer to the main j3D manual.