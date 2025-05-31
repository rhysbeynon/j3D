package com.discardsoft.j3D.tools.jEdit.model;

import com.discardsoft.j3D.core.level.LevelLoader;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import org.joml.Vector3f;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Core data model for the level editor.
 * 
 * This class manages all entities in the level, handles selection state,
 * and provides serialization to/from both jEDIT format and j3D format.
 */
public class LevelModel {
    
    private List<EditorEntity> entities;
    private List<EditorEntity> selectedEntities;
    private List<ChangeListener> changeListeners;
    private Gson gson;
    
    // Level metadata
    private String levelName;
    private String levelDescription;
    private String author;
    
    public LevelModel() {
        this.entities = new ArrayList<>();
        this.selectedEntities = new ArrayList<>();
        this.changeListeners = new ArrayList<>();
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        
        // Default metadata
        this.levelName = "Untitled Level";
        this.levelDescription = "";
        this.author = System.getProperty("user.name", "Unknown");
    }
    
    /**
     * Adds an entity to the level.
     */
    public void addEntity(EditorEntity entity) {
        entities.add(entity);
        fireChangeEvent();
    }
    
    /**
     * Removes an entity from the level.
     */
    public void removeEntity(EditorEntity entity) {
        entities.remove(entity);
        selectedEntities.remove(entity);
        fireChangeEvent();
    }
    
    /**
     * Gets all entities in the level.
     */
    public List<EditorEntity> getEntities() {
        return new ArrayList<>(entities);
    }
    
    /**
     * Gets all selected entities.
     */
    public List<EditorEntity> getSelectedEntities() {
        return new ArrayList<>(selectedEntities);
    }
    
    /**
     * Selects an entity.
     */
    public void selectEntity(EditorEntity entity, boolean addToSelection) {
        if (!addToSelection) {
            clearSelection();
        }
        
        if (!selectedEntities.contains(entity)) {
            selectedEntities.add(entity);
            entity.setSelected(true);
            fireChangeEvent();
        }
    }
    
    /**
     * Deselects an entity.
     */
    public void deselectEntity(EditorEntity entity) {
        if (selectedEntities.remove(entity)) {
            entity.setSelected(false);
            fireChangeEvent();
        }
    }
    
    /**
     * Clears all selection.
     */
    public void clearSelection() {
        for (EditorEntity entity : selectedEntities) {
            entity.setSelected(false);
        }
        selectedEntities.clear();
        fireChangeEvent();
    }
    
    /**
     * Selects all entities.
     */
    public void selectAll() {
        selectedEntities.clear();
        for (EditorEntity entity : entities) {
            entity.setSelected(true);
            selectedEntities.add(entity);
        }
        fireChangeEvent();
    }
    
    /**
     * Deletes all selected entities.
     */
    public void deleteSelected() {
        entities.removeAll(selectedEntities);
        selectedEntities.clear();
        fireChangeEvent();
    }
    
    /**
     * Finds entity at the specified 2D coordinates.
     */
    public EditorEntity getEntityAt(ViewMode viewMode, float x, float y) {
        // Search in reverse order to get topmost entity
        for (int i = entities.size() - 1; i >= 0; i--) {
            EditorEntity entity = entities.get(i);
            if (entity.isVisible() && entity.contains2D(viewMode, x, y)) {
                return entity;
            }
        }
        return null;
    }
    
    /**
     * Clears the level.
     */
    public void clear() {
        entities.clear();
        selectedEntities.clear();
        levelName = "Untitled Level";
        levelDescription = "";
        fireChangeEvent();
    }
    
    /**
     * Saves the level to a jEDIT format file.
     */
    public void saveToFile(File file) throws IOException {
        JsonObject levelData = new JsonObject();
        
        // Metadata
        JsonObject metadata = new JsonObject();
        metadata.addProperty("name", levelName);
        metadata.addProperty("description", levelDescription);
        metadata.addProperty("author", author);
        metadata.addProperty("version", "1.0");
        metadata.addProperty("created", java.time.Instant.now().toString());
        levelData.add("metadata", metadata);
        
        // Entities
        JsonArray entitiesArray = new JsonArray();
        for (EditorEntity entity : entities) {
            JsonObject entityData = new JsonObject();
            entityData.addProperty("id", entity.getId());
            entityData.addProperty("name", entity.getName());
            entityData.addProperty("model", entity.getModelName());
            
            // Position
            JsonObject position = new JsonObject();
            position.addProperty("x", entity.getPosition().x);
            position.addProperty("y", entity.getPosition().y);
            position.addProperty("z", entity.getPosition().z);
            entityData.add("position", position);
            
            // Rotation
            JsonObject rotation = new JsonObject();
            rotation.addProperty("x", entity.getRotation().x);
            rotation.addProperty("y", entity.getRotation().y);
            rotation.addProperty("z", entity.getRotation().z);
            entityData.add("rotation", rotation);
            
            // Scale
            JsonObject scale = new JsonObject();
            scale.addProperty("x", entity.getScale().x);
            scale.addProperty("y", entity.getScale().y);
            scale.addProperty("z", entity.getScale().z);
            entityData.add("scale", scale);
            
            entitiesArray.add(entityData);
        }
        levelData.add("entities", entitiesArray);
        
        // Write to file
        try (FileWriter writer = new FileWriter(file)) {
            gson.toJson(levelData, writer);
        }
    }
    
