// -|+++++++++++++++++++++++++++++++++++++++++++++++++++++++++|-
// -|              04/16/2025 | DISCVRD Software              |-
// -|        j3D is a lightweight custom-built engine         |-
// -|        Made with LWJGL, openGL, JOML, and other         |-
// -|        helpful libraries for use on DiscardSoft         |-
// -|              Engine Manager class for j3D               |-
// -|    Comments are always written above relevant context.  |-
// -|   ++++++++++++++++++++++++++++++++++++++++++++++++++    |-
// -|               Version: 0.04a In Development             |-
// -|   *some comments may be written by AI for convenience   |-
// -|+++++++++++++++++++++++++++++++++++++++++++++++++++++++++|-

package com.discardsoft.j3D.core;

import com.discardsoft.j3D.Main;
import com.discardsoft.j3D.core.utils.Consts;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;

/**
 * The game engine's main loop controller responsible for managing the game lifecycle.
 * <p>
 * This class coordinates initialization, game loop execution (input processing,
 * updating game state, rendering), and cleanup. It maintains timing for frame rate
 * control and FPS calculation.
 * </p>
 *
 * @author DISCVRD Software
 * @version 0.1
 */
public class EngineManager {

    /** Number of nanoseconds in one second */
    public static final long NANOSECOND = 1_000_000_000L;
    
    /** Target framerate */
    public static final float TARGET_FRAMERATE = 1000;
    
    /** Current frames per second value for display and monitoring */
    private static int fps;
    
    /** Time interval between frames in seconds (1/framerate) */
    private static float frameTime = 1.0f / TARGET_FRAMERATE;
    
    /** Flag indicating whether the engine is currently running */
    private boolean isRunning;
    
    /** The window management system reference */
    private WindowManager window;
    
    /** GLFW error callback handler */
    private GLFWErrorCallback errorCallback;
    
    /** Game logic implementation */
    private IGameLogic gameLogic;

    /**
     * Initializes the engine components.
     * <p>
     * Sets up error handling, retrieves window and game logic instances,
     * and initializes both the window system and game logic.
     * </p>
     *
     * @throws Exception If initialization fails
     */
    private void init() throws Exception {
        // Setup error callback for GLFW
        errorCallback = GLFWErrorCallback.createPrint(System.err);
        GLFW.glfwSetErrorCallback(errorCallback);
        
        // Retrieve window and game logic instances
        window = Main.getWindow();
        gameLogic = Main.getGame();
        
        // Initialize systems
        window.init();
        gameLogic.init();
    }

    /**
     * Starts the engine.
     * <p>
     * Initializes the engine and begins the main game loop if the engine
     * is not already running.
     * </p>
     *
     * @throws Exception If starting the engine fails
     */
    public void start() throws Exception {
        init();
        
        if(isRunning) {
            return;
        }
        
        run();
    }

    /**
     * Executes the main game loop.
     * <p>
     * Manages timing, input processing, state updates, and rendering at the
     * appropriate intervals. Continues until the engine is stopped.
     * </p>
     */
    public void run() {
        isRunning = true;

        // Variables for frame timing and FPS calculation
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
                
                // Calculate and display FPS once per second
                if (frameCounter >= NANOSECOND) {
                    setFps(frames);
                    window.setTitle(Consts.TITLE + " | " + Consts.VERSION + " | fps: " + fps);
                    frames = 0;
                    frameCounter = 0;
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

    /**
     * Stops the engine.
     * <p>
     * Sets the running flag to false, which will cause the game loop to exit.
     * </p>
     */
    private void stop() {
        if(!isRunning) {
            return;
        }
        isRunning = false;
    }

    /**
     * Processes input from the game logic.
     */
    private void input() {
        gameLogic.input();
    }

    /**
     * Updates the game state and renders the current frame.
     */
    private void render() {
        gameLogic.render();
        window.update();
    }

    /**
     * Updates the game state.
     */
    private void update() {
        gameLogic.update();
    }

    /**
     * Performs cleanup when the engine is shutting down.
     * <p>
     * Releases resources used by game logic, window system, and GLFW.
     * </p>
     */
    private void cleanup() {
        gameLogic.cleanup();
        window.cleanup();
        errorCallback.free();
        GLFW.glfwTerminate();
    }

    /**
     * Gets the current frames per second.
     *
     * @return The current FPS value
     */
    public static float getFps() {
        return fps;
    }

    /**
     * Sets the frames per second counter.
     *
     * @param fps The new FPS value
     */
    public static void setFps(int fps) {
        EngineManager.fps = fps;
    }
}
