/***********************************************************|-
 -|              04/10/2025 | DISCVRD Software              |-
 -|        j3D is a lightweight custom-built engine         |-
 -|        Made with LWJGL, openGL, JOML, and other         |-
 -|        helpful libraries for use on DiscardSoft         |-
 -|              Window Manager class for j3D               |-
 -|    Comments are always written above relevant context.  |-
 -|   ++++++++++++++++++++++++++++++++++++++++++++++++++    |-
 -|               Version: 0.1 In Development               |-
 -|**********************************************************/

package com.discardsoft.j3D.core;

import org.joml.Matrix4f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryUtil;

public class WindowManager {

    //CONSTANT VARS for window management
    public static final float FOV = (float) Math.toRadians(60);
    public static final float Z_NEAR = 0.001f;
    public static final float Z_FAR = 10000f;

    private final String title;

    private int width, height; // width and height of window
    private long window; // unique, auto-generated window ID used by GLFW.

    private boolean resize; // is window resizable?
    private boolean Vsync; // does window use Vsync?

    private final Matrix4f projectionMatrix;

    public WindowManager(String title, int width, int height, boolean vsync) {
        this.title = title;
        this.width = width;
        this.height = height;
        Vsync = vsync;
        projectionMatrix = new Matrix4f();
    }

    public void init() {
        /*
        creates a callback for any error in initialization-
        to be printed in the "System.err.print" format.
        This is the optimal alternative to system.out.println for every edge case.
         */
        GLFWErrorCallback.createPrint(System.err).set();

        /*
        THIS PERFORMS "GLFW.glfwInit();" AND THROWS
        EXCEPTION IF THERE WAS A PROBLEM WITH IT.
        glfwInit(); RETURNS A BOOLEAN VALUE, AFTER
        INITIALIZING A FUNCTION ADDRESS.
         */
        if(!GLFW.glfwInit()) {
            throw new IllegalStateException("Problem starting GLFW using GLFW.glfwInit()");
        }


        /*
        GLFW requires window "hints" to create a window.
        These set things like: Is window resizable?, is Vsync used?, etc.
        Here I set some safe default values. These aren't mandatory
        as they fall back on defaults, but they help specify
        settings I would like changed. For instance, resizable is
        set to the resize bool we initialized earlier using a ternary operator.
         */
        GLFW.glfwDefaultWindowHints();
        GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_FALSE); // This just makes the window invisible at first, not forever.
        GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, resize ? GLFW.GLFW_TRUE : GLFW.GLFW_FALSE); // ternary operator to control resizability.
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, 3);
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, 2);
        GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_PROFILE, GLFW.GLFW_OPENGL_CORE_PROFILE);
        GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_FORWARD_COMPAT, GLFW.GLFW_TRUE);


        /*
        Logic for maximizing the window.
        We first set maximized to false by default. If the width and height are both 0, we consider
        the window as maximized. We set the width and height to a placeholder value of 100
        and get GLFW to use GLFW_MAXIMIZED.
        We also set maximized to true.
         */
        boolean maximized = false;
        if(width == 0 && height == 0) {
            width = 100;
            height = 100;
            GLFW.glfwWindowHint(GLFW.GLFW_MAXIMIZED, GLFW.GLFW_TRUE);
            maximized = true;
        }


        /*
        Here we give our window ID a value.
        This is the standard method for creating a window using GLFW.
        The function requires our Width variable, Height variable, and our Title string.
        As well as 2 NULL values specifically from the MemoryUtil class.
        The actual value of NULL here is "0L" and is a LONG var type.
        JUST "NULL" OR "0" WONT WORK. THIS MUST BE "(long) 0L"
         */
        window = GLFW.glfwCreateWindow(width, height, title, MemoryUtil.NULL, MemoryUtil.NULL);
        if(window == MemoryUtil.NULL) {
            throw new RuntimeException("Problem creating GLFW window using GLFW.glfwCreateWindow() (Window ID is NULL)");
        }

        /*
        This handles memory allocation for the window frame buffer.
        It takes window information to create a framebuffer and uses
        a callback to change the framebuffer size when window params
        are changed for instance.
         */
        GLFW.glfwSetFramebufferSizeCallback(window, (window, width, height) -> {
            this.width = width;
            this.height = height;
            this.setResize(true);
        });

        /*
        Key callback for force quit shortcut.
        We set a key callback to listen for keypresses using GLFW.
         */
        GLFW.glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
            if(key == GLFW.GLFW_KEY_BACKSLASH && action == GLFW.GLFW_PRESS) {
                GLFW.glfwSetWindowShouldClose(window, true);
            }
        });

        /*
        Here, we continue the maximize logic.
        We use GLFW to maximize the window if our maximized bool
        is set to true. If not, we use a vidMode to have GLFW
        understand which display we would like to have the window
        spawn to. We then use glfwSetWindowPos() to center the
        window to the monitor's width by using vidMode width and height
        (The monitor width and height) and the window height to
        draw the window in the center. GLFW draws the window from
        the top left pixel of the window, hence the window's width
        and height values being subracted from the monitors dimensions.
         */
        if(maximized) {
            GLFW.glfwMaximizeWindow(window);
        } else {
            GLFWVidMode vidMode = GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor());
            GLFW.glfwSetWindowPos(window, (vidMode.width() - width) / 2, (vidMode.height() - height) / 2);
        }

        /*
        These functions do a variety of things. In order:
        -We make our window the target for rendering and inputs
         by passing through and using the window long variable.
        -We check for Vsync and, if enabled, we use vSync.
        -We show the window as it was initialized as hidden earlier.
        -We use GL's createCapabilities() function to initialize graphics
         library, handle errors, and allocate resources to this window.
        -We set the ClearColor, which is the window's background color,
         to black using RGBa float values for each color and alpha (opacity)
        -We enable a few openGL graphics tests, depth, stencil.
        -We enable face culling by default, and we cull the back faces (behind mesh normal direction).
         */
        GLFW.glfwMakeContextCurrent(window);

        if(isVsync()) {
            GLFW.glfwSwapInterval(1);
        }
        GLFW.glfwShowWindow(window);
        GL.createCapabilities();
        GL11.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_STENCIL_TEST);
