// -|+++++++++++++++++++++++++++++++++++++++++++++++++++++++++|-
// -|                     .--,-``-.                           |-
// -|                    /   /     '.      ,---,              |-
// -|                   / ../        ;   .'  .' `\            |-
// -|               .--.\ ``\  .`-    ',---.'     \           |- 
// -|             .--,`| \___\/   \   :|   |  .`\  |          |-
// -|             |  |.       \   :   |:   : |  '  |          |-
// -|             '--`_       /  /   / |   ' '  ;  :          |-
// -|             ,--,'|      \  \   \ '   | ;  .  |          |-
// -|             |  | '  ___ /   :   ||   | :  |  '          |-
// -|             :  | | /   /\   /   :'   : | /  ;           |-
// -|           __|  : '/ ,,/  ',-    .|   | '` ,/            |-
// -|         .'__/\_: |\ ''\        ; ;   :  .'              |-
// -|         |   :    : \   \     .'  |   ,.'                |-
// -|          \   \  /   `--`-,,-'    '---'                  |-
// -|           `--`-' 04/23/2025 | DiscardSoft               |-
// -|   ++++++++++++++++++++++++++++++++++++++++++++++++++    |-
// -|        j3D is a lightweight custom-built engine         |-
// -|        Made with LWJGL, openGL, JOML, and other         |-
// -|        helpful libraries for use on DiscardSoft         |-
// -|           Main class and entrypoint for j3D             |-
// -|    Comments are always written above relevant context.  |-
// -|   ++++++++++++++++++++++++++++++++++++++++++++++++++    |-
// -|               Version: 0.05a In Development             |-
// -|                                                         |-
// -|   *some comments may be written by AI for convenience   |-
// -|+++++++++++++++++++++++++++++++++++++++++++++++++++++++++|-

package com.discardsoft.j3D;

import com.discardsoft.j3D.core.EngineManager;
import com.discardsoft.j3D.core.WindowManager;
import com.discardsoft.j3D.core.utils.Consts;
import com.discardsoft.j3D.game.TestGame;
import org.lwjgl.Version;

/**
 * Entry point for the j3D engine application.
 * <p>
 * This class initializes the engine, creates the window and game instance,
 * and starts the main game loop. It also provides access to shared
 * window and game instances.
 * </p>
 * 
 * @author DiscardSoft
 * @version 0.1
 */
public class Main {

    /** The window management system instance */
    private static WindowManager window;
    
    /** The test game implementation instance */
    private static TestGame game;

    /**
     * Application entry point.
     * <p>
     * Initializes engine components and starts the game loop.
     * </p>
     *
     * @param args Command line arguments (not used)
     */
    public static void main(String[] args) {
        System.out.println("Starting j3D. (" + Consts.VERSION + ")");
        System.out.println("LWJGL Version: " + Version.getVersion());
        
        // Create window and game instances
        window = new WindowManager(Consts.TITLE, 800, 600);
        game = new TestGame();
        
        // Start the engine
        EngineManager engine = new EngineManager();
        try {
            engine.start();
        } catch (Exception e) {
            System.err.println("Error starting engine:");
            e.printStackTrace();
        }
    }

    /**
     * Gets the window manager instance.
     *
     * @return The window manager
     */
    public static WindowManager getWindow() {
        return window;
    }

    /**
     * Gets the game logic instance.
     *
     * @return The game implementation
     */
    public static TestGame getGame() {
        return game;
    }
}