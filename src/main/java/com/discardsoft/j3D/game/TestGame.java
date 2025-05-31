package com.discardsoft.j3D.game;

import com.discardsoft.j3D.Main;
import com.discardsoft.j3D.core.*;
import com.discardsoft.j3D.core.entity.Camera;
import com.discardsoft.j3D.core.entity.Entity;
import com.discardsoft.j3D.core.entity.Model;
import com.discardsoft.j3D.core.entity.Player;
import com.discardsoft.j3D.core.scene.TestScene;
import com.discardsoft.j3D.core.utils.DebugHUD;
import com.discardsoft.j3D.core.utils.Settings;
import org.joml.Vector3f;
import org.joml.Vector2f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

// Add the UI imports
import com.discardsoft.j3D.core.ui.UIManager;
import com.discardsoft.j3D.core.ui.PauseMenu;

// Add the level loading imports
import com.discardsoft.j3D.core.level.LevelLoader;

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
        
        // Load the scene from j3dl file instead of creating a hardcoded TestScene
        LevelLoader levelLoader = new LevelLoader(loader);
        String levelPath = "src/main/resources/levels/test_level.j3dl";
        LevelLoader.LoadedLevel loadedLevel = levelLoader.loadLevel(levelPath);
        
        // Set the loaded scene
        scene = loadedLevel.scene;
        
        // Create player at default spawn point (or at origin if no spawn points defined)
        Vector3f playerPos = new Vector3f(0.0f, 0.0f, 0.0f);
        if (loadedLevel.spawns != null && !loadedLevel.spawns.isEmpty()) {
            // Use the first spawn point
            playerPos = loadedLevel.spawns.get(0).position;
        }
        player = new Player(playerPos);
        
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
        
        // Level loading hotkeys (for testing)
        handleLevelLoadingInput();
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

        // set clear color to black
        window.setClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        
        // render the scene using the player's camera
        renderer.render(scene, player.getCamera());
        
        // render UI elements
        uiManager.render();
        
        // update current fps value from EngineManager
        currentFps = (int) EngineManager.getFps();
        
        // render debug HUD if enabled
        if (showDebugHUD && debugHUD != null) {
            debugHUD.render(currentFps, player, scene, player.isFreeCameraMode() ? currentCameraMoveSpeed : DEFAULT_CAMERA_MOVE_SPEED);
        }
    }

    @Override
    public void cleanup() {
        scene.cleanup();
        renderer.cleanup();
        loader.cleanup();
        
        // clean up UI resources
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
    
    /**
     * Handles level loading input for development testing.
     */
    private void handleLevelLoadingInput() {
        // Hotkey for loading a test level (L key)
        if (window.isKeyPressedBuffered(GLFW.GLFW_KEY_L)) {
            loadTestLevel();
        }
        
        // Hotkey for unloading the current level (U key)
        if (window.isKeyPressedBuffered(GLFW.GLFW_KEY_U)) {
            unloadCurrentLevel();
        }
        
        // F5 - Save current level
        if (window.isKeyPressedBuffered(GLFW.GLFW_KEY_F5)) {
            saveCurrentLevel();
        }
        
        // F6 - Load saved level
        if (window.isKeyPressedBuffered(GLFW.GLFW_KEY_F6)) {
            loadSavedLevel();
        }
        
        // F7 - Apply/reload current level
        if (window.isKeyPressedBuffered(GLFW.GLFW_KEY_F7)) {
            applyReloadLevel();
        }
    }
    
    /**
     * Loads a test level for development purposes.
     */
    private void loadTestLevel() {
        System.out.println("Loading test level...");
        // Example: Load a level from a file or create it procedurally
        // LevelLoader.loadLevel("path/to/level/file");
        
        // For now, let's just add a test entity to the scene
        try {
            Model cubeModel = com.discardsoft.j3D.core.utils.LoadModel.model("P_Cube");
            Entity testEntity = new Entity(
                cubeModel,
                new Vector3f(2.0f, 1.0f, -3.0f), // Position it near the Suzanne model
                new Vector3f(0.0f, 0.0f, 0.0f),   // No rotation
                new Vector3f(0.5f, 0.5f, 0.5f)    // Half scale
            );
            scene.addEntity(testEntity);
            System.out.println("Test entity added to scene.");
        } catch (Exception e) {
            System.err.println("Failed to load test entity: " + e.getMessage());
        }
    }
    
    /**
     * Unloads the current level, if any.
     */
    private void unloadCurrentLevel() {
        System.out.println("Unloading current level...");
        // Example: Remove all entities from the scene or reset the level
        scene.clearEntities();
    }
    
    /**
     * Saves the current level to a JSON file (F5 functionality).
     */
    private void saveCurrentLevel() {
        try {
            System.out.println("Saving current level...");
            
            // Create level loader instance
            LevelLoader levelLoader = new LevelLoader(loader);
            
            // Create LoadedLevel from current scene
            LevelLoader.LoadedLevel currentLevel = createLoadedLevelFromScene();
            
            // Define save path (you can customize this path as needed)
            String savePath = "saved_level.json";
            
            // Save the level
            levelLoader.saveLevel(currentLevel, savePath);
            
            System.out.println("Level saved successfully to: " + savePath);
            
        } catch (Exception e) {
            System.err.println("Failed to save level: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Loads a previously saved level from JSON file (F6 functionality).
     */
    private void loadSavedLevel() {
        try {
            System.out.println("Loading saved level...");
            
            // Create level loader instance
            LevelLoader levelLoader = new LevelLoader(loader);
            
            // Define load path (should match the save path)
            String loadPath = "saved_level.json";
            
            // Clear current scene first
            scene.clearEntities();
            
            // Load the level
            LevelLoader.LoadedLevel loadedLevel = levelLoader.loadLevel(loadPath);
            
            // Apply loaded entities to current scene
            if (loadedLevel.scene != null) {
                for (LevelLoader.LoadedEntity loadedEntity : loadedLevel.entities) {
                    scene.addEntity(loadedEntity.entity);
                }
                
                // Add terrain if it exists
                if (loadedLevel.terrain != null) {
                    scene.addEntity(loadedLevel.terrain);
                }
            }
            
            System.out.println("Level loaded successfully from: " + loadPath);
            
        } catch (Exception e) {
            System.err.println("Failed to load level: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Applies/reloads the current level (F7 functionality).
     * This reloads the current saved level, effectively resetting it.
     */
    private void applyReloadLevel() {
        try {
            System.out.println("Applying/reloading current level...");
            
            // Simply reload the saved level to reset the current state
            loadSavedLevel();
            
        } catch (Exception e) {
            System.err.println("Failed to apply/reload level: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Creates a LoadedLevel instance from the current scene state.
     * This is used for saving the current level.
     */
    private LevelLoader.LoadedLevel createLoadedLevelFromScene() {
        LevelLoader.LoadedLevel level = new LevelLoader.LoadedLevel();
        
        // Create metadata
        level.metadata = new LevelLoader.LevelMetadata();
        level.metadata.name = "Saved Level";
        level.metadata.description = "Level saved from j3D engine";
        level.metadata.version = "1.0";
        level.metadata.author = "j3D User";
        level.metadata.created = java.time.LocalDateTime.now().toString();
        level.metadata.modified = level.metadata.created;
        
        // Create scene with current light
        level.scene = new LevelLoader.LoadedScene(scene.getLight());
        
        // Convert current entities to LoadedEntity objects
        for (com.discardsoft.j3D.core.entity.Entity entity : scene.getEntities()) {
            // Create a basic JSON object for the entity (simplified)
            com.google.gson.JsonObject entityData = new com.google.gson.JsonObject();
            entityData.addProperty("model", "unknown"); // We don't have model name access, so use placeholder
            
            // Add position data
            com.google.gson.JsonObject position = new com.google.gson.JsonObject();
            position.addProperty("x", entity.getPosition().x);
            position.addProperty("y", entity.getPosition().y);
            position.addProperty("z", entity.getPosition().z);
            entityData.add("position", position);
            
            // Add rotation data
            com.google.gson.JsonObject rotation = new com.google.gson.JsonObject();
            rotation.addProperty("x", entity.getRotation().x);
            rotation.addProperty("y", entity.getRotation().y);
            rotation.addProperty("z", entity.getRotation().z);
            entityData.add("rotation", rotation);
            
            // Add scale data
            com.google.gson.JsonObject scale = new com.google.gson.JsonObject();
            scale.addProperty("x", entity.getScale().x);
            scale.addProperty("y", entity.getScale().y);
            scale.addProperty("z", entity.getScale().z);
            entityData.add("scale", scale);
            
            // Create LoadedEntity
            LevelLoader.LoadedEntity loadedEntity = new LevelLoader.LoadedEntity(entity, entityData);
            level.entities.add(loadedEntity);
        }
        
        return level;
    }
}
