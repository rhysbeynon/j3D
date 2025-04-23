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
 * @version 0.1
 */
public final class LoadModel {
    
    /** Shared instance of the object loader */
    private static final ObjectLoader loader = new ObjectLoader();
    
    /** Path to model resources */
    private static final String MODELS_PATH = "src/main/resources/models/";
    
    /** Path to texture resources */
    private static final String TEXTURES_PATH = "src/main/resources/textures/";
    
    /** Path to engine primitive models */
    private static final String ENGINE_MODELS_PATH = MODELS_PATH + "engine/";
    
    /** Path to the error model used when loading fails */
    private static final String ERROR_MODEL_PATH = ENGINE_MODELS_PATH + "errmodel.obj";

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
            model.setTexture(new Texture(loader.loadTexture(texturePath)));
            return model;
        } catch (Exception e) {
            System.err.println("Failed to load model: " + name + ". " + e.getMessage());
            return getErrorModel();
        }
    }

    /**
     * Loads a cube primitive model.
     *
     * @return The cube model, or an error model if loading fails
     */
    public static Model cube() {
        try {
            return loader.importOBJ(ENGINE_MODELS_PATH + "P_Cube.obj");
        } catch (Exception e) {
            System.err.println("Failed to load cube model: " + e.getMessage());
            return getErrorModel();
        }
    }

    /**
     * Loads a small sphere primitive model.
     *
     * @return The small sphere model, or an error model if loading fails
     */
    public static Model sphereSmall() {
        try {
            return loader.importOBJ(ENGINE_MODELS_PATH + "P_Sphere_Small.obj");
        } catch (Exception e) {
            System.err.println("Failed to load small sphere model: " + e.getMessage());
            return getErrorModel();
        }
    }

    /**
     * Loads a medium sphere primitive model.
     *
     * @return The medium sphere model, or an error model if loading fails
     */
    public static Model sphereMedium() {
        try {
            return loader.importOBJ(ENGINE_MODELS_PATH + "P_Sphere_Medium.obj");
        } catch (Exception e) {
            System.err.println("Failed to load medium sphere model: " + e.getMessage());
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
            return loader.importOBJ(ERROR_MODEL_PATH);
        } catch (Exception e) {
            System.err.println("Critical error: Failed to load error model: " + e.getMessage());
            return null;
        }
    }
}
