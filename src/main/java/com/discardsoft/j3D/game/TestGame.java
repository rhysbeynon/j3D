package com.discardsoft.j3D.game;

import com.discardsoft.j3D.Main;
import com.discardsoft.j3D.core.*;
import com.discardsoft.j3D.core.entity.Entity;
import com.discardsoft.j3D.core.entity.Model;
import com.discardsoft.j3D.core.entity.Texture;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

public class TestGame implements ILogic {

    private int direction = 0;
    private float color = 0.0f;

    private final RenderManager renderer;
    private final ObjectLoader loader;
    private final WindowManager window;

    private Entity entity;

    public TestGame() {
        renderer = new RenderManager();
        window = Main.getWindow();
        loader = new ObjectLoader();
    }

    @Override
    public void init() throws Exception {
        renderer.init();

        //DEBUG RECTANGLE
        float[] vertices = {
                0.5f, -0.5f, 0f,
                0.5f,  0.5f, 0f,
                -0.5f,  0.5f, 0f,
                -0.5f, -0.5f, 0f,
        };

        int[] indices = {
                0, 1, 3,
                3, 1, 2
        };

        float[] textureCoords = {
                0.0f, 0.1f,
                1.1f, 1.0f
        };

        Model model = loader.loadModel(vertices, textureCoords, indices);
        model.setTexture(new Texture(loader.loadTexture("src/main/resources/textures/test.png")));
        entity = new Entity(model, new Vector3f(1.0f, 0.0f, 0.0f), new Vector3f(0.0f, 0.0f, 0.0f), 1.0f);
    }

    @Override
    public void input() {
        if(window.isKeyPressed(GLFW.GLFW_KEY_UP)){
            direction = 1;
        } else if(window.isKeyPressed(GLFW.GLFW_KEY_DOWN)){
            direction = -1;
        }
    }

    @Override
    public void update() {
        // Update color separately
        color += direction * 0.01f;
        color = Math.max(0.0f, Math.min(color, 1.0f));

        // Consistent leftward movement independent of color condition
        entity.getPos().x += 0.1f;
        //position wrapping for window border
        if(entity.getPos().x < -1.6f) {
            entity.getPos().x = 1.5f;
        } else if (entity.getPos().x > 1.6f) {
            entity.getPos().x = -1.5f;
        }
    }

    @Override
    public void render() {
        if(window.isResize()) {
            GL11.glViewport(0, 0, window.getWidth(), window.getHeight());
            window.setResize(true);
        }

        window.setClearColor(color, color, color, 1.0f);
        renderer.render(entity);
    }

    @Override
    public void cleanup() {
        renderer.cleanup();
        loader.cleanup();
    }
}
