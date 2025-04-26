package com.discardsoft.j3D.core.utils;

import com.discardsoft.j3D.Main;
import com.discardsoft.j3D.core.entity.Camera;
import com.discardsoft.j3D.core.entity.Entity;
import org.joml.Matrix4f;
import org.joml.Vector3f;

/**
 * Utility class for 3D transformation calculations.
 * <p>
 * Provides methods to create transformation matrices for entities and cameras,
 * converting their position, rotation, and scale properties into matrices
 * that can be used by shaders for rendering.
 * </p>
 * 
 * @author DISCVRD Software
 * @version 0.1
 */
public final class Transformation {
    
    /**
     * Private constructor to prevent instantiation of utility class.
     */
    private Transformation() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    /**
     * Creates a transformation matrix for an entity.
     * <p>
     * Combines the entity's position, rotation, and scale into a single 4x4 matrix
     * that transforms the entity's model from model space to world space.
     * </p>
     *
     * @param entity The entity to create a transformation matrix for
     * @return A matrix representing the entity's transformation
     */
    public static Matrix4f createTransformationMatrix(Entity entity) {
        Matrix4f matrix = new Matrix4f();
        Vector3f position = entity.getPosition();
        Vector3f rotation = entity.getRotation();
        Vector3f scale = entity.getScale();
        
        // Translate to the entity position first (common for all modes)
        matrix.identity().translate(position);
        
        // Handle different billboard modes
        if (!entity.isBillboardY() && !entity.isBillboardFull()) {
            // Standard rotation (no billboarding)
            matrix.rotateX((float) Math.toRadians(rotation.x))
                  .rotateY((float) Math.toRadians(rotation.y))
                  .rotateZ((float) Math.toRadians(rotation.z));
        } else {
            // We need camera information for billboarding
            Camera camera = Main.getCurrentCamera();
            if (camera != null) {
                if (entity.isBillboardY()) {
                    // Y-axis only billboarding (horizontal rotation only)
                    // Calculate the angle between the camera and entity in the XZ plane
                    Vector3f cameraPos = camera.getPosition();
                    float dx = cameraPos.x - position.x;
                    float dz = cameraPos.z - position.z;
                    float angleY = (float) Math.toDegrees(Math.atan2(dx, dz));
                    
                    // Apply the calculated Y rotation, but keep entity's original X and Z rotations
                    matrix.rotateX((float) Math.toRadians(rotation.x))
                          .rotateY((float) Math.toRadians(angleY))
                          .rotateZ((float) Math.toRadians(rotation.z));
                } else if (entity.isBillboardFull()) {
                    // Full billboarding - entity always fully faces the camera
                    // Get camera position
                    Vector3f cameraPos = camera.getPosition();
                    Vector3f direction = new Vector3f();
                    
                    // Calculate direction from entity to camera
                    direction.x = cameraPos.x - position.x;
                    direction.y = cameraPos.y - position.y;
                    direction.z = cameraPos.z - position.z;
                    direction.normalize();
                    
                    // Create a look-at matrix for the entity to face the camera
                    // Calculate right vector as cross product of up and direction
                    Vector3f right = new Vector3f();
                    Vector3f up = new Vector3f(0, 1, 0); // World up
                    right.x = direction.z;
                    right.y = 0;
                    right.z = -direction.x;
                    right.normalize();
                    
                    // Calculate corrected up vector to ensure orthogonality
                    Vector3f correctedUp = new Vector3f();
                    correctedUp.x = direction.y * right.z - direction.z * right.y;
                    correctedUp.y = direction.z * right.x - direction.x * right.z;
                    correctedUp.z = direction.x * right.y - direction.y * right.x;
                    correctedUp.normalize();
                    
                    // Create a rotation matrix based on these vectors (right, up, forward)
                    // Using set() method instead of constructor with array
                    Matrix4f lookAt = new Matrix4f();
                    lookAt.set(
                        right.x, right.y, right.z, 0,
                        correctedUp.x, correctedUp.y, correctedUp.z, 0,
                        -direction.x, -direction.y, -direction.z, 0,
                        0, 0, 0, 1
                    );
                    
                    // Apply this rotation to our transformation
                    matrix.mul(lookAt);
                }
            }
        }
        
        // Scale is always applied last
        matrix.scale(scale);
        
        return matrix;
    }

    /**
     * Creates a view matrix for a camera.
     * <p>
     * Calculates the inverse of the camera's transformation, which is used
     * to transform world-space coordinates into view space (from the camera's perspective).
     * </p>
     *
     * @param camera The camera to create a view matrix for
     * @return A matrix representing the camera's view transformation
     */
    public static Matrix4f getViewMatrix(Camera camera) {
        Vector3f position = camera.getPosition();
        Vector3f rotation = camera.getRotation();
        
        // Start with identity matrix
        Matrix4f matrix = new Matrix4f().identity();
        
        // Apply camera rotation (in reverse order compared to entity rotation)
        matrix.rotate((float) Math.toRadians(rotation.x), new Vector3f(1, 0, 0))
              .rotate((float) Math.toRadians(rotation.y), new Vector3f(0, 1, 0))
              .rotate((float) Math.toRadians(rotation.z), new Vector3f(0, 0, 1));
        
        // Apply camera translation (with negative values to move the world in the opposite direction)
        matrix.translate(-position.x, -position.y, -position.z);
        
        return matrix;
    }
}
