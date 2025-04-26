package com.discardsoft.j3D.core.entity;

/**
 * Represents a 3D model with texture in the rendering system.
 * <p>
 * This class holds the OpenGL references for a vertex array object (VAO) and
 * a vertex count for rendering, along with an optional texture.
 * </p>
 *
 * @author DiscardSoft
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
     * Checks if this model has a texture that contains transparency.
     * <p>
     * Currently this is determined by looking at the texture name.
     * Textures containing "grass" or "T_" prefixes are considered transparent.
     * </p>
     *
     * @return True if the model's texture contains transparency
     */
    public boolean hasTransparentTexture() {
        // If there's no texture, there can't be transparency
        if (texture == null || texture.getName() == null) {
            return false;
        }
        
        // Simple check for common transparent texture patterns
        String name = texture.getName().toLowerCase();
        return name.contains("grass") || name.startsWith("t_");
    }
    
    /**
     * @deprecated Use {@link #getVaoId()} instead
     */
    @Deprecated
    public int getId() {
        return getVaoId();
    }
}
