package com.discardsoft.j3D.core.level;

import com.discardsoft.j3D.core.ObjectLoader;

/**
 * Utility class to test level file loading and saving functionality.
 * <p>
 * This class provides methods to test the LevelLoader with the test_level.j3dl file
 * and validate that the level file system is working correctly.
 * </p>
 * 
 * @author DiscardSoft
 * @version 0.1
 */
public class LevelTestUtility {
    
    /**
     * Tests loading the test_level.j3dl file and prints information about it.
     */
    public static void testLoadLevel() {
        try {
            System.out.println("=== Level File System Test ===");
            
            // Create a LevelLoader
            ObjectLoader objectLoader = new ObjectLoader();
            LevelLoader loader = new LevelLoader(objectLoader);
            
            // Try to load the test level
            String levelPath = "/levels/test_level.j3dl";
            System.out.println("Loading level from: " + levelPath);
            
            LevelLoader.LoadedLevel level = loader.loadLevel(levelPath);
            
            // Print level information
            if (level.metadata != null) {
                System.out.println("\nLevel Metadata:");
                System.out.println("  Name: " + level.metadata.name);
                System.out.println("  Description: " + level.metadata.description);
                System.out.println("  Version: " + level.metadata.version);
                System.out.println("  Author: " + level.metadata.author);
                System.out.println("  Created: " + level.metadata.created);
                System.out.println("  Modified: " + level.metadata.modified);
            }
            
            // Print lighting information
            if (level.scene != null && level.scene.getLight() != null) {
                System.out.println("\nLighting:");
                System.out.println("  Position: " + level.scene.getLight().getPosition());
                System.out.println("  Color: " + level.scene.getLight().getColor());
                System.out.println("  Ambient: " + level.scene.getLight().getAmbient());
            }
            
            // Print terrain information
            if (level.terrain != null) {
                System.out.println("\nTerrain:");
                System.out.println("  Size: " + level.terrain.getTerrain().getSize());
                System.out.println("  Grid Count: " + level.terrain.getTerrain().getGridCount());
                System.out.println("  Height: " + level.terrain.getTerrain().getHeight());
                System.out.println("  Position: " + level.terrain.getTerrain().getPosition());
                System.out.println("  Texture Repeat: " + level.terrain.getTerrain().getTextureRepeatCount());
            }
            
            // Print entity information
            System.out.println("\nEntities (" + level.entities.size() + " total):");
            for (LevelLoader.LoadedEntity entity : level.entities) {
                System.out.println("  Entity: " + entity.entity.getClass().getSimpleName());
                System.out.println("    Position: " + entity.entity.getPosition());
                System.out.println("    Rotation: " + entity.entity.getRotation());
                System.out.println("    Scale: " + entity.entity.getScale());
            }
            
            // Print spawn points
            System.out.println("\nSpawn Points (" + level.spawns.size() + " total):");
            for (LevelLoader.SpawnPoint spawn : level.spawns) {
                System.out.println("  " + spawn.name + " (ID: " + spawn.id + ")");
                System.out.println("    Position: " + spawn.position);
                System.out.println("    Rotation: " + spawn.rotation);
            }
            
            System.out.println("\n=== Level loading test completed successfully! ===");
            
        } catch (Exception e) {
            System.err.println("Error testing level loading: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Tests saving a level to a new file to verify the save functionality.
     */
    public static void testSaveLevel() {
        try {
            System.out.println("\n=== Level Save Test ===");
            
            // Create a LevelLoader
            ObjectLoader objectLoader = new ObjectLoader();
            LevelLoader loader = new LevelLoader(objectLoader);
            
            // Load the test level first
            String inputPath = "/levels/test_level.j3dl";
            LevelLoader.LoadedLevel level = loader.loadLevel(inputPath);
            
            // Save it to a new file (in a writable location)
            String outputPath = "test_level_copy.j3dl";
            System.out.println("Saving level to: " + outputPath);
            
            loader.saveLevel(level, outputPath);
            
            System.out.println("Level saved successfully!");
            
            // Try to load the saved file to verify it works
            System.out.println("Loading saved file to verify...");
            // Note: For testing saved files, we need to use filesystem loading since 
            // we can't save to classpath resources. In a real implementation, 
            // we would have separate methods for resource loading vs filesystem loading.
            System.out.println("(Skipping reload test - saved file is on filesystem, not classpath)");
            
            System.out.println("=== Level save test completed successfully! ===");
            
        } catch (Exception e) {
            System.err.println("Error testing level saving: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Main method to run all tests.
     */
    public static void main(String[] args) {
        testLoadLevel();
        testSaveLevel();
    }
}