    /**
     * Loads a level from a jEDIT format file.
     */
    public void loadFromFile(File file) throws IOException {
        try (FileReader reader = new FileReader(file)) {
            JsonObject levelData = gson.fromJson(reader, JsonObject.class);
            
            clear();
            
            // Load metadata
            if (levelData.has("metadata")) {
                JsonObject metadata = levelData.getAsJsonObject("metadata");
                levelName = metadata.has("name") ? metadata.get("name").getAsString() : "Untitled Level";
                levelDescription = metadata.has("description") ? metadata.get("description").getAsString() : "";
                author = metadata.has("author") ? metadata.get("author").getAsString() : "Unknown";
            }
            
            // Load entities
            if (levelData.has("entities")) {
                JsonArray entitiesArray = levelData.getAsJsonArray("entities");
                for (int i = 0; i < entitiesArray.size(); i++) {
                    JsonObject entityData = entitiesArray.get(i).getAsJsonObject();
                    
                    String id = entityData.get("id").getAsString();
                    String name = entityData.get("name").getAsString();
                    String model = entityData.get("model").getAsString();
                    
                    EditorEntity entity = new EditorEntity(id, name, model);
                    
                    // Position
                    if (entityData.has("position")) {
                        JsonObject pos = entityData.getAsJsonObject("position");
                        entity.setPosition(new Vector3f(
                            pos.get("x").getAsFloat(),
                            pos.get("y").getAsFloat(),
                            pos.get("z").getAsFloat()
                        ));
                    }
                    
                    // Rotation
                    if (entityData.has("rotation")) {
                        JsonObject rot = entityData.getAsJsonObject("rotation");
                        entity.setRotation(new Vector3f(
                            rot.get("x").getAsFloat(),
                            rot.get("y").getAsFloat(),
                            rot.get("z").getAsFloat()
                        ));
                    }
                    
                    // Scale
                    if (entityData.has("scale")) {
                        JsonObject scale = entityData.getAsJsonObject("scale");
                        entity.setScale(new Vector3f(
                            scale.get("x").getAsFloat(),
                            scale.get("y").getAsFloat(),
                            scale.get("z").getAsFloat()
                        ));
                    }
                    
                    entities.add(entity);
                }
            }
            
            fireChangeEvent();
        }
    }
    
    /**
     * Exports the level to j3D format.
     */
    public void exportToJ3D(File file) throws IOException {
        // Create a LevelLoader.LoadedLevel structure
        LevelLoader.LoadedLevel level = new LevelLoader.LoadedLevel();
        
        // Metadata
        level.metadata = new LevelLoader.LevelMetadata();
        level.metadata.name = levelName;
        level.metadata.description = levelDescription;
        level.metadata.author = author;
        level.metadata.version = "1.0";
        level.metadata.created = java.time.Instant.now().toString();
        level.metadata.modified = java.time.Instant.now().toString();
        
        // Entities
        level.entities = new ArrayList<>();
        for (EditorEntity editorEntity : entities) {
            // Create dummy JsonObject for original data
            JsonObject originalData = new JsonObject();
            originalData.addProperty("id", editorEntity.getId());
            originalData.addProperty("name", editorEntity.getName());
            originalData.addProperty("model", editorEntity.getModelName());
            
            // Create j3D entity (we can't actually create it without the full engine)
            // For now, we'll create the JSON structure directly
            LevelLoader.LoadedEntity loadedEntity = new LevelLoader.LoadedEntity(null, originalData);
            level.entities.add(loadedEntity);
        }
        
        // For now, save as JSON directly since we don't have access to full LevelLoader
        JsonObject levelData = new JsonObject();
        
        // Metadata
        JsonObject metadata = new JsonObject();
        metadata.addProperty("name", levelName);
        metadata.addProperty("description", levelDescription);
        metadata.addProperty("author", author);
        metadata.addProperty("version", "1.0");
        metadata.addProperty("created", java.time.Instant.now().toString());
        metadata.addProperty("modified", java.time.Instant.now().toString());
        levelData.add("metadata", metadata);
        
        // Default lighting
        JsonObject lighting = new JsonObject();
        JsonObject lightPos = new JsonObject();
        lightPos.addProperty("x", 0.0f);
        lightPos.addProperty("y", 10.0f);
        lightPos.addProperty("z", 0.0f);
        lighting.add("position", lightPos);
        
        JsonObject lightColor = new JsonObject();
        lightColor.addProperty("r", 1.0f);
        lightColor.addProperty("g", 1.0f);
        lightColor.addProperty("b", 1.0f);
        lighting.add("color", lightColor);
        
        JsonObject ambientColor = new JsonObject();
        ambientColor.addProperty("r", 0.3f);
        ambientColor.addProperty("g", 0.3f);
        ambientColor.addProperty("b", 0.3f);
        lighting.add("ambient", ambientColor);
        levelData.add("lighting", lighting);
        
        // Terrain (disabled)
        JsonObject terrain = new JsonObject();
        terrain.addProperty("enabled", false);
        levelData.add("terrain", terrain);
        
        // Entities
        JsonArray entitiesArray = new JsonArray();
        for (EditorEntity entity : entities) {
            JsonObject entityData = new JsonObject();
            entityData.addProperty("id", entity.getId());
            entityData.addProperty("name", entity.getName());
            entityData.addProperty("model", entity.getModelName());
            
            // Position
            JsonObject position = new JsonObject();
            position.addProperty("x", entity.getPosition().x);
            position.addProperty("y", entity.getPosition().y);
            position.addProperty("z", entity.getPosition().z);
            entityData.add("position", position);
            
            // Rotation
            JsonObject rotation = new JsonObject();
            rotation.addProperty("x", entity.getRotation().x);
            rotation.addProperty("y", entity.getRotation().y);
            rotation.addProperty("z", entity.getRotation().z);
            entityData.add("rotation", rotation);
            
            // Scale
            JsonObject scale = new JsonObject();
            scale.addProperty("x", entity.getScale().x);
            scale.addProperty("y", entity.getScale().y);
            scale.addProperty("z", entity.getScale().z);
            entityData.add("scale", scale);
            
            // Properties
            JsonObject properties = new JsonObject();
            properties.addProperty("hasTransparentTexture", false);
            properties.addProperty("billboardY", false);
            properties.addProperty("billboardFull", false);
            entityData.add("properties", properties);
            
            entitiesArray.add(entityData);
        }
        levelData.add("entities", entitiesArray);
        
        // Empty spawns array
        levelData.add("spawns", new JsonArray());
        
        // Write to file
        try (FileWriter writer = new FileWriter(file)) {
            gson.toJson(levelData, writer);
        }
    }
    
