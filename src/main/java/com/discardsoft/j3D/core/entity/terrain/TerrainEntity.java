package com.discardsoft.j3D.core.entity.terrain;

import com.discardsoft.j3D.core.entity.Entity;
import com.discardsoft.j3D.core.entity.terrain.Terrain;
import org.joml.Vector3f;

/**
 * Entity wrapper for terrain objects.
 * <p>
 * Creates an entity from terrain data for rendering in the scene.
 * </p>
 * 
 * @author DISCVRD Software
 * @version 0.1
 */
public class TerrainEntity extends Entity {
    
    /** The terrain data */
    private final Terrain terrain;
    
    /**
     * Creates a new terrain entity from a terrain object.
     *
     * @param terrain The terrain object
     */
    public TerrainEntity(Terrain terrain) {
        // Create an entity with the terrain's model, position, and no initial rotation
        super(
            terrain.getModel(), 
            terrain.getPosition(),
            new Vector3f(0, 0, 0),  // No rotation
            new Vector3f(1, 1, 1)   // No scaling
        );
        this.terrain = terrain;
    }
    
    /**
     * Gets the underlying terrain object.
     *
     * @return The terrain data
     */
    public Terrain getTerrain() {
        return terrain;
    }
}