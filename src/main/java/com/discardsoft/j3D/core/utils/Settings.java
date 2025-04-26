package com.discardsoft.j3D.core.utils;

/**
 * Configuration settings for the j3D engine.
 * <p>
 * This class contains settings that control various aspects of the engine's behavior,
 * such as development mode, window properties, and camera controls.
 * Settings can be adjusted here to modify the engine's functionality.
 * </p>
 * 
 * @author DISCVRD Software
 * @version 0.1
 */
public final class Settings {
    
    /**
     * Private constructor to prevent instantiation of settings class.
     */
    private Settings() {
        throw new UnsupportedOperationException("Settings class cannot be instantiated");
    }

    /** Development mode flag. When true, enables development features like wireframe mode. */
    public static final boolean DEV = true;
    
    //---------- Window Settings ----------//
    
    /** Window resizability flag. When true, allows the window to be resized by the user. */
    public static final boolean RESIZABLE = false;
    
    /** Vertical synchronization flag. When true, synchronizes frame rendering with monitor refresh rate. */
    public static final boolean VSYNC = true;
    
    //---------- Camera Settings ----------//
    
    /** Speed multiplier for camera movement. Higher values make the camera move faster. */
    public static final float CAMERA_MOVE_SPEED = 0.16f;
    
    /** Sensitivity multiplier for mouse movement. Higher values make the camera rotate faster. */
    public static final float MOUSE_SENSITIVITY = 0.2f;
    
    /** Field of view angle in radians. Controls the camera's viewing angle. */
    public static final float FOV = (float) Math.toRadians(70);
}