    /**
     * Imports a level from j3D format.
     */
    public void importFromJ3D(File file) throws IOException {
        try (FileReader reader = new FileReader(file)) {
            JsonObject levelData = gson.fromJson(reader, JsonObject.class);
            
            clear();
            
            // Load metadata
            if (levelData.has("metadata")) {
                JsonObject metadata = levelData.getAsJsonObject("metadata");
                levelName = metadata.has("name") ? metadata.get("name").getAsString() : "Imported Level";
                levelDescription = metadata.has("description") ? metadata.get("description").getAsString() : "";
                author = metadata.has("author") ? metadata.get("author").getAsString() : "Unknown";
            }
            
            // Load entities
            if (levelData.has("entities")) {
                JsonArray entitiesArray = levelData.getAsJsonArray("entities");
                for (int i = 0; i < entitiesArray.size(); i++) {
                    JsonObject entityData = entitiesArray.get(i).getAsJsonObject();
                    
                    String id = entityData.has("id") ? entityData.get("id").getAsString() : "entity_" + i;
                    String name = entityData.has("name") ? entityData.get("name").getAsString() : "Entity " + i;
                    String model = entityData.has("model") ? entityData.get("model").getAsString() : "P_Cube";
                    
                    EditorEntity entity = new EditorEntity(id, name, model);
                    
                    // Position
                    if (entityData.has("position")) {
                        JsonObject pos = entityData.getAsJsonObject("position");
                        entity.setPosition(new Vector3f(
                            pos.get("x").getAsFloat(),
                            pos.get("y").getAsFloat(),
                            pos.get("z").getAsFloat()
                        ));
                    }
                    
                    // Rotation
                    if (entityData.has("rotation")) {
                        JsonObject rot = entityData.getAsJsonObject("rotation");
                        entity.setRotation(new Vector3f(
                            rot.get("x").getAsFloat(),
                            rot.get("y").getAsFloat(),
                            rot.get("z").getAsFloat()
                        ));
                    }
                    
                    // Scale
                    if (entityData.has("scale")) {
                        JsonObject scale = entityData.getAsJsonObject("scale");
                        entity.setScale(new Vector3f(
                            scale.get("x").getAsFloat(),
                            scale.get("y").getAsFloat(),
                            scale.get("z").getAsFloat()
                        ));
                    }
                    
                    entities.add(entity);
                }
            }
            
            fireChangeEvent();
        }
    }
    
    // Event handling
    public void addChangeListener(ChangeListener listener) {
        changeListeners.add(listener);
    }
    
    public void removeChangeListener(ChangeListener listener) {
        changeListeners.remove(listener);
    }
    
    private void fireChangeEvent() {
        ChangeEvent event = new ChangeEvent(this);
        for (ChangeListener listener : changeListeners) {
            listener.stateChanged(event);
        }
    }
    
    // Getters and setters for metadata
    public String getLevelName() { return levelName; }
    public void setLevelName(String levelName) { 
        this.levelName = levelName; 
        fireChangeEvent();
    }
    
    public String getLevelDescription() { return levelDescription; }
    public void setLevelDescription(String levelDescription) { 
        this.levelDescription = levelDescription;
        fireChangeEvent();
    }
    
    public String getAuthor() { return author; }
    public void setAuthor(String author) { 
        this.author = author;
        fireChangeEvent();
    }
}
