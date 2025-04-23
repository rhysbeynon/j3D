package com.discardsoft.j3D.core.entity;

import com.discardsoft.j3D.core.utils.LoadModel;
import org.joml.Vector3f;

/**
 * Represents a player entity in the game world.
 * <p>
 * The Player class combines a bounding capsule collision shape with
 * a camera positioned at eye level. It supports both standard physics-based
 * movement and a "free camera" mode for development purposes.
 * </p>
 * 
 * @author DISCVRD Software
 * @version 0.1
 */
public class Player {
    
    /** The base collision entity (invisible capsule) */
    private final Entity boundingEntity;
    
    /** The camera used for the player's view */
    private final Camera camera;
    
    /** Total height of the player in units */
    private static final float PLAYER_HEIGHT = 1.8f;
    
    /** Eye height relative to the bottom of the player */
    private static final float EYE_HEIGHT = 0.8f * PLAYER_HEIGHT;
    
    /** Base Y offset to position the player correctly */
    private static final float BASE_Y_OFFSET = 0.9f;
    
    /** Player movement speed */
    private static final float MOVEMENT_SPEED = 0.03f;
    
    /** Maximum velocity of the player */
    private static final float MAX_VELOCITY = 0.1f;
    
    /** Inertia factor (0 = no inertia, 1 = maximum inertia) */
    private static final float INERTIA_FACTOR = 0.85f;
    
    /** Current velocity vector */
    private final Vector3f velocity;
    
    /** Movement input vector */
    private final Vector3f movementInput;
    
    /** Flag for free camera mode */
    private boolean freeCameraMode;
    
    /** Camera position before free camera mode was activated */
    private final Vector3f storedCameraPosition;
    
    /** Camera rotation before free camera mode was activated */
    private final Vector3f storedCameraRotation;

    /**
     * Creates a new player at the specified position.
     *
     * @param position The initial position of the player's feet
     */
    public Player(Vector3f position) {
        // Adjust position to account for the base offset
        Vector3f adjustedPosition = new Vector3f(position).add(0, BASE_Y_OFFSET, 0);
        
        // Create the bounding entity
        Model capsuleModel = LoadModel.model("PlayerCapsule");
        this.boundingEntity = new Entity(
            capsuleModel,
            adjustedPosition,
            new Vector3f(0, 0, 0),
            new Vector3f(1, 1, 1)
        );
        
        // Create the camera at eye level
        this.camera = new Camera();
        updateCameraPosition();
        
        // Initialize movement variables
        this.velocity = new Vector3f(0, 0, 0);
        this.movementInput = new Vector3f(0, 0, 0);
        this.freeCameraMode = false;
        this.storedCameraPosition = new Vector3f();
        this.storedCameraRotation = new Vector3f();
    }
    
    /**
     * Updates the player's position based on velocity and input.
     * <p>
     * Handles physics-based movement with inertia when not in free camera mode.
     * </p>
     *
     * @param deltaTime Time elapsed since last update in seconds
     */
    public void update(float deltaTime) {
        if (!freeCameraMode) {
            // Apply movement input to velocity with inertia
            velocity.x = velocity.x * INERTIA_FACTOR + movementInput.x * MOVEMENT_SPEED;
            velocity.z = velocity.z * INERTIA_FACTOR + movementInput.z * MOVEMENT_SPEED;
            
            // Limit maximum velocity
            if (velocity.lengthSquared() > MAX_VELOCITY * MAX_VELOCITY) {
                velocity.normalize().mul(MAX_VELOCITY);
            }
            
            // Move the bounding entity based on velocity
            Vector3f movementVec = new Vector3f(velocity).mul(deltaTime * 60); // Normalize for 60 fps
            boundingEntity.incrementPosition(movementVec.x, 0, movementVec.z);
            
            // Update camera position to match player
            updateCameraPosition();
        }
        
        // Reset movement input for next frame
        movementInput.set(0, 0, 0);
    }
    
