// -|+++++++++++++++++++++++++++++++++++++++++++++++++++++++++|-
// -|              04/16/2025 | DiscardSoft                   |-
// -|        j3D is a lightweight custom-built engine         |-
// -|        Made with LWJGL, openGL, JOML, and other         |-
// -|        helpful libraries for use on DiscardSoft         |-
// -|              IGameLogic interface for j3D               |-
// -|    Comments are always written above relevant context.  |-
// -|   ++++++++++++++++++++++++++++++++++++++++++++++++++    |-
// -|               Version: 0.05a In Development             |-
// -|   *some comments may be written by AI for convenience   |-
// -|+++++++++++++++++++++++++++++++++++++++++++++++++++++++++|-

package com.discardsoft.j3D.core;

/**
 * Interface defining the core game logic functionality.
 * <p>
 * This interface specifies the contract that any game implementation must follow
 * to operate within the engine's lifecycle. It handles the key aspects of game
 * execution including initialization, input processing, state updates, rendering,
 * and cleanup.
 * </p>
 *
 * @author DiscardSoft
 * @version 0.1
 */
public interface IGameLogic {

    /**
     * Initializes the game.
     * <p>
     * Called once during engine startup. Implementations should load resources,
     * create game objects, and set up the initial game state.
     * </p>
     *
     * @throws Exception If initialization fails
     */
    void init() throws Exception;

    /**
     * Processes input.
     * <p>
     * Called each frame to handle user input. Implementations should process
     * keyboard, mouse, and other input device events.
     * </p>
     */
    void input();

    /**
     * Updates game state.
     * <p>
     * Called each frame to update the game state. Implementations should update
     * object positions, physics, AI, and other game systems.
     * </p>
     */
    void update();

    /**
     * Renders the game.
     * <p>
     * Called each frame after update. Implementations should handle rendering
     * of all game objects and UI elements.
     * </p>
     */
    void render();

    /**
     * Cleans up resources.
     * <p>
     * Called when the engine is shutting down. Implementations should release
     * all resources to prevent memory leaks.
     * </p>
     */
    void cleanup();
}
