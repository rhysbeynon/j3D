package com.discardsoft.j3D.core.scene;

import com.discardsoft.j3D.core.entity.Entity;
import com.discardsoft.j3D.core.entity.Light;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Abstract base class for all scenes in the engine.
 * <p>
 * Provides common functionality for scene management including entity tracking,
 * lighting setup, and scene lifecycle management.
 * </p>
 *
 * @author DiscardSoft
 * @version 0.1
 */
public abstract class BaseScene implements IScene {
    
    /** List of entities in the scene */
    private final List<Entity> entities;
    
    /** Light source for the scene */
    private final Light light;

    /**
     * Constructs a new base scene.
     * <p>
     * Initializes the entity collection and creates a default light source.
     * </p>
     */
    public BaseScene() {
        entities = new ArrayList<>();
        light = new Light(
            new Vector3f(10.0f, 10.0f, 10.0f),  // position
            new Vector3f(1.0f, 1.0f, 1.0f),     // color (white)
            new Vector3f(0.05f, 0.1f, 0.1f)     // ambient
        );
    }

    /**
     * Constructs a new base scene with a custom light.
     * <p>
     * Initializes the entity collection and uses the provided light source.
     * </p>
     * 
     * @param light The light source for this scene
     */
    public BaseScene(Light light) {
        entities = new ArrayList<>();
        this.light = light;
    }

    @Override
    public void addEntity(Entity entity) {
        entities.add(entity);
    }

    @Override
    public void removeEntity(Entity entity) {
        entities.remove(entity);
    }

    @Override
    public List<Entity> getEntities() {
        return Collections.unmodifiableList(entities);
    }

    @Override
    public Light getLight() {
        return light;
    }
    
    /**
     * Gets the mutable list of entities for subclasses.
     * <p>
     * This method is protected to allow subclasses to directly manipulate
     * the entity list while still providing an immutable view to external callers.
     * </p>
     * 
     * @return The mutable list of entities
     */
    protected List<Entity> getMutableEntities() {
        return entities;
    }

    @Override
    public void update(float deltaTime) {
        // Default implementation applies rotation to all entities
        // Subclasses can override this for custom behavior
        for (Entity entity : entities) {
            entity.incrementRotation(0.0f, 15.0f * deltaTime, 0.0f);
        }
    }

    @Override
    public void cleanup() {
        // Default implementation is empty
        // Subclasses should override if they need to clean up resources
    }
}