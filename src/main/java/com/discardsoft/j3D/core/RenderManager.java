// -|+++++++++++++++++++++++++++++++++++++++++++++++++++++++++|-
// -|              04/16/2025 | DiscardSoft                   |-
// -|        j3D is a lightweight custom-built engine         |-
// -|        Made with LWJGL, openGL, JOML, and other         |-
// -|        helpful libraries for use on DiscardSoft         |-
// -|              Render Manager class for j3D               |-
// -|    Comments are always written above relevant context.  |-
// -|   ++++++++++++++++++++++++++++++++++++++++++++++++++    |-
// -|               Version: 0.07a In Development             |-
// -|   *some comments may be written by AI for convenience   |-
// -|+++++++++++++++++++++++++++++++++++++++++++++++++++++++++|-

package com.discardsoft.j3D.core;

import com.discardsoft.j3D.Main;
import com.discardsoft.j3D.core.entity.Camera;
import com.discardsoft.j3D.core.entity.Entity;
import com.discardsoft.j3D.core.entity.Light;
import com.discardsoft.j3D.core.scene.BaseScene;
import com.discardsoft.j3D.core.utils.Transformation;
import com.discardsoft.j3D.core.utils.Utils;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Manages the rendering of 3D entities in the game world.
 * <p>
 * This class is responsible for initializing and managing shaders,
 * setting up render parameters, and rendering entities with appropriate
 * lighting and camera view transformations.
 * </p>
 * 
 * @author DiscardSoft
 * @version 0.1
 */
public class RenderManager {
    
    /** Reference to window manager for viewport and projection updates */
    private final WindowManager window;
    
    /** Shader program manager for rendering */
    private ShaderManager shader;

    /**
     * Constructs a new render manager instance.
     * <p>
     * Retrieves the window manager instance from the main class.
     * </p>
     */
    public RenderManager() {
        window = Main.getWindow();
    }

    /**
     * Initializes the rendering system.
     * <p>
     * Loads and compiles shader programs, creates uniform variables,
     * and sets up the rendering pipeline.
     * </p>
     *
     * @throws Exception If initialization fails
     */
    public void init() throws Exception {
        shader = new ShaderManager();
        shader.createVertexShader(Utils.loadResource("/shaders/vertex.glsl"));
        shader.createFragmentShader(Utils.loadResource("/shaders/fragment.glsl"));
        shader.link();
        
        // Create model transformation uniforms
        shader.createUniform("textureSampler");
        shader.createUniform("transformationMatrix");
        shader.createUniform("projectionMatrix");
        shader.createUniform("viewMatrix");

        // Create lighting uniforms
        shader.createUniform("lightPosition");
        shader.createUniform("lightColor");
        shader.createUniform("ambientLight");
        shader.createUniform("cameraPosition");
        
        // Add a new uniform for transparency flag
        shader.createUniform("useTransparency");
    }

    /**
     * Renders an entire scene with the specified camera.
     * <p>
     * Processes all entities in the scene with their lighting settings.
     * </p>
     *
     * @param scene The scene to render
     * @param camera The camera from which to view the scene
     */
    public void render(BaseScene scene, Camera camera) {
        clear();
        
        // Get entities and light from scene
        List<Entity> entities = scene.getEntities();
        Light light = scene.getLight();

        // Separate entities into opaque and transparent lists
        List<Entity> opaqueEntities = new ArrayList<>();
        List<Entity> transparentEntities = new ArrayList<>();
        
        for (Entity entity : entities) {
            if (entity.hasTransparentTexture()) {
                transparentEntities.add(entity);
            } else {
                opaqueEntities.add(entity);
            }
        }

        // Prepare shader program
        shader.bind();
        
        // Set view and projection matrices (shared by all entities)
        shader.setUniform("projectionMatrix", window.updateProjectionMatrix());
        shader.setUniform("viewMatrix", Transformation.getViewMatrix(camera));
        
        // Set lighting properties (shared by all entities)
        shader.setUniform("lightPosition", light.getPosition());
        shader.setUniform("lightColor", light.getColor());
        shader.setUniform("ambientLight", light.getAmbient());
        shader.setUniform("cameraPosition", camera.getPosition());
        
        // Set texture sampler uniform (always texture unit 0)
        shader.setUniform("textureSampler", 0);
        
        // First render all opaque objects with depth testing and writing enabled
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(true);
        GL11.glDisable(GL11.GL_BLEND);
        
        // Set transparency flag to false for opaque objects
        shader.setUniform("useTransparency", 0);
        
        // Render each opaque entity
        for (Entity entity : opaqueEntities) {
            renderEntity(entity);
        }
        
        // Then render transparent objects with blending enabled
        if (!transparentEntities.isEmpty()) {
            // Sort transparent entities by distance from camera (back to front)
            sortTransparentEntities(transparentEntities, camera.getPosition());
            
            // Enable alpha blending
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GL11.glDepthMask(false);  // Don't write to depth buffer for transparent objects
            
            // Set transparency flag to true for transparent objects
            shader.setUniform("useTransparency", 1);
            
            // Render each transparent entity
            for (Entity entity : transparentEntities) {
                renderEntity(entity);
            }
            
            // Restore default state
            GL11.glDepthMask(true);
            GL11.glDisable(GL11.GL_BLEND);
        }
        
        // Unbind shader after rendering all entities
        shader.unbind();
    }
    
