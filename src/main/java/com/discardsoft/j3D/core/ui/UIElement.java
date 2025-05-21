package com.discardsoft.j3D.core.ui;

import com.discardsoft.j3D.core.entity.Model;
import com.discardsoft.j3D.core.entity.Texture;
import com.discardsoft.j3D.core.WindowManager;
import com.discardsoft.j3D.core.utils.Settings;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

/**
 * Base class for all UI elements in the j3D engine.
 * <p>
 * Provides common functionality for position, size, rendering,
 * and parent-child relationships for UI components.
 * </p>
 *
 * @author DiscardSoft
 * @version 0.1
 */
public abstract class UIElement {
    
    /** Position in normalized screen coordinates (-1 to 1) */
    protected float x, y;
    
    /** Size in normalized screen coordinates (0 to 2) */
    protected float width, height;
    
    /** Z-index for rendering order (higher values render on top) */
    protected int zIndex;
    
    /** Visibility flag */
    protected boolean visible = true;
    
    /** Transparency flag */
    protected boolean transparent;
    
    /** Model for rendering this element */
    protected Model model;
    
    /** Texture for this element */
    protected Texture texture;
    
    /** Model matrix for this element's position and scale */
    protected Matrix4f modelMatrix;
    
    /** Parent-child relationship for hierarchical structure */
    protected UIElement parent;
    protected List<UIElement> children;
    
    /** Original image dimensions (for aspect ratio calculation) */
    protected int imageWidth = 1, imageHeight = 1;
    
    /** Flag indicating if we have valid image dimensions */
    protected boolean hasImageDimensions = false;
    
    /**
     * Creates a new UI element.
     * 
     * @param texture The texture to use for this element
     * @param zIndex The z-index for rendering order
     */
    public UIElement(Texture texture, int zIndex) {
        this.texture = texture;
        this.zIndex = zIndex;
        this.transparent = texture.hasTransparency();
        this.children = new ArrayList<>();
        this.modelMatrix = new Matrix4f();
        
        // Default to centered position
        this.x = 0;
        this.y = 0;
        
        // Calculate aspect-ratio correct size based on texture
        calculateSizeFromTexture();
    }
    
    /**
     * Initializes the UI element with texture and z-index.
     * Used by subclasses that need to load textures asynchronously.
     * 
     * @param texture The texture to use for this element
     * @param zIndex The z-index for rendering order
     */
    protected void init(Texture texture, int zIndex) {
        this.texture = texture;
        this.zIndex = zIndex;
        this.transparent = texture.hasTransparency();
        this.children = new ArrayList<>();
        this.modelMatrix = new Matrix4f();
        
        // Default to centered position
        this.x = 0;
        this.y = 0;
        
        // Calculate aspect-ratio correct size based on texture
        calculateSizeFromTexture();
    }
    
    /**
     * Calculates appropriate size based on texture dimensions.
     * Uses the actual image dimensions when available or falls back to a default ratio.
     */
    protected void calculateSizeFromTexture() {
        if (hasImageDimensions) {
            // Use actual image aspect ratio
            float aspectRatio = (float)imageWidth / imageHeight;
            
            // Calculate width from height to maintain aspect ratio
            this.width = 1.0f;
            // Default to a height of 1.0 (half the screen height)
            this.height = this.width / aspectRatio;
        } else {
            // Fall back to default 1:1 aspect ratio if we don't have image dimensions
            this.height = 0.5f;
            this.width = 0.5f;
        }
        
        // Recreate model with new dimensions
        createModel();
        updateModelMatrix();
    }
    
    /**
     * Creates the model for this UI element.
     * This method should be implemented by subclasses.
     */
    protected abstract void createModel();
    
    /**
     * Updates the model matrix based on position and size.
     */
    protected void updateModelMatrix() {
        modelMatrix.identity()
                .translate(new Vector3f(x, y, 0))
                .scale(new Vector3f(width, height, 1.0f));
    }
    
    /**
     * Renders this UI element.
     * This method should be implemented by subclasses.
     */
    public abstract void render();
    
    /**
     * Updates this UI element and processes input.
     * 
     * @param window The window manager for input processing
     */
    public void update(WindowManager window) {
        // Update all children
        for (UIElement child : children) {
            if (child.isVisible()) {
                child.update(window);
            }
        }
    }
    
