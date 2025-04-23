package com.discardsoft.j3D.core.entity;

/**
 * Represents a 3D model that can be rendered in the game world.
 * <p>
 * A model consists of a vertex array object (VAO) ID, vertex count, and an optional texture.
 * The VAO ID points to the OpenGL vertex array object that contains the model's geometry data.
 * </p>
 * 
 * @author DISCVRD Software
 * @version 0.1
 * @see Texture
 */
public class Model {

    /** The OpenGL VAO ID for this model */
    private final int vaoId;
    
    /** The number of vertices in this model */
    private final int vertexCount;
    
    /** The texture applied to this model */
    private Texture texture;

    /**
     * Constructs a new model with no texture.
     *
     * @param vaoId The OpenGL VAO ID for this model
     * @param vertexCount The number of vertices in this model
     */
    public Model(int vaoId, int vertexCount) {
        this.vaoId = vaoId;
        this.vertexCount = vertexCount;
    }

    /**
     * Constructs a new model with a texture.
     *
     * @param vaoId The OpenGL VAO ID for this model
     * @param vertexCount The number of vertices in this model
     * @param texture The texture to apply to this model
     */
    public Model(int vaoId, int vertexCount, Texture texture) {
        this.vaoId = vaoId;
        this.vertexCount = vertexCount;
        this.texture = texture;
    }

    /**
     * Constructs a new model by copying another model's geometry and applying a new texture.
     *
     * @param model The model to copy geometry from
     * @param texture The texture to apply to the new model
     */
    public Model(Model model, Texture texture) {
        this.vaoId = model.vaoId;
        this.vertexCount = model.vertexCount;
        this.texture = texture;
    }

    /**
     * Gets the OpenGL VAO ID for this model.
     *
     * @return The VAO ID
     */
    public int getVaoId() {
        return vaoId;
    }

    /**
     * Gets the number of vertices in this model.
     *
     * @return The vertex count
     */
    public int getVertexCount() {
        return vertexCount;
    }

    /**
     * Gets the texture applied to this model.
     *
     * @return The texture, or null if no texture is applied
     */
    public Texture getTexture() {
        return texture;
    }

    /**
     * Sets the texture for this model.
     *
     * @param texture The texture to apply
     */
    public void setTexture(Texture texture) {
        this.texture = texture;
    }
    
    /**
     * @deprecated Use {@link #getVaoId()} instead
     */
    @Deprecated
    public int getId() {
        return getVaoId();
    }
}
