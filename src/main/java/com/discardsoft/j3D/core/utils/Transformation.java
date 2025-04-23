package com.discardsoft.j3D.core.utils;

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
        
        return matrix.identity()
            .translate(position)
            .rotateX((float) Math.toRadians(rotation.x))
            .rotateY((float) Math.toRadians(rotation.y))
            .rotateZ((float) Math.toRadians(rotation.z))
            .scale(scale);
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
