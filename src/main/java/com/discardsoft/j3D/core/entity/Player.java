package com.discardsoft.j3D.core.entity;

import com.discardsoft.j3D.core.utils.LoadModel;
import org.joml.Vector3f;

/**
 * Represents a player entity with physics-based movement and camera controls.
 * <p>
 * This class combines a camera with a physical presence in the world,
 * providing movement with momentum, collision detection, and support for
 * both physics-based and free camera modes.
 * </p>
 *
 * @author DiscardSoft
 * @version 0.3
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
    
    /** Base Y offset to position the player correctly 
     * @implSpec This is used to adjust the player's feet position
     * @implNote The base offset is half the player's height to center the player
     * vertically in the world
    */
    private static final float BASE_Y_OFFSET = (PLAYER_HEIGHT / 2);
    
    /** Player movement speed (reduced for slower movement) */
    private static final float MOVEMENT_SPEED = 0.4f;
    
    /** Maximum velocity of the player */
    private static final float MAX_VELOCITY = 999.9f;
    
    /** Inertia factor (0 = no inertia, 1 = maximum inertia) 
     * @implSpec 0.85f is a realistic earth-like friction factor
     * @implNote This value is used to calculate the inertia effect on the player
    */
    private static final float INERTIA_FACTOR = 0.85f;
    
    /** 
     * Gravity acceleration constant in game units per second squared
     * Based on Earth's gravity but scaled for game feel
     */
    private static final float GRAVITY = 15.0f;  // units per second squared
    
    /** 
     * Jump initial velocity (reduced for lower jumps)
     * Measured in game units per second
     */
    private static final float JUMP_POWER = 5.0f;
    
    /** Ground level y-coordinate */
    private static final float GROUND_LEVEL = 0.0f;
    
    /** Current velocity vector */
    private final Vector3f velocity;
    
    /** Movement input vector */
    private final Vector3f movementInput;
    
    /** Flag for free camera mode */
    private boolean freeCameraMode;
    
    /** Flag to track if player is on the ground */
    private boolean isGrounded;
    
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
        this.isGrounded = true;
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
        // Apply horizontal movement only when not in free camera mode
        if (!freeCameraMode) {
            // Apply movement input to velocity with inertia - frame rate independent
            float frameInertia = (float) Math.pow(INERTIA_FACTOR, deltaTime * 90); // Scale inertia to frame time
            
            // Apply horizontal movement
            velocity.x = velocity.x * frameInertia + movementInput.x * MOVEMENT_SPEED;
            velocity.z = velocity.z * frameInertia + movementInput.z * MOVEMENT_SPEED;
            
            // Limit maximum horizontal velocity
            float horizSpeedSquared = velocity.x * velocity.x + velocity.z * velocity.z;
            if (horizSpeedSquared > MAX_VELOCITY * MAX_VELOCITY) {
                float scale = MAX_VELOCITY / (float) Math.sqrt(horizSpeedSquared);
                velocity.x *= scale;
                velocity.z *= scale;
            }
        }
        
        // Always apply gravity and vertical physics, even in free camera mode
        // Apply gravity when not on ground
        if (!isGrounded) {
            velocity.y -= GRAVITY * deltaTime; // Scale with delta time
        }
        
        // Move the bounding entity based on velocity (even in free camera mode)
        Vector3f movementVec = new Vector3f(velocity).mul(deltaTime); // Scale with delta time
        
        // In free camera mode, only apply vertical movement to the player entity
        if (freeCameraMode) {
            boundingEntity.incrementPosition(0, movementVec.y, 0);
        } else {
            boundingEntity.incrementPosition(movementVec.x, movementVec.y, movementVec.z);
        }
        
        // Check if player is on the ground
        // Use the bottom of the player capsule for collision detection
        Vector3f playerPos = boundingEntity.getPosition();
        float playerBottom = playerPos.y - BASE_Y_OFFSET;
        
        if (playerBottom <= GROUND_LEVEL) {
            // Collided with ground, reset position and vertical velocity
            isGrounded = true;
            velocity.y = 0;
            boundingEntity.setPosition(playerPos.x, GROUND_LEVEL + BASE_Y_OFFSET, playerPos.z);
        } else {
            // Not touching ground
            isGrounded = false;
        }
        
        // Update camera position to match player
        if (!freeCameraMode) {
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
    
    /**
     * Gets the player's camera coordinates.
     *
     * @return A Vector3f containing the camera's position coordinates
     */
    public Vector3f getPlayerCamera() {
        return camera.getPosition();
    }
    
    /**
     * Makes the player jump if they are currently on the ground.
     * Does nothing if the player is already in the air.
     */
    public void jump() {
        if (isGrounded && !freeCameraMode) {
            velocity.y = JUMP_POWER;
            isGrounded = false;
        }
    }
    
    /**
     * Checks if the player is currently on the ground.
     * 
     * @return True if the player is on the ground, false if airborne
     */
    public boolean isGrounded() {
        return isGrounded;
    }
}