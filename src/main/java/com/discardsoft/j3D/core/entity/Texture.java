package com.discardsoft.j3D.core.entity;

/**
 * Represents a texture used for rendering 3D models.
 * <p>
 * This class wraps an OpenGL texture ID and filename for managing textures.
 * </p>
 *
 * @author DiscardSoft
 * @version 0.1
* @see Model
 */
public class Texture {

    /** The OpenGL texture ID */
    private final int textureId;
    
    /** The texture name/path (used for identification) */
    private String name;

    /**
     * Constructs a new texture with the specified OpenGL texture ID.
     *
     * @param textureId The OpenGL texture ID
     */
    public Texture(int textureId) {
        this.textureId = textureId;
    }
    
    /**
     * Constructs a new texture with the specified OpenGL texture ID and name.
     *
     * @param textureId The OpenGL texture ID
     * @param name The texture name or path
     */
    public Texture(int textureId, String name) {
        this.textureId = textureId;
        this.name = name;
    }

    /**
     * Gets the OpenGL texture ID.
     *
     * @return The texture ID
     */
    public int getTextureId() {
        return textureId;
    }
    
    /**
     * Gets the texture name or path.
     *
     * @return The texture name
     */
    public String getName() {
        return name;
    }
    
    /**
     * Sets the texture name or path.
     *
     * @param name The texture name or path
     */
    public void setName(String name) {
        this.name = name;
    }
    
    /**
     * @deprecated Use {@link #getTextureId()} instead
     */
    @Deprecated
    public int getId() {
        return getTextureId();
    }

    /**
     * Checks if this texture has transparency.
     * <p>
     * Transparency is determined by examining the texture name.
     * Textures with names containing "T_" prefix or certain keywords
     * are considered to have transparency.
     * </p>
     * 
     * @return true if the texture has transparency, false otherwise
     */
    public boolean hasTransparency() {
        if (name == null) {
            return false;
        }
        
        String lowerName = name.toLowerCase();
        return lowerName.startsWith("t_") || 
               lowerName.contains("grass") ||
               lowerName.contains("transparent") || 
               lowerName.contains("menu");
    }
}
