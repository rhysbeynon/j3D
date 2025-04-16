// -|+++++++++++++++++++++++++++++++++++++++++++++++++++++++++|-
// -|              04/16/2025 | DISCVRD Software              |-
// -|        j3D is a lightweight custom-built engine         |-
// -|        Made with LWJGL, openGL, JOML, and other         |-
// -|        helpful libraries for use on DiscardSoft         |-
// -|              Engine Manager class for j3D               |-
// -|    Comments are always written above relevant context.  |-
// -|   ++++++++++++++++++++++++++++++++++++++++++++++++++    |-
// -|               Version: 0.1 In Development               |-
// -|   *some comments may be written by AI for convenience   |-
// -|+++++++++++++++++++++++++++++++++++++++++++++++++++++++++|-

package com.discardsoft.j3D.core;

import com.discardsoft.j3D.Main;
import com.discardsoft.j3D.core.utils.Consts;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;

/*
 * The EngineManager class is the main loop controller for the j3D engine.
 * It handles the initialization, game loop, and cleanup of the engine.
 * This class is responsible for ensuring that the game logic and rendering
 * are executed in a consistent and efficient manner.
 */
public class EngineManager {

    /*
    Variable initialization. These are consts and/or "pivot" variables
    as I like to call them. More info is above each one.
     */
    // Nanoseconds in every second (1 billion)
    public static final long NANOSECOND = 1000000000L;
    // Framerate set to 1000 by default
    public static final float FRAMERATE = 1000;
    // fps for displaying. This should update every frame.
    private static int fps;
    // frame time is the time between each frame.
    private static float frameTime = 1.0f / FRAMERATE;
    // is program running indicator
    private boolean isRunning;
    //reference to the Window Manager class for width, height, etc
    private WindowManager window;
    //Error callback for Engine class
    private GLFWErrorCallback errorCallback;
    //Use ILogic interface file as a game logic guideline
    private ILogic gameLogic;

    /*
    HERE LIES THE METHODS.
    These are important methods that are the spine (or trunk in flora terms) of j3D.
    Comments are available for more information on each method below.
     */

    //Engine.init() method. Ran upon startup to initialize GLFW and window manager.
    private void init() throws Exception {
        //this is similar to Window Manager's implementation
        GLFW.glfwSetErrorCallback(errorCallback = GLFWErrorCallback.createPrint(System.err));
        //use getter from Main class to get window instance
        window = Main.getWindow();
        //use getter for the game we would like to run.
        gameLogic = Main.getGame();
        //run init() methods for Game's Logic and Window Manager to create game window using GLFW
        window.init();
        gameLogic.init();
    }

    //This runs Init() and makes tells everything else the engine is now running.
    public void start() throws Exception {
        init();
        //checks if already running. If not, execute run();
        if(isRunning) {
            return;
        }
        run();
    }

    /*
    This is the special sauce method.
    This is full of local variables.
     */
    public void run() {
        //set running to true when ran.
        this.isRunning = true;

        /*
        These are local variables used to manage
        frame counting and timing.
        */
        int frames = 0;
        long frameCounter = 0;
        long lastTime = System.nanoTime();
        double unprocessedTime = 0.0;

        //while window is open and running,
        while(isRunning) {
            //set render to false until there is something new to render
            boolean render = false;
            //record start time
            long startTime = System.nanoTime();
            //calculate difference in frametime and keep it cyclable
            long passedTime = startTime - lastTime;
            lastTime = startTime;

            //count time between frame rendering and input proccessing
            unprocessedTime += passedTime / (double) NANOSECOND;
            frameCounter += passedTime;

            //run input method for the frame
            input();

            //while the duration since last update was ran is over the time between last frames,
            while(unprocessedTime > frameTime) {
                //render new frame
                render = true;
                //correct for newly proccessed frame
                unprocessedTime -= frameTime;

                //if windowShouldClose is true, run the stop() method
                if (window.windowShouldClose()) {
                    stop();
                }
                //if the frame counter is older than 1 second (1 billion nanoseconds),
                if (frameCounter >= NANOSECOND) {
                    //refresh fps counter
                    setFps(frames);
                    //change title to show fps counter
                    window.setTitle(Consts.TITLE + " | " + Consts.VERSION + " | fps: " + fps);
                    //reset both framerate variables for next update
                    frames = 0;
                    frameCounter = 0;
                }
            }

            //if we have given permission to render,
            if(render) {
                //run update method,
                update();
                //render new frame,
                render();
                //increment frames for fps calculations
                frames++;
            }
        }

        //when broken out from while loops, run cleanup method to gracefully exit
        cleanup();

    }

    //Polar opposite to Start(). Self explanatory.
    private void stop() {
        if(!isRunning) {
            return;
        }
        isRunning = false;
    }

    private void input() {
        gameLogic.input();
    }

    //use window manager to update the current canvas
    private void render() {
        gameLogic.render();
        window.update();
    }

    private void update() {
        gameLogic.update();
    }

    //gracefully cleanup and exit program
    private void cleanup() {
        gameLogic.cleanup();
        window.cleanup();
        errorCallback.free();
        GLFW.glfwTerminate();
    }

    //getter for fps counter
    public static float getFps() {
        return fps;
    }

    //setter for fps counter
    public static void setFps(int fps) {
        EngineManager.fps = fps;
    }
}
