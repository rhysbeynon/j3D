package com.discardsoft.j3D.game;

import com.discardsoft.j3D.Main;
import com.discardsoft.j3D.core.*;
import com.discardsoft.j3D.core.entity.Camera;
import com.discardsoft.j3D.core.entity.Entity;
import com.discardsoft.j3D.core.entity.Model;
import com.discardsoft.j3D.core.entity.Texture;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

public class TestGame implements ILogic {

    private static final float CAMERA_MOVE_SPEED = 0.05f;
    private static final float CAMERA_ROTATION_SPEED = 0.5f;
    private static final float MOUSE_SENSITIVITY = 0.1f;

    private final RenderManager renderer;
    private final ObjectLoader loader;
    private final WindowManager window;

    private Entity entity;
    private Camera camera;

    Vector3f cameraInc;
    Vector3f cameraRotation;
    private boolean mouseControlEnabled = false;
    private double lastMouseX = 0;
    private double lastMouseY = 0;

    public TestGame() {
        renderer = new RenderManager();
        window = Main.getWindow();
        loader = new ObjectLoader();
        camera = new Camera();
        cameraInc = new Vector3f(0.0f, 0.0f, 0.0f);
        cameraRotation = new Vector3f(0.0f, 0.0f, 0.0f);
    }

    @Override
    public void init() throws Exception {
        renderer.init();

        // LOW-POLY SPHERE
        int latitudeBands = 8;  // Number of horizontal divisions
        int longitudeBands = 8; // Number of vertical divisions
        float radius = 0.5f;    // Radius of the sphere
        
        // Calculate the number of vertices and indices
        int numVertices = (latitudeBands + 1) * (longitudeBands + 1);
        int numIndices = latitudeBands * longitudeBands * 6;
        
        float[] vertices = new float[numVertices * 3];
        float[] textureCoords = new float[numVertices * 2];
        int[] indices = new int[numIndices];
        
        // Generate vertices
        int vertexIndex = 0;
        int textureIndex = 0;
        
        for (int lat = 0; lat <= latitudeBands; lat++) {
            float theta = lat * (float) Math.PI / latitudeBands;
            float sinTheta = (float) Math.sin(theta);
            float cosTheta = (float) Math.cos(theta);
            
            for (int longitude = 0; longitude <= longitudeBands; longitude++) {
                float phi = longitude * 2 * (float) Math.PI / longitudeBands;
                float sinPhi = (float) Math.sin(phi);
                float cosPhi = (float) Math.cos(phi);
                
                float x = cosPhi * sinTheta;
                float y = cosTheta;
                float z = sinPhi * sinTheta;
                
                // Vertex position
                vertices[vertexIndex++] = x * radius;
                vertices[vertexIndex++] = y * radius;
                vertices[vertexIndex++] = z * radius;
                
                // Texture coordinates
                textureCoords[textureIndex++] = 1.0f - (float) longitude / longitudeBands;
                textureCoords[textureIndex++] = 1.0f - (float) lat / latitudeBands;
            }
        }
        
        // Generate indices
        int index = 0;
        for (int lat = 0; lat < latitudeBands; lat++) {
            for (int longitude = 0; longitude < longitudeBands; longitude++) {
                int first = lat * (longitudeBands + 1) + longitude;
                int second = first + longitudeBands + 1;
                
                indices[index++] = first;
                indices[index++] = second;
                indices[index++] = first + 1;
                
                indices[index++] = second;
                indices[index++] = second + 1;
                indices[index++] = first + 1;
            }
        }

        Model model = loader.loadModel(vertices, textureCoords, indices);
        model.setTexture(new Texture(loader.loadTexture("src/main/resources/textures/test.png")));
        entity = new Entity(model, new Vector3f(0.0f, 0.0f, -5.0f), new Vector3f(0.0f, 0.0f, 0.0f), 1.0f);
    }

    @Override
    public void input() {
        cameraInc.set(0.0f, 0.0f, 0.0f);
        cameraRotation.set(0.0f, 0.0f, 0.0f);

        // Toggle mouse control with Escape key
        if (window.isKeyPressed(GLFW.GLFW_KEY_ESCAPE)) {
            mouseControlEnabled = !mouseControlEnabled;
            if (mouseControlEnabled) {
                // Initialize mouse position
                lastMouseX = window.getCursorX();
                lastMouseY = window.getCursorY();
            }
        }

        // Handle mouse movement for camera rotation when enabled
        if (mouseControlEnabled) {
            double mouseX = window.getCursorX();
            double mouseY = window.getCursorY();
            
            // Calculate mouse movement delta
            double deltaX = mouseX - lastMouseX;
            double deltaY = mouseY - lastMouseY;
            
            // Apply rotation based on mouse movement
            cameraRotation.y = (float) deltaX * MOUSE_SENSITIVITY;
            cameraRotation.x = (float) deltaY * MOUSE_SENSITIVITY;
            
            // Update last mouse position
            lastMouseX = mouseX;
            lastMouseY = mouseY;
        }

        //WASD controls - now normalized to world coordinates
        //Forward and backward
        if(window.isKeyPressed(GLFW.GLFW_KEY_W)) {
            cameraInc.z = -1.0f;
        }
        if(window.isKeyPressed(GLFW.GLFW_KEY_S)) {
            cameraInc.z = 1.0f;
        }
        //side-to-side
        if(window.isKeyPressed(GLFW.GLFW_KEY_A)) {
            cameraInc.x = -1.0f;
        }
        if(window.isKeyPressed(GLFW.GLFW_KEY_D)) {
            cameraInc.x = 1.0f;
        }

        if(window.isKeyPressed(GLFW.GLFW_KEY_SPACE)) {
            cameraInc.y = 1.0f;
        }
        if(window.isKeyPressed(GLFW.GLFW_KEY_LEFT_CONTROL) || window.isKeyPressed(GLFW.GLFW_KEY_C)) {
            cameraInc.y = -1.0f;
        }
    }

    @Override
    public void update() {
        // Apply camera rotation first
        camera.moveRotation(cameraRotation.x * CAMERA_ROTATION_SPEED, cameraRotation.y * CAMERA_ROTATION_SPEED, 0.0f);
        
        // Apply movement in world coordinates (not relative to camera)
        camera.movePositionNormalized(cameraInc.x * CAMERA_MOVE_SPEED, cameraInc.y * CAMERA_MOVE_SPEED, cameraInc.z * CAMERA_MOVE_SPEED);

        entity.incRot(0.0f, 0.05f, 0.0f);
    }

    @Override
    public void render() {
        if(window.isResize()) {
            GL11.glViewport(0, 0, window.getWidth(), window.getHeight());
            window.setResize(true);
        }

        window.setClearColor(0.0f,0.0f,0.0f, 1.0f);
        renderer.render(entity, camera);
    }

    @Override
    public void cleanup() {
        renderer.cleanup();
        loader.cleanup();
    }
}