    /**
     * Sorts transparent entities by distance from camera, farthest to nearest.
     * This ensures proper alpha blending by rendering distant objects first.
     *
     * @param transparentEntities List of entities with transparency
     * @param cameraPosition Camera position to calculate distance from
     */
    private void sortTransparentEntities(List<Entity> transparentEntities, Vector3f cameraPosition) {
        transparentEntities.sort(new Comparator<Entity>() {
            @Override
            public int compare(Entity e1, Entity e2) {
                float distance1 = calculateDistanceSquared(e1.getPosition(), cameraPosition);
                float distance2 = calculateDistanceSquared(e2.getPosition(), cameraPosition);
                // Sort in descending order (furthest first)
                return Float.compare(distance2, distance1);
            }
        });
    }
    
    /**
     * Calculates squared distance between two points.
     * Using squared distance avoids unnecessary square root operations.
     *
     * @param pos1 First position
     * @param pos2 Second position
     * @return Square of the Euclidean distance between positions
     */
    private float calculateDistanceSquared(Vector3f pos1, Vector3f pos2) {
        float dx = pos1.x - pos2.x;
        float dy = pos1.y - pos2.y;
        float dz = pos1.z - pos2.z;
        return dx * dx + dy * dy + dz * dz;
    }

    /**
     * Renders a single entity with the currently bound shader.
     * <p>
     * Sets entity-specific uniforms, binds model data, and issues draw calls.
     * </p>
     *
     * @param entity The entity to render
     */
    private void renderEntity(Entity entity) {
        // Set entity-specific transformation matrix
        shader.setUniform("transformationMatrix", Transformation.createTransformationMatrix(entity));

        // Bind vertex array and enable attribute arrays
        GL30.glBindVertexArray(entity.getModel().getVaoId());
        GL20.glEnableVertexAttribArray(0); // Position
        GL20.glEnableVertexAttribArray(1); // Texture coordinates
        GL20.glEnableVertexAttribArray(2); // Normals
        
        // Bind texture
        GL15.glActiveTexture(GL15.GL_TEXTURE0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, entity.getModel().getTexture().getTextureId());
        
        // Set texture filtering to nearest (pixelated look) for all textures
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
        
        // Draw the entity
        GL11.glDrawElements(GL11.GL_TRIANGLES, entity.getModel().getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
        
        // Cleanup - disable attribute arrays and unbind vertex array
        GL20.glDisableVertexAttribArray(0);
        GL20.glDisableVertexAttribArray(1);
        GL20.glDisableVertexAttribArray(2);
        GL30.glBindVertexArray(0);
    }

    /**
     * Renders a single entity with the specified camera and light.
     * <p>
     * This method is maintained for backward compatibility.
     * New code should prefer using the scene-based rendering approach.
     * </p>
     *
     * @param entity The entity to render
     * @param camera The camera from which to view the entity
     * @param light The light source for illuminating the entity
     * @deprecated Use {@link #render(BaseScene, Camera)} instead
     */
    @Deprecated
    public void render(Entity entity, Camera camera, Light light) {
        clear();
        shader.bind();

        // Set transformation matrices
        shader.setUniform("textureSampler", 0);
        shader.setUniform("transformationMatrix", Transformation.createTransformationMatrix(entity));
        shader.setUniform("projectionMatrix", window.updateProjectionMatrix());
        shader.setUniform("viewMatrix", Transformation.getViewMatrix(camera));

        // Set lighting properties
        shader.setUniform("lightPosition", light.getPosition());
        shader.setUniform("lightColor", light.getColor());
        shader.setUniform("ambientLight", light.getAmbient());
        shader.setUniform("cameraPosition", camera.getPosition());
        
        // Set transparency flag based on entity
        shader.setUniform("useTransparency", entity.hasTransparentTexture() ? 1 : 0);

        // Enable alpha blending if needed
        if (entity.hasTransparentTexture()) {
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GL11.glDepthMask(false);
        }

        // Render the entity
        renderEntity(entity);
        
        // Restore default state
        if (entity.hasTransparentTexture()) {
            GL11.glDepthMask(true);
            GL11.glDisable(GL11.GL_BLEND);
        }
        
        shader.unbind();
    }

    /**
     * Clears the color and depth buffers in preparation for rendering.
     */
    public void clear() {
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
    }

    /**
     * Releases resources used by the render manager.
     * <p>
     * Calls cleanup on the shader manager to delete shader programs.
     * </p>
     */
    public void cleanup() {
        shader.cleanup();
    }
}