//        GL11.glEnable(GL11.GL_CULL_FACE);
//        GL11.glCullFace(GL11.GL_BACK);
    }

    /*
    UPDATE METHOD
    This is run every "frame" and is swaps the current frame to the next one,
    Polls for events (THIS IS CRUCIAL AS IT PROCESSES OBJECTS IN OUR RENDER QUEUE)
     */
    public void update() {
        GLFW.glfwSwapBuffers(window);
        GLFW.glfwPollEvents();
    }

    /*
    CLEANUP METHOD
    This should execute when window is closed.
    It tidily cleans up open processes and closes
    the program gracefully.
     */
    public void cleanup() {
        GLFW.glfwDestroyWindow(window);
    }

    /*
    Custom method for changing the background color for
    our window. Takes RGB and alpha values as params.
     */
    public void setClearColor(float r, float g, float b, float a) {
        GL11.glClearColor(r, g, b, a);
    }

    /*
    KEYPRESS METHOD
    This is to detect keypresses.
    Once a key is pressed, it detects it and returns
    the relevant keycode for further processing.
    Takes a keycode as a param.
     */
    public boolean isKeyPressed(int key) {
        return GLFW.glfwGetKey(window, key) == GLFW.GLFW_PRESS;
    }

    /*
    Closes the window upon request by flipping
    windowShouldClose to TRUE.
     */
    public boolean windowShouldClose() {
        return GLFW.glfwWindowShouldClose(window);
    }

    public String getTitle() {
        return title;
    }

    /*
    Sets the title for the window using GLFW.
    this method allows to title changes at runtime (per update)
     */
    public void setTitle(String title) {
        GLFW.glfwSetWindowTitle(window, title);
    }

    /*
    GETTERS AND SETTERS
    I have combined these visually in one place for convenience.
    There is more underneath this segment.
     */
    public boolean isResize() {
        return resize;
    }
    public void setResize(boolean resize) {
        this.resize = resize;
    }
    public boolean isVsync() {
        return Vsync;
    }
    public int getWidth() {
        return width;
    }
    public int getHeight() {
        return height;
    }
    public long getWindow() {
        return window;
    }
    public Matrix4f getProjectionMatrix() {
        return projectionMatrix;
    }

    /*
    Uses 4D matrix variable from JOML library to cast a projection to a 2D canvas.
    We can give it our FOV, aspectRatio, Near, and Far variables to create a perspectve
    object for use in openGL. This is high level math and im not going to
    pretend to understand how it works. I consider it boilerplate.
    The first method is to allow running without params, the second is the optimal
    run-case where we pass through width and height and all that fun stuff.
     */
    public Matrix4f updateProjectionMatrix() {
        float aspectRatio = (float) width / (float) height;
        return projectionMatrix.setPerspective(FOV, aspectRatio, Z_NEAR, Z_FAR);
    }

    public Matrix4f updateProjectionMatrix(Matrix4f matrix, int width, int height) {
        float aspectRatio = (float) width / height;
        return matrix.perspective(FOV, aspectRatio, Z_NEAR, Z_FAR);
    }

    /**
     * Get the current X position of the cursor
     * @return The X position of the cursor
     */
    public double getCursorX() {
        double[] xpos = new double[1];
        GLFW.glfwGetCursorPos(window, xpos, null);
        return xpos[0];
    }
    
    /**
     * Get the current Y position of the cursor
     * @return The Y position of the cursor
     */
    public double getCursorY() {
        double[] ypos = new double[1];
        GLFW.glfwGetCursorPos(window, null, ypos);
        return ypos[0];
    }
    
    /**
     * Set the position of the cursor
     * @param x The X position to set
     * @param y The Y position to set
     */
    public void setCursorPosition(int x, int y) {
        GLFW.glfwSetCursorPos(window, x, y);
    }
}
