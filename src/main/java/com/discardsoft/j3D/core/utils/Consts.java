package com.discardsoft.j3D.core.utils;

/**
 * Engine-wide constants for the j3D engine.
 * <p>
 * This class contains constants that are used throughout the engine,
 * including version information, branding, and rendering constants.
 * </p>
 * 
 * @author DiscardSoft
 * @version 0.2
 */
public final class Consts {
    
    /**
     * Private constructor to prevent instantiation of constants class.
     */
    private Consts() {
        throw new UnsupportedOperationException("Constants class cannot be instantiated");
    }

    //---------- Version and Branding ----------//
    
    /** Engine version string displayed in window title and logs */
    public static final String VERSION = "v0.07a";
    
    /** Author/company name */
    public static final String AUTHOR = "DiscardSoft";
    
    /** Stylized author/company name for display purposes */
    public static final String AUTHOR_STYLIZED = "DISCVRD";
    
    /** Engine name */
    public static final String TITLE = "j3D";
    
    //---------- Rendering Constants ----------//
    
    /** Near clipping plane distance for the camera frustum */
    public static final float Z_NEAR = 0.001f;
    
    /** Far clipping plane distance for the camera frustum */
    public static final float Z_FAR = 10000f;
}
