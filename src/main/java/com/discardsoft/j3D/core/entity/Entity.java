package com.discardsoft.j3D.core.entity;

import org.joml.Vector3f;

/**
 * Base class for all renderable entities in the 3D world.
 * <p>
 * An entity represents a 3D object with position, rotation, and scale.
 * It references a 3D model that defines its visual appearance.
 * </p>
 *
 * @author DiscardSoft
 * @version 0.1
 */
public class Entity {

    /** The 3D model representing this entity's appearance */
    private Model model;
    
    /** The position of this entity in 3D space */
    private final Vector3f position;
    
    /** The rotation of this entity in degrees around each axis */
    private final Vector3f rotation;
    
    /** The scale of this entity on each axis */
    private final Vector3f scale;
    
    /** Flag indicating if this entity has transparency */
    private boolean hasTransparentTexture;
    
    /** Flag for horizontal (Y-axis only) billboarding */
    private boolean billboardY;
    
    /** Flag for full billboarding (faces camera completely) */
    private boolean billboardFull;

    /**
     * Constructs a new entity with the specified model and transform parameters.
     *
     * @param model The 3D model for this entity
     * @param position The initial position vector
     * @param rotation The initial rotation vector (in degrees)
     * @param scale The initial scale vector
     */
    public Entity(Model model, Vector3f position, Vector3f rotation, Vector3f scale) {
        this.model = model;
        this.position = new Vector3f(position);
        this.rotation = new Vector3f(rotation);
        this.scale = new Vector3f(scale);
        this.hasTransparentTexture = model.hasTransparentTexture();
        this.billboardY = false;
        this.billboardFull = false;
    }

    /**
     * Gets the model used by this entity.
     *
     * @return The model
     */
    public Model getModel() {
        return model;
    }

    /**
     * Sets a new model for this entity.
     *
     * @param model The new model
     */
    public void setModel(Model model) {
        this.model = model;
        this.hasTransparentTexture = model.hasTransparentTexture();
    }

    /**
     * Gets the position of this entity.
     *
     * @return The position vector
     */
    public Vector3f getPosition() {
        return position;
    }

    /**
     * Gets the rotation of this entity.
     *
     * @return The rotation vector (in degrees)
     */
    public Vector3f getRotation() {
        return rotation;
    }

    /**
     * Gets the scale of this entity.
     *
     * @return The scale vector
     */
    public Vector3f getScale() {
        return scale;
    }

    /**
     * Checks if this entity has a transparent texture.
     *
     * @return True if the entity has a transparent texture
     */
    public boolean hasTransparentTexture() {
        return hasTransparentTexture;
    }

    /**
     * Sets whether this entity has a transparent texture.
     *
     * @param hasTransparentTexture True if the entity has a transparent texture
     */
    public void setHasTransparentTexture(boolean hasTransparentTexture) {
        this.hasTransparentTexture = hasTransparentTexture;
    }

    /**
     * Checks if this entity uses Y-axis (horizontal) billboarding.
     *
     * @return True if Y-axis billboarding is enabled
     */
    public boolean isBillboardY() {
        return billboardY;
    }

    /**
     * Sets whether this entity should use Y-axis (horizontal) billboarding.
     * When enabled, the entity will rotate around the Y-axis to face the camera.
     * Note: Setting this to true will automatically disable full billboarding.
     *
     * @param billboardY True to enable Y-axis billboarding
     */
    public void setBillboardY(boolean billboardY) {
        this.billboardY = billboardY;
        if (billboardY) {
            this.billboardFull = false; // Cannot have both types at once
        }
    }

    /**
     * Checks if this entity uses full billboarding.
     *
     * @return True if full billboarding is enabled
     */
    public boolean isBillboardFull() {
        return billboardFull;
    }

    /**
     * Sets whether this entity should use full billboarding.
     * When enabled, the entity will always fully face the camera from any angle.
     * Note: Setting this to true will automatically disable Y-axis billboarding.
     *
     * @param billboardFull True to enable full billboarding
     */
    public void setBillboardFull(boolean billboardFull) {
        this.billboardFull = billboardFull;
        if (billboardFull) {
            this.billboardY = false; // Cannot have both types at once
        }
    }

    /**
     * Increments the position of this entity.
     *
     * @param dx The change in x
     * @param dy The change in y
     * @param dz The change in z
     */
    public void incrementPosition(float dx, float dy, float dz) {
        position.x += dx;
        position.y += dy;
        position.z += dz;
    }

    /**
     * Increments the rotation of this entity.
     *
     * @param dx The change in x rotation (degrees)
     * @param dy The change in y rotation (degrees)
     * @param dz The change in z rotation (degrees)
     */
    public void incrementRotation(float dx, float dy, float dz) {
        rotation.x += dx;
        rotation.y += dy;
        rotation.z += dz;
    }

    /**
     * Sets the absolute position of this entity.
     *
     * @param x The x coordinate
     * @param y The y coordinate
     * @param z The z coordinate
     */
    public void setPosition(float x, float y, float z) {
        position.x = x;
        position.y = y;
        position.z = z;
    }

    /**
     * Sets the absolute position of this entity.
     *
     * @param position The new position vector
     */
    public void setPosition(Vector3f position) {
        this.position.set(position);
    }

    /**
     * Sets the absolute rotation of this entity.
     *
     * @param x The x rotation (degrees)
     * @param y The y rotation (degrees)
     * @param z The z rotation (degrees)
     */
    public void setRotation(float x, float y, float z) {
        rotation.x = x;
        rotation.y = y;
        rotation.z = z;
    }

    /**
     * Sets the absolute rotation of this entity.
     *
     * @param rotation The new rotation vector (in degrees)
     */
    public void setRotation(Vector3f rotation) {
        this.rotation.set(rotation);
    }

    /**
     * Sets the absolute scale of this entity.
     *
     * @param x The x scale
     * @param y The y scale
     * @param z The z scale
     */
    public void setScale(float x, float y, float z) {
        scale.x = x;
        scale.y = y;
        scale.z = z;
    }
    
    // Legacy method aliases for backward compatibility
    
    /**
     * @deprecated Use {@link #getPosition()} instead
     */
    @Deprecated
    public Vector3f getPos() {
        return getPosition();
    }
    
    /**
     * @deprecated Use {@link #getRot()} instead
     */
    @Deprecated
    public Vector3f getRot() {
        return getRotation();
    }
    
    /**
     * @deprecated Use {@link #incrementPosition(float, float, float)} instead
     */
    @Deprecated
    public void incPos(float x, float y, float z) {
        incrementPosition(x, y, z);
    }
    
    /**
     * @deprecated Use {@link #incrementRotation(float, float, float)} instead
     */
    @Deprecated
    public void incRot(float x, float y, float z) {
        incrementRotation(x, y, z);
    }
    
    /**
     * @deprecated Use {@link #setPosition(float, float, float)} instead
     */
    @Deprecated
    public void setPos(float x, float y, float z) {
        setPosition(x, y, z);
    }
    
    /**
     * @deprecated Use {@link #setRotation(float, float, float)} instead
     */
    @Deprecated
    public void setRot(float x, float y, float z) {
        setRotation(x, y, z);
    }
}
