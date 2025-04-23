package com.discardsoft.j3D.core.scene;

import com.discardsoft.j3D.core.entity.Entity;
import com.discardsoft.j3D.core.entity.Light;

import java.util.List;

/**
 * Interface defining the structure and behavior of a scene in the j3D engine.
 * <p>
 * A scene manages a collection of entities and lighting that make up a 
 * rendered environment. Implementations should handle entity management,
 * scene-specific logic, and resource lifecycle.
 * </p>
 * 
 * @author DISCVRD Software
 * @version 0.1
 */
public interface IScene {
    
    /**
     * Initializes the scene with default entities and settings.
     * <p>
     * This method should be called once after scene creation to set up
     * the initial state, load models, create entities, and configure lighting.
     * </p>
     */
    void initialize();
    
    /**
     * Updates all entities and scene-specific logic.
     * <p>
     * This method is called once per frame to update the state of all
     * entities and other dynamic elements in the scene.
     * </p>
     *
     * @param deltaTime The time elapsed since the last update in seconds
     */
    void update(float deltaTime);
    
    /**
     * Adds an entity to the scene.
     *
     * @param entity The entity to add
     */
    void addEntity(Entity entity);
    
    /**
     * Removes an entity from the scene.
     *
     * @param entity The entity to remove
     */
    void removeEntity(Entity entity);
    
    /**
     * Gets all entities in the scene.
     *
     * @return An unmodifiable list of all entities
     */
    List<Entity> getEntities();
    
    /**
     * Gets the scene's light source.
     *
     * @return The light source
     */
    Light getLight();
    
    /**
     * Cleans up resources used by the scene.
     * <p>
     * This should be called when the scene is no longer needed to
     * release any resources held by the scene or its entities.
     * </p>
     */
    void cleanup();
}