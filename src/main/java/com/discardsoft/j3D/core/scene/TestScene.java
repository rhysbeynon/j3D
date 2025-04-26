package com.discardsoft.j3D.core.scene;

import com.discardsoft.j3D.core.ObjectLoader;
import com.discardsoft.j3D.core.entity.Entity;
import com.discardsoft.j3D.core.entity.Model;
import com.discardsoft.j3D.core.entity.terrain.Terrain;
import com.discardsoft.j3D.core.entity.terrain.TerrainEntity;
import com.discardsoft.j3D.core.utils.LoadModel;

import java.util.Random;

import org.joml.Vector3f;

/**
 * Test implementation of a scene with sample entities.
 * <p>
 * This scene demonstrates how to create and organize entities
 * using the scene system, including model loading and lighting.
 * </p>
 *
 * @author DiscardSoft
 * @version 0.1
 */
public class TestScene extends BaseScene {

    // Reference to track the Suzanne model for animation
    private Entity suzanneEntity;
    
    // Reference to the terrain entity
    private TerrainEntity terrainEntity;
    
    // Dimensions for the terrain
    private static final float TERRAIN_SIZE = 512.0f;
    private static final int TERRAIN_GRID_COUNT = 64; // 512/8 = 64 cells to get 8 unit grid cells
    private static final float TERRAIN_HEIGHT = 0.0f; // Y-level 0
    private static final float TEXTURE_REPEAT = 64.0f; // Repeat texture 64 times across terrain

    /**
     * Constructs a new test scene.
     */
    public TestScene() {
        super(); // Use default light from BaseScene
    }
    
    @Override
    public void initialize() {
        try {
            // Create terrain first (so it's drawn first)
            createTerrain("ground1");
            
            // Add some additional models to the scene to show scale
            addSceneModels();
            
            // Set custom light position - raised higher for better terrain lighting
            getLight().setPosition(30.0f, 50.0f, 30.0f);
            getLight().setAmbient(0.3f, 0.3f, 0.3f); // Increase ambient light for terrain
            
        } catch (Exception e) {
            System.err.println("Error initializing test scene: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Creates and adds a terrain to the scene.
     */
    private void createTerrain(String texture) throws Exception {
        // Get the object loader
        ObjectLoader loader = new ObjectLoader();
        
        // Position the terrain so its center is at the origin
        float halfSize = TERRAIN_SIZE / 2.0f;
        Vector3f terrainPosition = new Vector3f(-halfSize, TERRAIN_HEIGHT, -halfSize);
        
        // Create the terrain with defaulttex texture
        Terrain terrain = new Terrain(
            TERRAIN_SIZE, 
            TERRAIN_GRID_COUNT, 
            TERRAIN_HEIGHT,
            terrainPosition,
            loader,
            "src/main/resources/textures/" + texture + ".png",
            TEXTURE_REPEAT
        );
        
        // Create an entity for the terrain and add it to the scene
        terrainEntity = new TerrainEntity(terrain);
        addEntity(terrainEntity);
    }
    
    /**
     * Adds various models to the scene to demonstrate scale and terrain usage.
     */
    private void addSceneModels() {
        // Add the classic Suzanne monkey head model
        Model suzanneModel = LoadModel.model("suzanne");
        suzanneEntity = new Entity(
            suzanneModel,
            new Vector3f(0.0f, 1.0f, -5.0f), // Raised above terrain
            new Vector3f(0.0f, 0.0f, 0.0f),
            new Vector3f(1.0f, 1.0f, 1.0f)
        );
        addEntity(suzanneEntity);
        
        // Create a 100 x 100 grid of grass sprites at y=1.2
        Model grassModel = LoadModel.model("grass2D", "T_shrub2");
        Random random = new Random();
        float spacing = 1.0f; // Controls how closely packed the grass is
        float startX = -40.0f; // Starting X position (centered)
        float startZ = -40.0f; // Starting Z position (centered)
        
        for (int z = 0; z < 50; z++) {
            for (int x = 0; x < 50; x++) {
                Entity grassEntity = new Entity(
                    grassModel,
                    new Vector3f(startX + x * spacing * random.nextFloat(1.2f), 0.2f, startZ + z * spacing * random.nextFloat(1.2f)),
                    new Vector3f(0.0f, 0.0f, 0.0f),
                    new Vector3f(0.8f, 0.8f, 0.8f)
                );
                grassEntity.setBillboardY(true);
                grassEntity.setHasTransparentTexture(true);
                addEntity(grassEntity);
            }
        }
    }
    
    @Override
    public void update(float deltaTime) {
        // Only rotate the Suzanne model
        if (suzanneEntity != null) {
            // Get current rotation
            Vector3f rotation = suzanneEntity.getRotation();
            
            // Update Y rotation (spin around vertical axis)
            rotation.y += 45.0f * deltaTime; // 45 degrees per second
            
            // Optional: add some gentle wobble on X and Z for more interesting motion
            rotation.x = 15.0f * (float)Math.sin(rotation.y * 0.05f);
            
            // Apply the updated rotation
            suzanneEntity.setRotation(rotation);
        }
    }
}
