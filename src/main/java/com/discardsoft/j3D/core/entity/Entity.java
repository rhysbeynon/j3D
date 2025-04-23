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

    /**
     * Constructs a new entity with the specified properties.
     *
     * @param model The 3D model to render for this entity
     * @param position The initial position in 3D space
     * @param rotation The initial rotation in degrees
     * @param scale The initial scale factors
     */
    public Entity(Model model, Vector3f position, Vector3f rotation, Vector3f scale) {
        this.model = model;
        this.position = position;
        this.rotation = rotation;
        this.scale = scale;
    }

    /**
     * Increments the entity's position.
     *
     * @param x The amount to move along the X axis
     * @param y The amount to move along the Y axis
     * @param z The amount to move along the Z axis
     */
    public void incrementPosition(float x, float y, float z) {
        this.position.x += x;
        this.position.y += y;
        this.position.z += z;
    }

    /**
     * Sets the entity's position.
     *
     * @param x The new X coordinate
     * @param y The new Y coordinate
     * @param z The new Z coordinate
     */
    public void setPosition(float x, float y, float z) {
        this.position.x = x;
        this.position.y = y;
        this.position.z = z;
    }
    
    /**
     * Sets the entity's position.
     *
     * @param position The new position vector
     */
    public void setPosition(Vector3f position) {
        this.position.set(position);
    }

    /**
     * Increments the entity's rotation.
     *
     * @param x The rotation degrees to add around the X axis
     * @param y The rotation degrees to add around the Y axis
     * @param z The rotation degrees to add around the Z axis
     */
    public void incrementRotation(float x, float y, float z) {
        this.rotation.x += x;
        this.rotation.y += y;
        this.rotation.z += z;
    }

    /**
     * Sets the entity's rotation.
     *
     * @param x The rotation in degrees around the X axis
     * @param y The rotation in degrees around the Y axis
     * @param z The rotation in degrees around the Z axis
     */
    public void setRotation(float x, float y, float z) {
        this.rotation.x = x;
        this.rotation.y = y;
        this.rotation.z = z;
    }
    
    /**
     * Sets the entity's rotation.
     *
     * @param rotation The new rotation vector in degrees
     */
    public void setRotation(Vector3f rotation) {
        this.rotation.set(rotation);
    }
    
    /**
     * Sets the entity's scale.
     *
     * @param x The scale factor along the X axis
     * @param y The scale factor along the Y axis
     * @param z The scale factor along the Z axis
     */
    public void setScale(float x, float y, float z) {
        this.scale.x = x;
        this.scale.y = y;
        this.scale.z = z;
    }
    
    /**
     * Sets the entity's scale.
     *
     * @param scale The new scale vector
     */
    public void setScale(Vector3f scale) {
        this.scale.set(scale);
    }

    /**
     * Gets the entity's model.
     *
     * @return The entity's 3D model
     */
    public Model getModel() {
        return model;
    }
    
    /**
     * Sets the entity's model.
     *
     * @param model The new 3D model
     */
    public void setModel(Model model) {
        this.model = model;
    }

    /**
     * Gets the entity's position.
     *
     * @return The position vector
     */
    public Vector3f getPosition() {
        return position;
    }

    /**
     * Gets the entity's rotation.
     *
     * @return The rotation vector in degrees
     */
    public Vector3f getRotation() {
        return rotation;
    }

    /**
     * Gets the entity's scale.
     *
     * @return The scale vector
     */
    public Vector3f getScale() {
        return scale;
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
     * @deprecated Use {@link #setPosition(float, float, float)} instead
     */
    @Deprecated
    public void setPos(float x, float y, float z) {
        setPosition(x, y, z);
    }
    
    /**
     * @deprecated Use {@link #incrementRotation(float, float, float)} instead
     */
    @Deprecated
    public void incRot(float x, float y, float z) {
        incrementRotation(x, y, z);
    }
    
    /**
     * @deprecated Use {@link #setRotation(float, float, float)} instead
     */
    @Deprecated
    public void setRot(float x, float y, float z) {
        setRotation(x, y, z);
    }
}
