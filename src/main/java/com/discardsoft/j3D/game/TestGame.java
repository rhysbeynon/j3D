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
