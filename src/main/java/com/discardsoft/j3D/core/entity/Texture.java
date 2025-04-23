package com.discardsoft.j3D.core.entity;

/**
 * Represents a texture that can be applied to a 3D model.
 * <p>
 * A texture is identified by its OpenGL texture ID, which points to the
 * texture data loaded into the GPU memory.
 * </p>
 * 
 * @author DISCVRD Software
 * @version 0.1
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
