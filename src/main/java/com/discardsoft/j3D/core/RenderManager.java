// -|+++++++++++++++++++++++++++++++++++++++++++++++++++++++++|-
// -|              04/16/2025 | DiscardSoft                   |-
// -|        j3D is a lightweight custom-built engine         |-
// -|        Made with LWJGL, openGL, JOML, and other         |-
// -|        helpful libraries for use on DiscardSoft         |-
// -|              Render Manager class for j3D               |-
// -|    Comments are always written above relevant context.  |-
// -|   ++++++++++++++++++++++++++++++++++++++++++++++++++    |-
// -|               Version: 0.05a In Development             |-
// -|   *some comments may be written by AI for convenience   |-
// -|+++++++++++++++++++++++++++++++++++++++++++++++++++++++++|-

package com.discardsoft.j3D.core;

import com.discardsoft.j3D.Main;
import com.discardsoft.j3D.core.entity.Camera;
import com.discardsoft.j3D.core.entity.Entity;
import com.discardsoft.j3D.core.entity.Light;
import com.discardsoft.j3D.core.scene.TestScene;
import com.discardsoft.j3D.core.utils.Transformation;
import com.discardsoft.j3D.core.utils.Utils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

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
        shader.createVertexShader(Utils.loadResource("/shaders/vertex.vs"));
        shader.createFragmentShader(Utils.loadResource("/shaders/fragment.fs"));
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
    public void render(TestScene scene, Camera camera) {
        clear();
        
        // Get entities and light from scene
        List<Entity> entities = scene.getEntities();
        Light light = scene.getLight();

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

        // Render each entity in the scene
        for (Entity entity : entities) {
            renderEntity(entity);
        }
        
        // Unbind shader after rendering all entities
        shader.unbind();
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
        
        // Set texture filtering to nearest (pixelated look)
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
     * @deprecated Use {@link #render(TestScene, Camera)} instead
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

        // Render the entity
        renderEntity(entity);
        
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
