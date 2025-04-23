package com.discardsoft.j3D.game;

import com.discardsoft.j3D.Main;
import com.discardsoft.j3D.core.*;
import com.discardsoft.j3D.core.entity.Camera;
import com.discardsoft.j3D.core.entity.Player;
import com.discardsoft.j3D.core.scene.TestScene;
import com.discardsoft.j3D.core.utils.DebugHUD;
import com.discardsoft.j3D.core.utils.Settings;
import com.discardsoft.j3D.core.utils.Consts;
import org.joml.Vector3f;
import org.joml.Vector2f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

/**
 * Test implementation of the game logic for demonstration and testing purposes.
 * <p>
 * Provides a simple 3D scene with a rotatable model, controllable camera, and basic lighting.
 * Implements player movement with WASD keys, momentum-based physics, and mouse look controls.
 * </p>
 * 
 * @author DISCVRD Software
 * @version 0.1
 */
public class TestGame implements IGameLogic {
    /** Flag for development mode features */
    private static final boolean DEV_MODE = Settings.DEV;
    
    /** Toggle for wireframe rendering mode */
    private boolean wireframeMode = false;
    
    /** Toggle for debug HUD display */
    private boolean showDebugHUD = false;
    
    /** Debug HUD for displaying game information */
    private DebugHUD debugHUD;
    
    /** Current FPS for debug display */
    private int currentFps;

    /** Speed multiplier for camera movement in free camera mode */
    private static final float CAMERA_MOVE_SPEED = Settings.CAMERA_MOVE_SPEED;

    /** Rendering system reference */
    private final RenderManager renderer;
    
    /** Resource loader reference */
    private final ObjectLoader loader;
    
    /** Window management system reference */
    private final WindowManager window;

    /** The test scene containing all game entities */
    private TestScene scene;
    
    /** The player entity */
    private Player player;

    /** Vector for camera movement increments (used in free camera mode) */
    private final Vector3f cameraInc;
    
    /** Time tracking for animation and physics */
    private float deltaTime;
    private long lastFrameTime;

    /**
     * Constructs a new TestGame instance.
     * <p>
     * Initializes rendering systems, window management, and camera.
     * </p>
     */
    public TestGame() {
        renderer = new RenderManager();
        window = Main.getWindow();
        loader = new ObjectLoader();
        cameraInc = new Vector3f(0.0f, 0.0f, 0.0f);
    }

    @Override
    public void init() throws Exception {
        // Initialize rendering system
        renderer.init();
        
        // Create and initialize the scene
        scene = new TestScene();
        scene.initialize();
        
        // Create player at origin
        player = new Player(new Vector3f(0.0f, 0.0f, 0.0f));
        
        // Add player's bounding entity to the scene
        scene.addEntity(player.getBoundingEntity());
        
        // Initialize debug HUD
        debugHUD = new DebugHUD(window.getWidth(), window.getHeight());
        
        // Initialize time tracking for animations
        lastFrameTime = System.currentTimeMillis();
    }

    @Override
    public void input() {
        // Check for cursor control toggles first
        if (window.isKeyPressedBuffered(GLFW.GLFW_KEY_ESCAPE)) {
            window.releaseCursor();
        } else if (window.isWaitingForClick() && window.isMouseButtonClicked()) {
            window.captureCursor();
        }
        
        // Skip movement input if cursor is not captured
        if (!window.isCursorCaptured()) {
            return;
        }
        
        // Reset camera movement vector for free camera mode
        cameraInc.set(0.0f, 0.0f, 0.0f);
        
        // Player movement input (default to zero)
        float forwardMovement = 0.0f;
        float sidewaysMovement = 0.0f;

        // Development mode controls
        if (DEV_MODE) {
            handleDevModeInput();
        }

        // Movement controls
        // Forward/backward movement (Z axis)
        if (window.isKeyPressed(GLFW.GLFW_KEY_W)) {
            if (player.isFreeCameraMode()) {
                cameraInc.z = -1.0f;
            } else {
                forwardMovement = 1.0f;
            }
        }
        if (window.isKeyPressed(GLFW.GLFW_KEY_S)) {
            if (player.isFreeCameraMode()) {
                cameraInc.z = 1.0f;
            } else {
                forwardMovement = -1.0f;
            }
        }
        
        // Left/right movement (X axis)
        if (window.isKeyPressed(GLFW.GLFW_KEY_A)) {
            if (player.isFreeCameraMode()) {
                cameraInc.x = -1.0f;
            } else {
                sidewaysMovement = -1.0f;
            }
        }
        if (window.isKeyPressed(GLFW.GLFW_KEY_D)) {
            if (player.isFreeCameraMode()) {
                cameraInc.x = 1.0f;
            } else {
                sidewaysMovement = 1.0f;
            }
        }
        
        // Up/down movement (Y axis - only in free camera mode)
        if (player.isFreeCameraMode()) {
            if (window.isKeyPressed(GLFW.GLFW_KEY_SPACE)) {
                cameraInc.y = 1.0f;
            }
            if (window.isKeyPressed(GLFW.GLFW_KEY_LEFT_SHIFT)) {
                cameraInc.y = -1.0f;
            }
        }
        
        // Send player movement input if not in free camera mode
        if (!player.isFreeCameraMode()) {
            player.setMovementInput(forwardMovement, sidewaysMovement);
        }
        
        // Camera rotation from mouse input (always active for look controls)
        handleCameraRotation();
    }
    
