package com.discardsoft.j3D.core.scene;

import com.discardsoft.j3D.core.entity.Entity;
import com.discardsoft.j3D.core.entity.Model;
import com.discardsoft.j3D.core.utils.LoadModel;
import org.joml.Vector3f;

/**
 * A test scene implementation for the j3D engine.
 * <p>
 * This class extends BaseScene and demonstrates how to create a custom scene
 * by overriding initialization and update methods.
 * </p>
 *
 * @author DISCVRD Software
 * @version 0.1
 */
public class TestScene extends BaseScene {

    // Reference to track the Suzanne model for animation
    private Entity suzanneEntity;

    /**
     * Constructs a new test scene.
     */
    public TestScene() {
        super(); // Use default light from BaseScene
    }
    
    @Override
    public void initialize() {
        // Add the classic Suzanne monkey head model
        Model suzanneModel = LoadModel.model("suzanne");
        suzanneEntity = new Entity(
            suzanneModel,
            new Vector3f(0.0f, 0.0f, -5.0f),
            new Vector3f(0.0f, 0.0f, 0.0f),
            new Vector3f(1.0f, 1.0f, 1.0f)
        );
        addEntity(suzanneEntity);
        
        // Set custom light position
        getLight().setPosition(10.0f, 10.0f, 5.0f);
        
        // Optional: Add more entities
        // Model cubeModel = LoadModel.model("P_Cube");
        // Entity cube = new Entity(
        //     cubeModel,
        //     new Vector3f(3.0f, 0.0f, -5.0f),
        //     new Vector3f(0.0f, 0.0f, 0.0f),
        //     new Vector3f(0.5f, 0.5f, 0.5f)
        // );
        // addEntity(cube);
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
