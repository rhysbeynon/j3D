package com.discardsoft.j3D.core.utils;

import com.discardsoft.j3D.core.ObjectLoader;
import com.discardsoft.j3D.core.entity.Model;
import com.discardsoft.j3D.core.entity.Texture;

/**
 * Utility class for loading 3D models and their textures.
 * <p>
 * Provides methods to load models from OBJ files and associate them with textures.
 * Also includes convenience methods for loading standard primitive models.
 * </p>
 * 
 * @author DISCVRD Software
 * @version 0.2
 */
public final class LoadModel {
    
    /** Shared instance of the object loader */
    private static final ObjectLoader loader = new ObjectLoader();
    
    /** Path to model resources */
    private static final String MODELS_PATH = "src/main/resources/models/";
    
    /** Path to texture resources */
    private static final String TEXTURES_PATH = "src/main/resources/textures/";

    /**
     * Private constructor to prevent instantiation of utility class.
     */
    private LoadModel() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    /**
     * Loads a named model with its matching texture.
     * <p>
     * The method looks for an OBJ file and a matching PNG texture
     * with the same name in the resources directories.
     * </p>
     *
     * @param name The name of the model and texture (without extensions)
     * @return The loaded model with its texture, or an error model if loading fails
     */
    public static Model model(String name) {
        try {
            String modelPath = MODELS_PATH + name + ".obj";
            String texturePath = TEXTURES_PATH + name + ".png";
            
            Model model = loader.importOBJ(modelPath);
            int textureId = loader.loadTexture(texturePath);
            // Create texture with name so we can identify it for transparency
            Texture texture = new Texture(textureId, name);
            model.setTexture(texture);
            return model;
        } catch (Exception e) {
            System.err.println("Failed to load model: " + name + ". " + e.getMessage());
            return getErrorModel();
        }
    }
    
    /**
     * Loads a named model with its specified texture.
     * <p>
     * The method looks for an OBJ file and a PNG texture
     * with respective names in the resources directories.
     * </p>
     *
     * @param name The name of the model and texture (without extensions)
     * @param texture The name of the texture (without extension)
     * @return The loaded model with its texture, or an error model if loading fails
     */
    public static Model model(String name, String texture) {
        try {
            String modelPath = MODELS_PATH + name + ".obj";
            String texturePath = TEXTURES_PATH + texture + ".png";
            
            Model model = loader.importOBJ(modelPath);
            int textureId = loader.loadTexture(texturePath);
            // Create texture with name so we can identify it for transparency
            Texture textureObj = new Texture(textureId, texture);
            model.setTexture(textureObj);
            return model;
        } catch (Exception e) {
            System.err.println("Failed to load model: " + name + ". " + e.getMessage());
            return getErrorModel();
        }
    }

    /**
     * Gets the error model used when model loading fails.
     *
     * @return The error model, or null if even the error model cannot be loaded
     */
    public static Model getErrorModel() {
        try {
            Model model = loader.importOBJ(MODELS_PATH + "errmodels.obj");
            int textureId = loader.loadTexture(TEXTURES_PATH + "errtex.png");
            model.setTexture(new Texture(textureId, "errtex"));
            return model;
        } catch (Exception e) {
            System.err.println("Critical error: Failed to load error model: " + e.getMessage());
            return null;
        }
    }
}
