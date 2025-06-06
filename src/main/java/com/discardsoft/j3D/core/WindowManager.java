// -|+++++++++++++++++++++++++++++++++++++++++++++++++++++++++|-
// -|              04/16/2025 | DiscardSoft                   |-
// -|        j3D is a lightweight custom-built engine         |-
// -|        Made with LWJGL, openGL, JOML, and other         |-
// -|        helpful libraries for use on DiscardSoft         |-
// -|              Window Manager class for j3D               |-
// -|    Comments are always written above relevant context.  |-
// -|   ++++++++++++++++++++++++++++++++++++++++++++++++++    |-
// -|               Version:  In Development             |-
// -|   *some comments may be written by AI for convenience   |-
// -|+++++++++++++++++++++++++++++++++++++++++++++++++++++++++|-

package com.discardsoft.j3D.core;

import com.discardsoft.j3D.core.utils.Consts;
import com.discardsoft.j3D.core.utils.Settings;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryUtil;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static org.lwjgl.opengl.GL11.glViewport;

public class WindowManager {

    //CONSTANT VARS for window management
    public static final float FOV = Settings.FOV;
    public static final float Z_NEAR = Consts.Z_NEAR;
    public static final float Z_FAR = Consts.Z_FAR;

    private final String title;

    private int width, height; // width and height of window
    private long window; // unique, auto-generated window ID used by GLFW.

    private boolean resize = Settings.RESIZABLE; // is window resizable?
    private boolean Vsync; // does window use Vsync?

    private final Matrix4f projectionMatrix;

    private final Map<Integer, Boolean> prevKeyStates = new HashMap<>(); // map to track key states

    // Add fields to track mouse position and sensitivity
    private double lastMouseX, lastMouseY;
    private boolean firstMouse = true;
    
    // Track if cursor is currently captured for camera control
    private boolean cursorCaptured = true;
    
    // Track if cursor is waiting for a click to resume capture
    private boolean waitingForClick = false;

    // Mouse button state tracking
    private boolean leftMousePressed = false;

    // Track scroll wheel movement
    private double scrollOffset = 0.0;
    private boolean hasScrolled = false;
    
    // Window focus state
    private boolean windowFocused = true;
    
    // Event listeners
    private final List<Consumer<Boolean>> cursorCaptureListeners = new ArrayList<>();
    private final List<Consumer<Boolean>> windowFocusListeners = new ArrayList<>();

    public WindowManager(String title, int width, int height) {
        this.title = title;
        this.width = width;
        this.height = height;
        Vsync = Settings.VSYNC;
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

        // Get the primary monitor's resolution
        GLFWVidMode vidMode = GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor());
        
        // NOTE: Removed the code that was overriding the user-specified resolution
        // Now the resolution set in Main.java will be used directly
        
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
        JUST "NULL" OR "0" WON'T WORK. THIS MUST BE "(long) 0L"
         */
        window = GLFW.glfwCreateWindow(width, height, title, MemoryUtil.NULL, MemoryUtil.NULL);
        if(window == MemoryUtil.NULL) {
            throw new RuntimeException("Problem creating GLFW window using GLFW.glfwCreateWindow() (Window ID is NULL)");
        }

        /*
        * Here we set the correct cursor type
        * for the window. This is a standard for 3D games
        * that use the mouse as a camera controller.
        * We also attempt to use RAW MOUSE INPUT which
        * is very, very, super highly preferable.
        */
        GLFW.glfwSetInputMode(window, GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_DISABLED);
        if(GLFW.glfwRawMouseMotionSupported()) {
            GLFW.glfwSetInputMode(window, GLFW.GLFW_RAW_MOUSE_MOTION, GLFW.GLFW_TRUE);
        }

        // Make the OpenGL context current before setting up callbacks
        GLFW.glfwMakeContextCurrent(window);

        // Set up the framebuffer size callback after making the context current
        GLFW.glfwSetFramebufferSizeCallback(window, (window, width, height) -> {
            this.width = width;
            this.height = height;
            glViewport(0, 0, width, height);
            // Update projection matrix with new aspect ratio
            float aspectRatio = (float) width / (float) height;
            projectionMatrix.setPerspective(FOV, aspectRatio, Z_NEAR, Z_FAR);
        });

        // Initialize OpenGL capabilities after making the context current
        GL.createCapabilities();