    /**
     * Sets the movement input direction.
     * <p>
     * The input vector should represent normalized directional input
     * (e.g., -1/0/1 for each axis from keyboard controls).
     * </p>
     *
     * @param forwardMovement Forward/backward input (-1 to 1)
     * @param sidewaysMovement Left/right input (-1 to 1)
     */
    public void setMovementInput(float forwardMovement, float sidewaysMovement) {
        if (!freeCameraMode) {
            // Reset movement input
            movementInput.set(0, 0, 0);
            
            // Note: We negate the forwardMovement parameter to correct the direction
            // This fixes the forward/backward being flipped in the controls
            forwardMovement = -forwardMovement;
            
            // Apply the same directional movement calculation as the Camera class
            // Forward/backward movement along camera direction
            if (forwardMovement != 0) {
                float radians = (float) Math.toRadians(camera.getRotation().y);
                movementInput.x += (float) Math.sin(radians) * -1.0f * forwardMovement;
                movementInput.z += (float) Math.cos(radians) * forwardMovement;
            }
            
            // Left/right movement perpendicular to camera direction
            if (sidewaysMovement != 0) {
                float radians = (float) Math.toRadians(camera.getRotation().y - 90);
                movementInput.x += (float) Math.sin(radians) * -1.0f * sidewaysMovement;
                movementInput.z += (float) Math.cos(radians) * sidewaysMovement;
            }
        }
    }
    
    /**
     * Toggles free camera mode on or off.
     * <p>
     * When toggling on, stores the current camera position and rotation.
     * When toggling off, returns the camera to the player's position but
     * preserves the current look direction.
     * </p>
     */
    public void toggleFreeCamera() {
        freeCameraMode = !freeCameraMode;
        
        if (freeCameraMode) {
            // Store camera position and rotation for later restoration
            storedCameraPosition.set(camera.getPosition());
            storedCameraRotation.set(camera.getRotation());
        } else {
            // Restore camera position AND rotation to original state
            updateCameraPosition();
            camera.setRotation(storedCameraRotation);
        }
    }
    
    /**
     * Updates the camera position to match the player's eye level.
     */
    private void updateCameraPosition() {
        // Position camera at player's eye level
        Vector3f entityPos = boundingEntity.getPosition();
        camera.setPosition(
            entityPos.x,
            entityPos.y - BASE_Y_OFFSET + EYE_HEIGHT, // Adjust for base offset and eye height
            entityPos.z
        );
    }
    
    /**
     * Checks if the player is in free camera mode.
     *
     * @return True if free camera mode is active
     */
    public boolean isFreeCameraMode() {
        return freeCameraMode;
    }
    
    /**
     * Gets the player's camera.
     *
     * @return The camera used by the player
     */
    public Camera getCamera() {
        return camera;
    }
    
    /**
     * Gets the player's bounding entity.
     *
     * @return The entity representing the player's physical body
     */
    public Entity getBoundingEntity() {
        return boundingEntity;
    }
    
    /**
     * Sets the player's absolute position (feet position).
     *
     * @param x X coordinate
     * @param y Y coordinate (ground level)
     * @param z Z coordinate
     */
    public void setPosition(float x, float y, float z) {
        boundingEntity.setPosition(x, y + BASE_Y_OFFSET, z);
        updateCameraPosition();
    }
    
    /**
     * Sets the player's absolute position.
     *
     * @param position The new position vector (feet position)
     */
    public void setPosition(Vector3f position) {
        Vector3f adjustedPos = new Vector3f(position).add(0, BASE_Y_OFFSET, 0);
        boundingEntity.setPosition(adjustedPos);
        updateCameraPosition();
    }
    
    /**
     * Gets the player's current position (feet position).
     *
     * @return The position vector
     */
    public Vector3f getPosition() {
        Vector3f adjustedPos = new Vector3f(boundingEntity.getPosition()).sub(0, BASE_Y_OFFSET, 0);
        return adjustedPos;
    }
    
    /**
     * Gets the player's raw velocity vector.
     *
     * @return The current velocity vector
     */
    public Vector3f getVelocity() {
        return velocity;
    }
}