    /**
     * Sets the position of this UI element.
     * 
     * @param x The x-coordinate in normalized screen coordinates (-1 to 1)
     * @param y The y-coordinate in normalized screen coordinates (-1 to 1)
     */
    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
        updateModelMatrix();
    }
    
    /**
     * Sets the size of this UI element.
     * 
     * @param width The width in normalized screen coordinates
     * @param height The height in normalized screen coordinates
     */
    public void setSize(float width, float height) {
        this.width = width;
        this.height = height;
        updateModelMatrix();
    }
    
    /**
     * Sets the size based on a desired height while preserving aspect ratio.
     * Uses actual image dimensions when available.
     * 
     * @param height The desired height
     */
    public void setSizeFromHeight(float height) {
        this.height = height;
        
        if (hasImageDimensions) {
            // Calculate width based on image aspect ratio
            float aspectRatio = (float)imageWidth / imageHeight;
            this.width = height * aspectRatio;
        } else {
            // Fall back to 1:1 aspect ratio
            this.width = height;
        }
        
        updateModelMatrix();
    }
    
    /**
     * Sets the size based on a desired width while preserving aspect ratio.
     * Uses actual image dimensions when available.
     * 
     * @param width The desired width
     */
    public void setSizeFromWidth(float width) {
        this.width = width;
        
        if (hasImageDimensions) {
            // Calculate height based on image aspect ratio
            float aspectRatio = (float)imageWidth / imageHeight;
            this.height = this.width / aspectRatio;
        } else {
            // Fall back to 1:1 aspect ratio
            this.height = width;
        }
        
        updateModelMatrix();
    }
    
    /**
     * Adds a child UI element to this element.
     * 
     * @param child The child UI element to add
     */
    public void addChild(UIElement child) {
        children.add(child);
        child.parent = this;
    }
    
    /**
     * Removes a child UI element from this element.
     * 
     * @param child The child UI element to remove
     */
    public void removeChild(UIElement child) {
        children.remove(child);
        child.parent = null;
    }
    
    /**
     * Gets the z-index of this UI element.
     * 
     * @return The z-index
     */
    public int getZIndex() {
        return zIndex;
    }
    
    /**
     * Sets the z-index of this UI element.
     * 
     * @param zIndex The new z-index
     */
    public void setZIndex(int zIndex) {
        this.zIndex = zIndex;
    }
    
    /**
     * Sets the visibility of this UI element.
     * 
     * @param visible true to make the element visible, false to hide it
     */
    public void setVisible(boolean visible) {
        this.visible = visible;
    }
    
    /**
     * Checks if this UI element is visible.
     * 
     * @return true if the element is visible, false otherwise
     */
    public boolean isVisible() {
        return visible;
    }
    
    /**
     * Checks if this UI element has transparency.
     * 
     * @return true if the element has transparency, false otherwise
     */
    public boolean hasTransparency() {
        return transparent;
    }
    
    /**
     * Sets whether this UI element has transparency.
     * 
     * @param transparent true if the element has transparency, false otherwise
     */
    public void setTransparent(boolean transparent) {
        this.transparent = transparent;
    }
    
    /**
     * Gets the model matrix for this UI element.
     * 
     * @return The model matrix
     */
    public Matrix4f getModelMatrix() {
        return modelMatrix;
    }
    
    /**
     * Gets the position of this UI element.
     * 
     * @return A Vector2f containing the position
     */
    public Vector2f getPosition() {
        return new Vector2f(x, y);
    }
    
    /**
     * Gets the size of this UI element.
     * 
     * @return A Vector2f containing the size
     */
    public Vector2f getSize() {
        return new Vector2f(width, height);
    }
    
    /**
     * Gets the texture of this UI element.
     * 
     * @return The texture
     */
    public Texture getTexture() {
        return texture;
    }
    
    /**
     * Gets the children of this UI element.
     * 
     * @return List of child UI elements
     */
    public List<UIElement> getChildren() {
        return children;
    }
    
    /**
     * Releases resources used by this UI element.
     */
    public void cleanup() {
        // Model cleanup is handled by the ObjectLoader, we just need to clean up children
        
        // Clean up children
        for (UIElement child : children) {
            child.cleanup();
        }
    }
}