    /**
     * Handles development-specific input controls.
     */
    private void handleDevModeInput() {
        // Toggle wireframe mode
        if (window.isKeyPressedBuffered(GLFW.GLFW_KEY_APOSTROPHE)) {
            wireframeMode = !wireframeMode;
            GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, wireframeMode ? GL11.GL_LINE : GL11.GL_FILL);
            if (wireframeMode) {
                GL11.glDisable(GL11.GL_CULL_FACE);
            } else {
                GL11.glEnable(GL11.GL_CULL_FACE);
            }
        }
        
        // Toggle freecam
        if (window.isKeyPressedBuffered(GLFW.GLFW_KEY_C)) {
            player.toggleFreeCamera();
            System.out.println("Free camera mode: " + player.isFreeCameraMode());
        }
        
        // Toggle debug HUD with F3 key
        if (window.isKeyPressedBuffered(GLFW.GLFW_KEY_F3)) {
            showDebugHUD = !showDebugHUD;
            System.out.println("Debug HUD: " + (showDebugHUD ? "ON" : "OFF"));
        }
    }
    
    /**
     * Handles mouse input for camera rotation.
     */
    private void handleCameraRotation() {
        Vector2f mouseDelta = window.processMouseMovement();
        player.getCamera().rotateCamera(mouseDelta.x, mouseDelta.y);
    }

    @Override
    public void update() {
        // Calculate delta time for smooth animations
        long currentTime = System.currentTimeMillis();
        deltaTime = (currentTime - lastFrameTime) / 1000.0f;
        lastFrameTime = currentTime;
        
        // Update the player
        player.update(deltaTime);
        
        // Update camera position when in free camera mode
        if (player.isFreeCameraMode()) {
            Camera camera = player.getCamera();
            camera.movePosition(
                cameraInc.x * CAMERA_MOVE_SPEED * deltaTime * 60.0f, 
                cameraInc.y * CAMERA_MOVE_SPEED * deltaTime * 60.0f, 
                cameraInc.z * CAMERA_MOVE_SPEED * deltaTime * 60.0f
            );
        }

        // Update scene entities
        scene.update(deltaTime);
    }

    @Override
    public void render() {
        // Handle window resize if needed
        if(window.isResize()) {
            // Update viewport to match new window dimensions
            GL11.glViewport(0, 0, window.getWidth(), window.getHeight());
            // Force update the projection matrix
            window.updateProjectionMatrix();
            // Update debug HUD dimensions
            if (debugHUD != null) {
                debugHUD.updateScreenSize(window.getWidth(), window.getHeight());
            }
            window.setResize(false);  // Reset the resize flag
        }

        // Set window background to blue sky color
        window.setClearColor(0.3f, 0.5f, 1.0f, 1.0f);
        
        // Render the scene using the player's camera
        renderer.render(scene, player.getCamera());
        
        // Update current FPS value from EngineManager
        currentFps = (int) EngineManager.getFps();
        
        // Render debug HUD if enabled
        if (showDebugHUD && debugHUD != null) {
            debugHUD.render(currentFps, player, scene);
        }
    }

    @Override
    public void cleanup() {
        scene.cleanup();
        renderer.cleanup();
        loader.cleanup();
    }
}
