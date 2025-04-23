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

    /**
     * Constructs a new texture with the specified OpenGL texture ID.
     *
     * @param textureId The OpenGL texture ID
     */
    public Texture(int textureId) {
        this.textureId = textureId;
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
     * @deprecated Use {@link #getTextureId()} instead
     */
    @Deprecated
    public int getId() {
        return getTextureId();
    }
}
