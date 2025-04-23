package com.discardsoft.j3D.core.entity;

import org.joml.Vector3f;

/**
 * Represents a light source in the 3D scene.
 * <p>
 * A light has position, color, and ambient light properties that determine
 * how it illuminates objects in the scene. This is currently implemented as
 * a point light with ambient, diffuse, and specular components.
 * </p>
 * 
 * @author DISCVRD Software
 * @version 0.1
 */
public class Light {
    
    /** The position of the light in 3D space */
    private final Vector3f position;
    
    /** The RGB color of the light */
    private final Vector3f color;
    
    /** The ambient light intensity (RGB values) */
    private final Vector3f ambient;

    /**
     * Constructs a default light source at the origin.
     * <p>
     * Creates a white light with moderate ambient lighting.
     * </p>
     */
    public Light() {
        this.position = new Vector3f(0, 0, 0);
        this.color = new Vector3f(1, 1, 1);  // Default white light
        this.ambient = new Vector3f(0.2f, 0.2f, 0.15f);  // Slight blue-green tint to ambient
    }

    /**
     * Constructs a light with specified properties.
     *
     * @param position The position of the light in 3D space
     * @param color The RGB color of the light (usually normalized to 0.0-1.0)
     * @param ambient The ambient light intensity (RGB values between 0.0-1.0)
     */
    public Light(Vector3f position, Vector3f color, Vector3f ambient) {
        this.position = position;
        this.color = color;
        this.ambient = ambient;
    }

    /**
     * Gets the position of the light.
     *
     * @return The position vector
     */
    public Vector3f getPosition() {
        return position;
    }

    /**
     * Gets the color of the light.
     *
     * @return The color vector (RGB)
     */
    public Vector3f getColor() {
        return color;
    }
    
    /**
     * Gets the ambient light intensity.
     *
     * @return The ambient light vector (RGB)
     */
    public Vector3f getAmbient() {
        return ambient;
    }
    
    /**
     * Sets the light's position.
     *
     * @param x The X coordinate
     * @param y The Y coordinate 
     * @param z The Z coordinate
     */
    public void setPosition(float x, float y, float z) {
        this.position.set(x, y, z);
    }
    
    /**
     * Sets the light's position.
     *
     * @param position The new position vector
     */
    public void setPosition(Vector3f position) {
        this.position.set(position);
    }
    
    /**
     * Sets the light's color.
     * 
     * @param r Red component (0.0-1.0)
     * @param g Green component (0.0-1.0)
     * @param b Blue component (0.0-1.0)
     */
    public void setColor(float r, float g, float b) {
        this.color.set(r, g, b);
    }
    
    /**
     * Sets the light's color.
     *
     * @param color The new color vector (RGB)
     */
    public void setColor(Vector3f color) {
        this.color.set(color);
    }
    
    /**
     * Sets the ambient light intensity.
     *
     * @param r Red component (0.0-1.0)
     * @param g Green component (0.0-1.0) 
     * @param b Blue component (0.0-1.0)
     */
    public void setAmbient(float r, float g, float b) {
        this.ambient.set(r, g, b);
    }
    
    /**
     * Sets the ambient light intensity.
     *
     * @param ambient The new ambient light vector (RGB)
     */
    public void setAmbient(Vector3f ambient) {
        this.ambient.set(ambient);
    }
}
