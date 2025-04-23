package com.discardsoft.j3D.core.entity.terrain;

import com.discardsoft.j3D.core.ObjectLoader;
import com.discardsoft.j3D.core.entity.Model;
import com.discardsoft.j3D.core.entity.Texture;
import org.joml.Vector3f;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a flat terrain patch in the 3D world.
 * <p>
 * This class generates a flat terrain mesh with a grid pattern
 * and texture for use as ground in a scene.
 * </p>
 *
 * @author DiscardSoft
 * @version 0.1
 */
public class Terrain {
    
    /** The size of the terrain in world units (x and z dimensions) */
    private final float size;
    
    /** The number of grid cells along each axis */
    private final int gridCount;
    
    /** The size of each grid cell */
    private final float gridSize;
    
    /** The y-position of the terrain (height) */
    private final float height;
    
    /** The model containing the terrain mesh */
    private Model model;
    
    /** The position of the terrain in world space */
    private final Vector3f position;
    
    /** Number of times to repeat the texture across terrain */
    private final float textureRepeatCount;

    /**
     * Creates a new terrain with specified dimensions.
     *
     * @param size The total size of the terrain in world units
     * @param gridCount The number of grid cells along each axis
     * @param height The y-position of the terrain
     * @param position The position of the terrain in world space
     * @param loader The object loader to use for mesh generation
     * @param texturePath The path to the texture to apply to the terrain
     * @param textureRepeatCount Number of times to repeat the texture across terrain
     */
    public Terrain(float size, int gridCount, float height, Vector3f position, 
                  ObjectLoader loader, String texturePath, float textureRepeatCount) throws Exception {
        this.size = size;
        this.gridCount = gridCount;
        this.gridSize = size / gridCount;
        this.height = height;
        this.position = position;
        this.textureRepeatCount = textureRepeatCount;
        
        // Generate the terrain mesh and apply texture
        generateTerrain(loader, texturePath);
    }
    
    /**
     * Creates a new terrain with specified dimensions and default texture tiling.
     *
     * @param size The total size of the terrain in world units
     * @param gridCount The number of grid cells along each axis
     * @param height The y-position of the terrain
     * @param position The position of the terrain in world space
     * @param loader The object loader to use for mesh generation
     * @param texturePath The path to the texture to apply to the terrain
     */
    public Terrain(float size, int gridCount, float height, Vector3f position, 
                  ObjectLoader loader, String texturePath) throws Exception {
        this(size, gridCount, height, position, loader, texturePath, size / 8.0f);
    }
    
    /**
     * Gets the model representation of the terrain.
     *
     * @return The terrain model
     */
    public Model getModel() {
        return model;
    }
    
    /**
     * Gets the position of the terrain.
     *
     * @return The position in world space
     */
    public Vector3f getPosition() {
        return position;
    }
    
    /**
     * Generates the terrain mesh data and creates a model.
     *
     * @param loader The object loader to use
     * @param texturePath The path to the texture to apply
     */
    private void generateTerrain(ObjectLoader loader, String texturePath) throws Exception {
        // Calculate total vertices and indices
        int vertexCount = (gridCount + 1) * (gridCount + 1);
        int indexCount = 6 * gridCount * gridCount; // 2 triangles per grid cell, 3 indices per triangle
        
        float[] vertices = new float[vertexCount * 3];
        float[] normals = new float[vertexCount * 3];
        float[] textureCoords = new float[vertexCount * 2];
        int[] indices = new int[indexCount];
        
        // Generate vertices
        int vertexIndex = 0;
        for (int z = 0; z <= gridCount; z++) {
            for (int x = 0; x <= gridCount; x++) {
                // Calculate position - spread vertices evenly across terrain size
                float xPos = ((float)x / gridCount) * size;
                float zPos = ((float)z / gridCount) * size;
                
                // Position (3 floats per vertex)
                vertices[vertexIndex * 3] = xPos;
                vertices[vertexIndex * 3 + 1] = height; // Flat terrain at specified height
                vertices[vertexIndex * 3 + 2] = zPos;
                
                // Normal (3 floats per vertex) - all point up for flat terrain
                normals[vertexIndex * 3] = 0;
                normals[vertexIndex * 3 + 1] = 1;
                normals[vertexIndex * 3 + 2] = 0;
                
                // Texture coordinates (2 floats per vertex)
                // Scale texture coordinates to create tiling effect
                textureCoords[vertexIndex * 2] = ((float)x / gridCount) * textureRepeatCount;
                textureCoords[vertexIndex * 2 + 1] = ((float)z / gridCount) * textureRepeatCount;
                
                vertexIndex++;
            }
        }
        
        // Generate indices for triangles
        int indexIndex = 0;
        for (int z = 0; z < gridCount; z++) {
            for (int x = 0; x < gridCount; x++) {
                // Calculate the indices of the four corners of the current grid cell
                int topLeft = z * (gridCount + 1) + x;
                int topRight = topLeft + 1;
                int bottomLeft = (z + 1) * (gridCount + 1) + x;
                int bottomRight = bottomLeft + 1;
                
                // First triangle (top-left, bottom-left, bottom-right)
                indices[indexIndex++] = topLeft;
                indices[indexIndex++] = bottomLeft;
                indices[indexIndex++] = bottomRight;
                
                // Second triangle (top-left, bottom-right, top-right)
                indices[indexIndex++] = topLeft;
                indices[indexIndex++] = bottomRight;
                indices[indexIndex++] = topRight;
            }
        }
        
        // Create the model
        model = loader.loadModel(vertices, textureCoords, normals, indices);
        
        // Load and apply texture
        Texture texture = new Texture(loader.loadTexture(texturePath));
        model.setTexture(texture);
    }
}