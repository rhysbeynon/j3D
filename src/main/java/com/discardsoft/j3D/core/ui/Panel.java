package com.discardsoft.j3D.core.ui;

import com.discardsoft.j3D.core.ObjectLoader;
import com.discardsoft.j3D.core.entity.Model;
import com.discardsoft.j3D.core.entity.Texture;
import com.discardsoft.j3D.core.utils.LoadModel;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

/**
 * A UI Panel element that displays a texture.
 * <p>
 * Panels are rectangular UI elements that can be used as backgrounds,
 * buttons, or other visual elements in the UI.
 * </p>
 *
 * @author DiscardSoft
 * @version 0.1
 */
public class Panel extends UIElement {
    
    /** Object loader for loading textures */
    private static final ObjectLoader loader = new ObjectLoader();
    
    /**
     * Creates a new panel with the specified texture.
     * 
     * @param texture The texture to use for this panel
     * @param zIndex The z-index for rendering order
     */
    public Panel(Texture texture, int zIndex) {
        super(texture, zIndex);
    }
    
    /**
     * Creates a new panel with the specified texture path.
     * 
     * @param texturePath The path to the texture resource
     * @param zIndex The z-index for rendering order
     */
    public Panel(String texturePath, int zIndex) {
        // Call the default constructor with a temporary texture
        super(new Texture(0), zIndex);
        
        // Then load texture using the ObjectLoader and replace it
        try {
            int textureId = loader.loadTexture(texturePath);
            String textureName = texturePath.substring(texturePath.lastIndexOf('/') + 1);
            this.texture = new Texture(textureId, textureName);
            this.transparent = texture.hasTransparency();
            
            // Recalculate size based on the new texture
            calculateSizeFromTexture();
        } catch (Exception e) {
            System.err.println("Failed to load texture for UI Panel: " + texturePath);
            // Fall back to error texture
            try {
                int textureId = loader.loadTexture("src/main/resources/textures/errtex.png");
                this.texture = new Texture(textureId, "errtex");
                this.transparent = texture.hasTransparency();
                
                // Recalculate size based on the error texture
                calculateSizeFromTexture();
            } catch (Exception ex) {
                System.err.println("Critical error: Failed to load error texture");
            }
        }
    }
    
    @Override
    protected void createModel() {
        // Create a simple quad mesh centered at origin
        float[] positions = new float[] {
            // Left-bottom
            -0.5f, -0.5f, 0,
            // Right-bottom
            0.5f, -0.5f, 0,
            // Right-top
            0.5f, 0.5f, 0,
            // Left-top
            -0.5f, 0.5f, 0
        };
        
        float[] textCoords = new float[] {
            0, 1,  // Left-bottom
            1, 1,  // Right-bottom
            1, 0,  // Right-top
            0, 0   // Left-top
        };
        
        int[] indices = new int[] {
            0, 1, 2,  // First triangle
            2, 3, 0   // Second triangle
        };
        
        // Create the model using the game's loading utility
        model = LoadModel.loadQuadModel(positions, textCoords, indices);
        
        // Set the texture for the model
        model.setTexture(texture);
    }
    
    @Override
    public void render() {
        // The actual rendering is handled by UIManager
        // This is just for rendering children
        for (UIElement child : children) {
            if (child.isVisible()) {
                child.render();
            }
        }
    }
    
    /**
     * Renders the panel model with the bound shader.
     * This method is called by the UIManager.
     */
    public void renderModel() {
        // Bind vertex array and enable attribute arrays
        GL30.glBindVertexArray(model.getVaoId());
        GL20.glEnableVertexAttribArray(0);
        GL20.glEnableVertexAttribArray(1);
        
        // Bind texture
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, model.getTexture().getId());
        
        // Set texture filtering to nearest (pixelated look) for UI textures
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
        
        // Draw the mesh
        GL11.glDrawElements(GL11.GL_TRIANGLES, model.getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
        
        // Cleanup - disable attribute arrays and unbind vertex array
        GL20.glDisableVertexAttribArray(0);
        GL20.glDisableVertexAttribArray(1);
        GL30.glBindVertexArray(0);
    }
}