        /*
        Key callback for force quit shortcut.
        We set a key callback to listen for keypresses using GLFW.
         */
        GLFW.glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
            if(key == GLFW.GLFW_KEY_BACKSLASH && action == GLFW.GLFW_PRESS) {
                GLFW.glfwSetWindowShouldClose(window, true);
            }
        });
        
        // Set up window focus callback - automatically pause/resume based on focus
        GLFW.glfwSetWindowFocusCallback(window, (window, focused) -> {
            windowFocused = focused;
            if (!focused) {
                // Only release cursor if we're not already in an unfocused state
                if (cursorCaptured) {
                    releaseCursor();
                }
            }
            // Notify all window focus listeners
            notifyWindowFocusListeners(focused);
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
            vidMode = GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor());
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
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glCullFace(GL11.GL_BACK);
        GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);

        // Set up the mouse scroll callback
        GLFW.glfwSetScrollCallback(window, (windowHandle, xOffset, yOffset) -> {
            scrollOffset = yOffset;
            hasScrolled = true;
        });
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
    public boolean isKeyPressedBuffered(int key) {
        boolean curr = isKeyPressed(key);
        boolean prev = prevKeyStates.getOrDefault(key, false);
        prevKeyStates.put(key, curr);
        return curr && !prev;
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
        float aspectRatio = (float) width / (float) height;
        return matrix.setPerspective(FOV, aspectRatio, Z_NEAR, Z_FAR);
    }

    // method to process mouse movement
    public Vector2f processMouseMovement() {
        double[] xPos = new double[1];
        double[] yPos = new double[1];
        GLFW.glfwGetCursorPos(window, xPos, yPos);

        double deltaX = xPos[0] - lastMouseX;
        double deltaY = yPos[0] - lastMouseY;

        if (firstMouse) {
            deltaX = 0;
            deltaY = 0;
            firstMouse = false;
        }

        lastMouseX = xPos[0];
        lastMouseY = yPos[0];

        float mouseSensitivity = Settings.MOUSE_SENSITIVITY;
        return new Vector2f((float) deltaX * mouseSensitivity, (float) deltaY * mouseSensitivity);
    }

    /**
     * Releases the cursor from camera control mode.
     * Makes the cursor visible and allows it to move freely within the window.
     */
    public void releaseCursor() {
        if (cursorCaptured) {
            GLFW.glfwSetInputMode(window, GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_NORMAL);
            cursorCaptured = false;
            waitingForClick = true;
            // Reset first mouse to avoid camera jump when recapturing
            firstMouse = true;
            
            // Notify all cursor capture listeners
            notifyCursorCaptureListeners(false);
        }
    }

    /**
     * Captures the cursor for camera control.
     * Hides the cursor and constrains it to the window for camera movement.
     */
    public void captureCursor() {
        if (!cursorCaptured && windowFocused) {
            GLFW.glfwSetInputMode(window, GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_DISABLED);
            cursorCaptured = true;
            waitingForClick = false;
            // Reset first mouse to avoid camera jump
            firstMouse = true;
            
            // Notify all cursor capture listeners
            notifyCursorCaptureListeners(true);
        }
    }
    
    /**
     * Checks if the cursor is currently captured for camera control.
     * 
     * @return true if cursor is in camera control mode, false otherwise
     */
    public boolean isCursorCaptured() {
        return cursorCaptured;
    }
    
    /**
     * Checks if the window is waiting for a click to recapture the cursor.
     * 
     * @return true if waiting for a click, false otherwise
     */
    public boolean isWaitingForClick() {
        return waitingForClick;
    }
    
    /**
     * Checks if the left mouse button is currently pressed.
     * 
     * @return true if left mouse button is pressed, false otherwise
     */
    public boolean isMouseButtonPressed() {
        return GLFW.glfwGetMouseButton(window, GLFW.GLFW_MOUSE_BUTTON_LEFT) == GLFW.GLFW_PRESS;
    }
    
    /**
     * Checks if the left mouse button was just clicked (pressed and released).
     * Similar to the buffered key press detection.
     * 
     * @return true if left mouse button was just clicked, false otherwise
     */
    public boolean isMouseButtonClicked() {
        boolean curr = isMouseButtonPressed();
        boolean result = curr && !leftMousePressed;
        leftMousePressed = curr;
        return result;
    }

    /**
     * Gets the mouse scroll wheel movement since the last call.
     * 
     * @return positive value for scroll up, negative for scroll down, zero for no scrolling
     */
    public double getScrollOffset() {
        double result = scrollOffset;
        scrollOffset = 0.0;
        return result;
    }
    
    /**
     * Checks if the scroll wheel has been used since the last call.
     * 
     * @return true if scroll wheel was used, false otherwise
     */
    public boolean hasScrolled() {
        boolean result = hasScrolled;
        hasScrolled = false;
        return result;
    }
    
    /**
     * Checks if the window currently has focus.
     * 
     * @return true if window is focused, false otherwise
     */
    public boolean isWindowFocused() {
        return windowFocused;
    }
    
    /**
     * Add a listener for cursor capture events.
     * 
     * @param listener Consumer that takes a boolean indicating if cursor is captured
     */
    public void addCursorCaptureListener(Consumer<Boolean> listener) {
        cursorCaptureListeners.add(listener);
    }
    
    /**
     * Add a listener for window focus events.
     * 
     * @param listener Consumer that takes a boolean indicating if window is focused
     */
    public void addWindowFocusListener(Consumer<Boolean> listener) {
        windowFocusListeners.add(listener);
    }
    
    /**
     * Notifies all cursor capture listeners of a change in cursor capture state.
     * 
     * @param captured true if cursor is captured, false otherwise
     */
    private void notifyCursorCaptureListeners(boolean captured) {
        for (Consumer<Boolean> listener : cursorCaptureListeners) {
            listener.accept(captured);
        }
    }
    
    /**
     * Notifies all window focus listeners of a change in window focus state.
     * 
     * @param focused true if window is focused, false otherwise
     */
    private void notifyWindowFocusListeners(boolean focused) {
        for (Consumer<Boolean> listener : windowFocusListeners) {
            listener.accept(focused);
        }
    }

    /**
     * Creates an orthographic projection matrix for 2D UI rendering.
     * <p>
     * This creates a 2D projection with coordinates from -1 to 1 in both axes,
     * which is ideal for UI elements that should be displayed in normalized device coordinates.
     * </p>
     * 
     * @return An orthographic projection matrix for 2D rendering
     */
    public Matrix4f getOrthographicMatrix() {
        Matrix4f orthoMatrix = new Matrix4f();
        orthoMatrix.setOrtho(-1f, 1f, -1f, 1f, -1f, 1f);
        return orthoMatrix;
    }
}
