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

// Add the UI imports
import com.discardsoft.j3D.core.ui.UIManager;
import com.discardsoft.j3D.core.ui.PauseMenu;

/**
 * Test implementation of game logic.
 * <p>
 * This class demonstrates how to use the j3D engine by creating a simple
 * test scene with various objects and lighting. It handles input processing,
 * updates, and rendering through the IGameLogic interface.
 * </p>
 *
 * @author DiscardSoft
 * @version 0.1
 */
public class TestGame implements IGameLogic {
    /** Flag for development mode features */
    private static final boolean DEV_MODE = Settings.DEV;
    
    /** Toggle for wireframe rendering mode */
    private boolean wireframeMode = false;
    
    /** Toggle for debug HUD display */
    private boolean showDebugHUD = DEV_MODE; // Now enabled by default in dev mode
    
    /** Debug HUD for displaying game information */
    private DebugHUD debugHUD;
    
    /** Current FPS for debug display */
    private int currentFps;

    /** Default speed multiplier for camera movement in free camera mode */
    private static final float DEFAULT_CAMERA_MOVE_SPEED = Settings.CAMERA_MOVE_SPEED;
    
    /** Current speed multiplier for camera movement in free camera mode */
    private float currentCameraMoveSpeed = DEFAULT_CAMERA_MOVE_SPEED;
    
    /** Increment value for camera speed adjustment */
    private static final float CAMERA_SPEED_INCREMENT = 0.01f;
    
    /** Minimum allowed camera speed */
    private static final float MIN_CAMERA_SPEED = 0.01f;
    
    /** Maximum allowed camera speed */
    private static final float MAX_CAMERA_SPEED = 0.5f;

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
    
    /** Flag to track if the game is paused */
    private boolean gamePaused;
    
    /** Time tracking for animation and physics */
    private float deltaTime;
    private long lastFrameTime;

    /** UI management system */
    private UIManager uiManager;
    
    /** Pause menu UI element */
    private PauseMenu pauseMenu;

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
        
        // Initialize UI system
        uiManager = new UIManager(window);
        
        // Create and configure pause menu
        pauseMenu = new PauseMenu();
        pauseMenu.setResumeAction(this::resumeGame);
        pauseMenu.hide(); // Start hidden
        uiManager.addElement(pauseMenu);
        
        // Initialize time tracking for animations
        lastFrameTime = System.currentTimeMillis();
        
        // Register window focus listener to automatically pause the game
        window.addWindowFocusListener(this::handleWindowFocus);
        
        // Register cursor capture listener to automatically pause/unpause the game
        window.addCursorCaptureListener(this::handleCursorCapture);
    }

    @Override
    public void input() {
        // Check for pause toggle with ESC key
        if (window.isKeyPressedBuffered(GLFW.GLFW_KEY_ESCAPE)) {
            togglePause();
        }
        
        // Skip movement input if cursor is not captured or game is paused
        if (!window.isCursorCaptured() || gamePaused) {
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

        // Handle scroll wheel for camera speed adjustment - only in free camera mode
        if (player.isFreeCameraMode() && window.hasScrolled()) {
            double scrollOffset = window.getScrollOffset();
            adjustCameraSpeed(scrollOffset);
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
        } else {
            // Jump when space is pressed and player is on the ground
            if (window.isKeyPressedBuffered(GLFW.GLFW_KEY_SPACE)) {
                player.jump();
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
     * Handles window focus changes.
     * Pauses the game when window loses focus.
     * 
     * @param focused true if window gained focus, false if it lost focus
     */
    private void handleWindowFocus(boolean focused) {
        if (!focused) {
            pauseGame();
            System.out.println("Game paused: Window lost focus");
        }
    }
    
    /**
     * Handles cursor capture changes.
     * Links cursor state to game pause state.
     * 
     * @param captured true if cursor is captured, false if it's released
     */
    private void handleCursorCapture(boolean captured) {
        if (captured) {
            resumeGame();
        } else {
            pauseGame();
        }
    }
    
    /**
     * Toggles the pause state of the game.
     * If paused, it will resume; if running, it will pause.
     */
    private void togglePause() {
        if (gamePaused) {
            resumeGame();
        } else {
            pauseGame();
        }
    }
    
    /**
     * Pauses the game and releases the cursor.
     */
    private void pauseGame() {
        gamePaused = true;
        window.releaseCursor();
        pauseMenu.show(); // Show the pause menu
        System.out.println("PAUSE");
    }
    
    /**
     * Resumes the game and captures the cursor.
     * This method can be called from a GUI "Resume" button.
     */
    public void resumeGame() {
        // Only try to resume if the window is focused
        if (window.isWindowFocused()) {
            window.captureCursor();
            gamePaused = false;
            pauseMenu.hide(); // Hide the pause menu
            
            // Reset the time tracking to prevent large delta time after resuming
            lastFrameTime = System.currentTimeMillis();
            System.out.println("RESUME");
        }
    }
    
    /**
     * Checks if the game is currently paused.
     * 
     * @return true if game is paused, false if game is running
     */
    public boolean isGamePaused() {
        return gamePaused;
    }
    
    /**
     * Adjusts the camera movement speed based on scroll wheel input.
     * 
     * @param scrollOffset The scroll wheel movement (positive for up, negative for down)
     */
    private void adjustCameraSpeed(double scrollOffset) {
        if (scrollOffset > 0) {
            // Scroll up - increase speed
            currentCameraMoveSpeed += CAMERA_SPEED_INCREMENT;
            if (currentCameraMoveSpeed > MAX_CAMERA_SPEED) {
                currentCameraMoveSpeed = MAX_CAMERA_SPEED;
            }
        } else if (scrollOffset < 0) {
            // Scroll down - decrease speed
            currentCameraMoveSpeed -= CAMERA_SPEED_INCREMENT;
            if (currentCameraMoveSpeed < MIN_CAMERA_SPEED) {
                currentCameraMoveSpeed = MIN_CAMERA_SPEED;
            }
        }
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
        
        // Update UI elements (even when paused)
        uiManager.update(window);
        
        // Skip updates if the game is paused
        if (gamePaused) {
            return;
        }
        
        // Update the player
        player.update(deltaTime);
        
        // Update camera position when in free camera mode
        if (player.isFreeCameraMode()) {
            Camera camera = player.getCamera();
            camera.movePosition(
                cameraInc.x * currentCameraMoveSpeed * deltaTime * 60.0f, 
                cameraInc.y * currentCameraMoveSpeed * deltaTime * 60.0f, 
                cameraInc.z * currentCameraMoveSpeed * deltaTime * 60.0f
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
        
        // Render UI elements
        uiManager.render();
        
        // Update current FPS value from EngineManager
        currentFps = (int) EngineManager.getFps();
        
        // Render debug HUD if enabled
        if (showDebugHUD && debugHUD != null) {
            debugHUD.render(currentFps, player, scene, player.isFreeCameraMode() ? currentCameraMoveSpeed : DEFAULT_CAMERA_MOVE_SPEED);
        }
    }

    @Override
    public void cleanup() {
        scene.cleanup();
        renderer.cleanup();
        loader.cleanup();
        
        // Clean up UI resources
        if (uiManager != null) {
            uiManager.cleanup();
        }
    }
    
    /**
     * Gets the player object.
     * <p>
     * This method provides access to the player entity, which contains
     * the camera used for rendering and billboarding calculations.
     * </p>
     *
     * @return The player entity
     */
    public Player getPlayer() {
        return player;
    }
}
