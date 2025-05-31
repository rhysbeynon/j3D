package com.discardsoft.j3D.tools.jEdit.model;

import org.joml.Vector3f;

/**
 * Represents a 3D entity in the 2D editor.
 * 
 * This class stores the essential properties of a 3D entity that can be
 * edited in the 2D interface, including position, rotation, scale, and
 * the model reference.
 */
public class EditorEntity {
    
    private String id;
    private String name;
    private String modelName;
    private Vector3f position;
    private Vector3f rotation;
    private Vector3f scale;
    private boolean selected;
    private boolean visible;
    
    // Visual properties for 2D representation
    private float width;
    private float height;
    private float depth;
    
    public EditorEntity() {
        this("entity_" + System.currentTimeMillis(), "Untitled Entity", "P_Cube");
    }
    
    public EditorEntity(String id, String name, String modelName) {
        this.id = id;
        this.name = name;
        this.modelName = modelName;
        this.position = new Vector3f(0, 0, 0);
        this.rotation = new Vector3f(0, 0, 0);
        this.scale = new Vector3f(1, 1, 1);
        this.selected = false;
        this.visible = true;
        
        // Default dimensions - these could be loaded from model data
        updateDimensionsFromModel();
    }
    
    /**
     * Updates the entity's 2D representation dimensions based on the model.
     */
    private void updateDimensionsFromModel() {
        // Default dimensions for common models
        switch (modelName) {
            case "P_Cube":
                width = height = depth = 2.0f;
                break;
            case "P_Sphere_Small":
                width = height = depth = 1.0f;
                break;
            case "P_Sphere_Medium":
                width = height = depth = 2.0f;
                break;
            case "Suzanne":
                width = depth = 2.0f;
                height = 2.5f;
                break;
            case "PlayerCapsule":
                width = depth = 1.0f;
                height = 3.0f;
                break;
            default:
                width = height = depth = 1.0f;
                break;
        }
        
        // Apply scale to dimensions
        width *= scale.x;
        height *= scale.y;
        depth *= scale.z;
    }
    
    /**
     * Gets the 2D position for the specified view mode.
     */
    public float getX2D(ViewMode viewMode) {
        switch (viewMode) {
            case TOP: return position.x;
            case SIDE: return position.z;
            case FRONT: return position.x;
            default: return position.x;
        }
    }
    
    public float getY2D(ViewMode viewMode) {
        switch (viewMode) {
            case TOP: return -position.z; // Flip Z for screen coordinates
            case SIDE: return position.y;
            case FRONT: return position.y;
            default: return position.y;
        }
    }
    
    /**
     * Gets the 2D width for the specified view mode.
     */
    public float getWidth2D(ViewMode viewMode) {
        switch (viewMode) {
            case TOP: return width;
            case SIDE: return depth;
            case FRONT: return width;
            default: return width;
        }
    }
    
    /**
     * Gets the 2D height for the specified view mode.
     */
    public float getHeight2D(ViewMode viewMode) {
        switch (viewMode) {
            case TOP: return depth;
            case SIDE: return height;
            case FRONT: return height;
            default: return height;
        }
    }
    
    /**
     * Sets the 2D position and updates the 3D position accordingly.
     */
    public void setPosition2D(ViewMode viewMode, float x2d, float y2d) {
        switch (viewMode) {
            case TOP:
                position.x = x2d;
                position.z = -y2d; // Flip Z back
                break;
            case SIDE:
                position.z = x2d;
                position.y = y2d;
                break;
            case FRONT:
                position.x = x2d;
                position.y = y2d;
                break;
        }
    }
    
    /**
     * Checks if a 2D point is inside this entity's bounds.
     */
    public boolean contains2D(ViewMode viewMode, float x, float y) {
        float entityX = getX2D(viewMode);
        float entityY = getY2D(viewMode);
        float entityWidth = getWidth2D(viewMode);
        float entityHeight = getHeight2D(viewMode);
        
        return x >= entityX - entityWidth/2 && x <= entityX + entityWidth/2 &&
               y >= entityY - entityHeight/2 && y <= entityY + entityHeight/2;
    }
    
    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getModelName() { return modelName; }
    public void setModelName(String modelName) { 
        this.modelName = modelName;
        updateDimensionsFromModel();
    }
    
    public Vector3f getPosition() { return new Vector3f(position); }
    public void setPosition(Vector3f position) { 
        this.position.set(position);
    }
    
    public Vector3f getRotation() { return new Vector3f(rotation); }
    public void setRotation(Vector3f rotation) { 
        this.rotation.set(rotation);
    }
    
    public Vector3f getScale() { return new Vector3f(scale); }
    public void setScale(Vector3f scale) { 
        this.scale.set(scale);
        updateDimensionsFromModel();
    }
    
    public boolean isSelected() { return selected; }
    public void setSelected(boolean selected) { this.selected = selected; }
    
    public boolean isVisible() { return visible; }
    public void setVisible(boolean visible) { this.visible = visible; }
    
    public float getWidth() { return width; }
    public float getHeight() { return height; }
    public float getDepth() { return depth; }
    
    @Override
    public String toString() {
        return name + " (" + modelName + ")";
    }
}
