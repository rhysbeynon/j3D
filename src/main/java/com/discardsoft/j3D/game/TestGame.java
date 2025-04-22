package com.discardsoft.j3D.game;

import com.discardsoft.j3D.Main;
import com.discardsoft.j3D.core.*;
import com.discardsoft.j3D.core.entity.Camera;
import com.discardsoft.j3D.core.entity.Entity;
import com.discardsoft.j3D.core.entity.Light;
import com.discardsoft.j3D.core.entity.Model;
import com.discardsoft.j3D.core.entity.Texture;

import com.discardsoft.j3D.core.utils.LoadModel;
import com.discardsoft.j3D.core.utils.Settings;
import org.joml.Vector3f;
import org.joml.Vector2f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

public class TestGame implements ILogic {
    private static final boolean dev = Settings.DEV;
    private boolean wireframe = false;

    private static final float CAMERA_MOVE_SPEED = Settings.CAMERA_MOVE_SPEED;

    private final RenderManager renderer;
    private final ObjectLoader loader;
    private final WindowManager window;

    private Entity entity;
    private Camera camera;
    private Light light;

    Vector3f cameraInc;

    public TestGame() {
        renderer = new RenderManager();
        window = Main.getWindow();
        loader = new ObjectLoader();
        camera = new Camera();
        cameraInc = new Vector3f(0.0f, 0.0f, 0.0f);
        light = new Light(new Vector3f(10.0f, 10.0f, 10.0f), new Vector3f(1.0f, 1.0f, 1.0f), new Vector3f(0.05f, 0.1f, 0.1f));
    }

    @Override
    public void init() throws Exception {
        renderer.init();

        //TODO change to model[] or model hashmap<>
        Model model = LoadModel.model("suzanne");
        //TODO change Entity class to take in and render
        entity = new Entity(model, new Vector3f(0.0f, 0.0f, -5.0f), new Vector3f(0.0f, 0.0f, 0.0f), new Vector3f(1.0f, 1.0f, 1.0f));
    }

    @Override
    public void input() {
        cameraInc.set(0.0f, 0.0f, 0.0f);

        if (dev) {
            if (window.isKeyPressedBuffered(GLFW.GLFW_KEY_APOSTROPHE)) {
                wireframe = !wireframe;
                GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, wireframe ? GL11.GL_LINE : GL11.GL_FILL);
                if (wireframe) {
                    GL11.glDisable(GL11.GL_CULL_FACE);
                } else {
                    GL11.glEnable(GL11.GL_CULL_FACE);
                }
            }
        }

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

        entity.incRot(0.0f, 0.5f, 0.0f);
    }

    @Override
    public void render() {
        if(window.isResize()) {
            GL11.glViewport(0, 0, window.getWidth(), window.getHeight());
            window.setResize(true);
        }

        window.setClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        renderer.render(entity, camera, light);
    }

    @Override
    public void cleanup() {
        renderer.cleanup();
        loader.cleanup();
    }
}
