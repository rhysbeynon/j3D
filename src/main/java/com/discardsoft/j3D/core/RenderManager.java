// -|+++++++++++++++++++++++++++++++++++++++++++++++++++++++++|-
// -|              04/16/2025 | DISCVRD Software              |-
// -|        j3D is a lightweight custom-built engine         |-
// -|        Made with LWJGL, openGL, JOML, and other         |-
// -|        helpful libraries for use on DiscardSoft         |-
// -|              Render Manager class for j3D               |-
// -|    Comments are always written above relevant context.  |-
// -|   ++++++++++++++++++++++++++++++++++++++++++++++++++    |-
// -|               Version: 0.1 In Development               |-
// -|   *some comments may be written by AI for convenience   |-
// -|+++++++++++++++++++++++++++++++++++++++++++++++++++++++++|-

package com.discardsoft.j3D.core;

import com.discardsoft.j3D.Main;
import com.discardsoft.j3D.core.entity.Camera;
import com.discardsoft.j3D.core.entity.Entity;
import com.discardsoft.j3D.core.entity.Light;
import com.discardsoft.j3D.core.entity.Model;
import com.discardsoft.j3D.core.utils.Transformation;
import com.discardsoft.j3D.core.utils.Utils;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

public class RenderManager {
    private final WindowManager window;
    private ShaderManager shader;

    public RenderManager() {
        window = Main.getWindow();
    }

    public void init() throws Exception {
        shader = new ShaderManager();
        shader.createVertexShader(Utils.loadResource("/shaders/vertex.vs"));
        shader.createFragmentShader(Utils.loadResource("/shaders/fragment.fs"));
        shader.link();
        //model uniforms
        shader.createUniform("textureSampler");
        shader.createUniform("transformationMatrix");
        shader.createUniform("projectionMatrix");
        shader.createUniform("viewMatrix");

        //vertex shading uniforms (gouraud shading)
        shader.createUniform("lightPosition");
        shader.createUniform("lightColor");
        shader.createUniform("ambientLight");
        shader.createUniform("cameraPosition");
    }

    public void render(Entity entity, Camera camera, Light light) {
        clear();
        shader.bind();

        //set texture and transformation matrix uniforms
        shader.setUniform("textureSampler", 0);
        shader.setUniform("transformationMatrix", Transformation.createTransformationMatrix(entity));
        shader.setUniform("projectionMatrix", window.updateProjectionMatrix());
        shader.setUniform("viewMatrix", Transformation.getViewMatrix(camera));

        //set light uniforms
        shader.setUniform("lightPosition", light.getPosition()); //set light to camera position
        shader.setUniform("lightColor", light.getColor());
        shader.setUniform("ambientLight", new Vector3f(0.2f, 0.2f, 0.2f)); // Set ambient light to maximum for testing
        shader.setUniform("cameraPosition", camera.getPosition());

        GL30.glBindVertexArray(entity.getModel().getId());
        //use our previously set index value of 0 (arbitrary)
        GL20.glEnableVertexAttribArray(0);
        GL20.glEnableVertexAttribArray(1);
        GL20.glEnableVertexAttribArray(2); // Enable normals
        GL15.glActiveTexture(GL15.GL_TEXTURE0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, entity.getModel().getTexture().getId());
        //disables texture filtering
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
        GL11.glDrawElements(GL11.GL_TRIANGLES, entity.getModel().getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
        //unset all the values after using them
        GL20.glDisableVertexAttribArray(0);
        GL20.glDisableVertexAttribArray(1);
        GL20.glDisableVertexAttribArray(2); // Disable normals
        GL30.glBindVertexArray(0);
        shader.unbind();
    }

    public void clear() {
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
    }

    public void cleanup() {
        shader.cleanup();
    }
}
