package com.discardsoft.j3D.game;

import com.discardsoft.j3D.Main;
import com.discardsoft.j3D.core.*;
import com.discardsoft.j3D.core.entity.Camera;
import com.discardsoft.j3D.core.entity.Entity;
import com.discardsoft.j3D.core.entity.Model;
import com.discardsoft.j3D.core.entity.Texture;
import com.discardsoft.j3D.core.utils.Consts;

import org.joml.Vector3f;
import org.joml.Vector2f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

public class TestGame implements ILogic {

    private static final float CAMERA_MOVE_SPEED = Consts.CAMERA_MOVE_SPEED;

    private final RenderManager renderer;
    private final ObjectLoader loader;
    private final WindowManager window;

    private Entity entity;
    private Camera camera;

    Vector3f cameraInc;

    public TestGame() {
        renderer = new RenderManager();
        window = Main.getWindow();
        loader = new ObjectLoader();
        camera = new Camera();
        cameraInc = new Vector3f(0.0f, 0.0f, 0.0f);
    }

    @Override
    public void init() throws Exception {
        renderer.init();

        //DEBUG CUBE
        float[] vertices = new float[] {
                // Front face (z+)
                -0.5f, 0.5f, 0.5f,    // 0: top-left
                -0.5f, -0.5f, 0.5f,   // 1: bottom-left
                0.5f, -0.5f, 0.5f,    // 2: bottom-right
                0.5f, 0.5f, 0.5f,     // 3: top-right

                // Back face (z-)
                -0.5f, 0.5f, -0.5f,   // 4: top-left
                -0.5f, -0.5f, -0.5f,  // 5: bottom-left
                0.5f, -0.5f, -0.5f,   // 6: bottom-right
                0.5f, 0.5f, -0.5f,    // 7: top-right

                // Top face (y+)
                -0.5f, 0.5f, -0.5f,   // 8: back-left
                0.5f, 0.5f, -0.5f,    // 9: back-right
                -0.5f, 0.5f, 0.5f,    // 10: front-left
                0.5f, 0.5f, 0.5f,     // 11: front-right

                // Right face (x+)
                0.5f, 0.5f, 0.5f,     // 12: top-front
                0.5f, -0.5f, 0.5f,    // 13: bottom-front
                0.5f, 0.5f, -0.5f,    // 14: top-back
                0.5f, -0.5f, -0.5f,   // 15: bottom-back

                // Left face (x-)
                -0.5f, 0.5f, 0.5f,    // 16: top-front
                -0.5f, -0.5f, 0.5f,   // 17: bottom-front
                -0.5f, 0.5f, -0.5f,   // 18: top-back
                -0.5f, -0.5f, -0.5f,  // 19: bottom-back

                // Bottom face (y-)
                -0.5f, -0.5f, -0.5f,  // 20: back-left
                0.5f, -0.5f, -0.5f,   // 21: back-right
                -0.5f, -0.5f, 0.5f,   // 22: front-left
                0.5f, -0.5f, 0.5f     // 23: front-right
        };

        float[] textureCoords = new float[]{
                // Front Face (z+)
                0.0f, 0.0f, // top-left
                0.0f, 1.0f, // bottom-left
                1.0f, 1.0f, // bottom-right
                1.0f, 0.0f, // top-right

                // Back Face (z-)
                1.0f, 0.0f, // top-left
                1.0f, 1.0f, // bottom-left
                0.0f, 1.0f, // bottom-right
                0.0f, 0.0f, // top-right

                // Top Face (y+)
                0.0f, 0.0f, // back-left
                1.0f, 0.0f, // back-right
                0.0f, 1.0f, // front-left
                1.0f, 1.0f, // front-right

                // Right Face (x+)
                0.0f, 0.0f, // top-front
                0.0f, 1.0f, // bottom-front
                1.0f, 0.0f, // top-back
                1.0f, 1.0f, // bottom-back

                // Left Face (x-)
                1.0f, 0.0f, // top-front
                1.0f, 1.0f, // bottom-front
                0.0f, 0.0f, // top-back
                0.0f, 1.0f, // bottom-back

                // Bottom Face (y-)
                0.0f, 0.0f, // back-left
                1.0f, 0.0f, // back-right
                0.0f, 1.0f, // front-left
                1.0f, 1.0f  // front-right
        };

        int[] indices = new int[]{
                // Front face
                0, 1, 3, 3, 1, 2,
                // Back face
                4, 7, 5, 5, 7, 6,
                // Top face
                8, 9, 10, 10, 9, 11,
                // Right face
                12, 14, 13, 13, 14, 15,
                // Left face
                16, 18, 17, 17, 18, 19,
                // Bottom face
                20, 21, 22, 22, 21, 23
        };

        // Model model = loader.loadModel(vertices, textureCoords, indices);
        Model model = loader.importOBJ("src/main/resources/models/notarealmodel.obj");
        model.setTexture(new Texture(loader.loadTexture("src/main/resources/textures/notaeraltexture.png")));
        entity = new Entity(model, new Vector3f(0.0f, 0.0f, -5.0f), new Vector3f(0.0f, 0.0f, 0.0f), 1.0f);
    }

    @Override
    public void input() {
        cameraInc.set(0.0f, 0.0f, 0.0f);

        // WASD controls
        if (window.isKeyPressed(GLFW.GLFW_KEY_W)) {
            cameraInc.z = -1.0f;
        }
        if (window.isKeyPressed(GLFW.GLFW_KEY_S)) {
            cameraInc.z = 1.0f;
        }
        if (window.isKeyPressed(GLFW.GLFW_KEY_A)) {
            cameraInc.x = -1.0f;
        }
        if (window.isKeyPressed(GLFW.GLFW_KEY_D)) {
            cameraInc.x = 1.0f;
        }
        if (window.isKeyPressed(GLFW.GLFW_KEY_SPACE)) {
            cameraInc.y = 1.0f;
        }
        if (window.isKeyPressed(GLFW.GLFW_KEY_LEFT_CONTROL) || window.isKeyPressed(GLFW.GLFW_KEY_C)) {
            cameraInc.y = -1.0f;
        }

        // Process mouse movement for camera rotation
        Vector2f mouseDelta = window.processMouseMovement();
        camera.rotateCamera(mouseDelta.x, mouseDelta.y);
    }

    @Override
    public void update() {
        camera.movePosition(cameraInc.x * CAMERA_MOVE_SPEED, cameraInc.y * CAMERA_MOVE_SPEED, cameraInc.z * CAMERA_MOVE_SPEED);

        entity.incRot(0.0f, 0.95f, 0.0f);
    }

    @Override
    public void render() {
        if(window.isResize()) {
            GL11.glViewport(0, 0, window.getWidth(), window.getHeight());
            window.setResize(true);
        }

        window.setClearColor(0.1f, 0.1f, 0.1f, 1.0f);
        renderer.render(entity, camera);
    }

    @Override
    public void cleanup() {
        renderer.cleanup();
        loader.cleanup();
    }
}
