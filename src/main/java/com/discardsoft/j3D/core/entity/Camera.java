package com.discardsoft.j3D.core.entity;

import org.joml.Vector3f;

/**
 * Represents a camera in 3D space that provides a view into the game world.
 * <p>
 * The camera has position and rotation properties that determine what part
 * of the scene is visible. It provides methods to move and rotate the camera,
 * including first-person style movement relative to the camera's orientation.
 * </p>
 * 
 * @author DISCVRD Software
 * @version 0.1
 */
public class Camera {

    /** The camera's position in 3D space */
    private final Vector3f position;
    
    /** The camera's rotation in degrees around each axis */
    private final Vector3f rotation;

    /**
     * Constructs a new camera at the origin with no rotation.
     */
    public Camera() {
        this(new Vector3f(0, 0, 0), new Vector3f(0, 0, 0));
    }

    /**
     * Constructs a new camera with the specified position and rotation.
     *
     * @param position The initial position
     * @param rotation The initial rotation in degrees
     */
    public Camera(Vector3f position, Vector3f rotation) {
        this.position = position;
        this.rotation = rotation;
    }

    /**
     * Moves the camera relative to its current orientation.
     * <p>
     * This method performs first-person style movement where:
     * - Z movement is forward/backward along the camera's view direction
     * - X movement is left/right perpendicular to the view direction
     * - Y movement is up/down along the world's Y axis
     * </p>
     *
     * @param x Left/right movement amount
     * @param y Up/down movement amount
     * @param z Forward/backward movement amount
     */
    public void movePosition(float x, float y, float z) {
        // Forward/backward movement along camera direction
        if (z != 0) {
            float radians = (float) Math.toRadians(rotation.y);
            position.x += (float) Math.sin(radians) * -1.0f * z;
            position.z += (float) Math.cos(radians) * z;
        }
        
        // Left/right movement perpendicular to camera direction
        if (x != 0) {
            float radians = (float) Math.toRadians(rotation.y - 90);
            position.x += (float) Math.sin(radians) * -1.0f * x;
            position.z += (float) Math.cos(radians) * x;
        }
        
        // Simple up/down movement along world Y axis
        position.y += y;
    }

    /**
     * Sets the camera's absolute position.
     *
     * @param x X-coordinate
     * @param y Y-coordinate
     * @param z Z-coordinate
     */
    public void setPosition(float x, float y, float z) {
        position.x = x;
        position.y = y;
        position.z = z;
    }

    /**
     * Sets the camera's position using a vector.
     *
     * @param position The new position vector
     */
    public void setPosition(Vector3f position) {
        this.position.set(position);
    }

    /**
     * Sets the camera's absolute rotation.
     *
     * @param pitch Rotation around X-axis (up/down)
     * @param yaw Rotation around Y-axis (left/right)
     * @param roll Rotation around Z-axis (tilting)
     */
    public void setRotation(float pitch, float yaw, float roll) {
        rotation.x = pitch;
        rotation.y = yaw;
        rotation.z = roll;
    }

    /**
     * Sets the camera's rotation using a vector.
     *
     * @param rotation The new rotation vector in degrees
     */
    public void setRotation(Vector3f rotation) {
        this.rotation.set(rotation);
    }

    /**
     * Incrementally changes the camera's rotation.
     *
     * @param pitch Change in rotation around X-axis
     * @param yaw Change in rotation around Y-axis
     * @param roll Change in rotation around Z-axis
     */
    public void addRotation(float pitch, float yaw, float roll) {
        rotation.x += pitch;
        rotation.y += yaw;
        rotation.z += roll;
    }

    /**
     * Gets the camera's current position.
     *
     * @return The position vector
     */
    public Vector3f getPosition() {
        return position;
    }

    /**
     * Gets the camera's current rotation.
     *
     * @return The rotation vector in degrees
     */
    public Vector3f getRotation() {
        return rotation;
    }

    /**
     * Rotates the camera based on mouse movement.
     * <p>
     * Applies yaw and pitch rotation and constrains pitch to avoid camera flipping.
     * </p>
     *
     * @param deltaYaw Change in horizontal rotation (mouse X movement)
     * @param deltaPitch Change in vertical rotation (mouse Y movement)
     */
    public void rotateCamera(float deltaYaw, float deltaPitch) {
        // Apply rotation changes
        rotation.y += deltaYaw; // Yaw (left/right)
        rotation.x += deltaPitch; // Pitch (up/down)

        // Constrain pitch to -90 to 90 degrees to prevent flipping
        if (rotation.x > 90.0f) {
            rotation.x = 90.0f;
        } else if (rotation.x < -90.0f) {
            rotation.x = -90.0f;
        }
        
        // Optional: Normalize yaw to 0-360 degrees
        while (rotation.y >= 360.0f) {
            rotation.y -= 360.0f;
        }
        while (rotation.y < 0.0f) {
            rotation.y += 360.0f;
        }
    }
    
    /**
     * @deprecated Use {@link #addRotation(float, float, float)} instead
     */
    @Deprecated
    public void moveRotation(float x, float y, float z) {
        addRotation(x, y, z);
    }
}
