// -|+++++++++++++++++++++++++++++++++++++++++++++++++++++++++|-
// -|              04/16/2025 | DISCVRD Software              |-
// -|        j3D is a lightweight custom-built engine         |-
// -|        Made with LWJGL, openGL, JOML, and other         |-
// -|        helpful libraries for use on DiscardSoft         |-
// -|              Object Loader class for j3D                |-
// -|    Comments are always written above relevant context.  |-
// -|   ++++++++++++++++++++++++++++++++++++++++++++++++++    |-
// -|               Version: 0.1 In Development               |-
// -|   *some comments may be written by AI for convenience   |-
// -|+++++++++++++++++++++++++++++++++++++++++++++++++++++++++|-

package com.discardsoft.j3D.core;

import com.discardsoft.j3D.core.entity.Model;
import com.discardsoft.j3D.core.utils.Utils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * The ObjectLoader class is responsible for loading 3D models and textures
 * into memory and preparing them for rendering. It manages the creation
 * and cleanup of OpenGL resources such as VAOs, VBOs, and textures.
 */
public class ObjectLoader {

    // Vertex Array Objects (VAOs) store the structure of vertex data.
    private List<Integer> vaos = new ArrayList<>();

    // Vertex Buffer Objects (VBOs) store the actual vertex data.
    private List<Integer> vbos = new ArrayList<>();

    // Textures store image data for rendering.
    private List<Integer> textures = new ArrayList<>();

    /**
     * Loads a 3D model into memory.
     *
     * @param vertices The vertex positions of the model.
     * @param textureCoords The texture coordinates of the model.
     * @param indices The indices defining the model's faces.
     * @return A Model object containing the loaded data.
     */
    public Model loadModel(float[] vertices, float[] textureCoords, int[] indices) {
        int id = createVAO();
        storeIndicesBuffer(indices);
        storeDataInAttributeList(0, 3, vertices);
        storeDataInAttributeList(1, 2, textureCoords);
        unbind();
        return new Model(id, indices.length);
    }

    /**
     * Loads a texture from a file.
     *
     * @param path The file path to the texture.
     * @return The OpenGL texture ID.
     * @throws Exception If the texture cannot be loaded.
     */
    public int loadTexture(String path) throws Exception {
        int width;
        int height;
        ByteBuffer buffer;
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer widthBuffer = stack.mallocInt(1);
            IntBuffer heightBuffer = stack.mallocInt(1);
            IntBuffer channelBuffer = stack.mallocInt(1);

            buffer = STBImage.stbi_load(path, widthBuffer, heightBuffer, channelBuffer, 4);
            if (buffer == null) {
                // Load a default error texture if the specified texture is missing.
                path = "src/main/resources/textures/errtex.png";
                buffer = STBImage.stbi_load(path, widthBuffer, heightBuffer, channelBuffer, 4);
                if (buffer == null) {
                    throw new Exception("Failed to load " + path + " texture file: " + STBImage.stbi_failure_reason());
                }
            }

            width = widthBuffer.get();
            height = heightBuffer.get();
        }

        int id = GL11.glGenTextures();
        textures.add(id);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, id);
        GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, width, height, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);
        GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);
        STBImage.stbi_image_free(buffer);
        return id;
    }

    /**
     * Creates a new Vertex Array Object (VAO).
     *
     * @return The ID of the created VAO.
     */
    private int createVAO() {
        int id = GL30.glGenVertexArrays();
        vaos.add(id);
        GL30.glBindVertexArray(id);
        return id;
    }

    /**
     * Stores indices in an OpenGL Element Array Buffer.
     *
     * @param indices The indices to store.
     */
    private void storeIndicesBuffer(int[] indices) {
        int vbo = GL15.glGenBuffers();
        vbos.add(vbo);
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vbo);
        IntBuffer buffer = Utils.storeDataInIntBuffer(indices);
        GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
    }

    /**
     * Stores vertex data in an OpenGL Array Buffer.
     *
     * @param attributeNumber The attribute number in the VAO.
     * @param vertexCount The number of components per vertex.
     * @param data The vertex data to store.
     */
    private void storeDataInAttributeList(int attributeNumber, int vertexCount, float[] data) {
        int vbo = GL15.glGenBuffers();
        vbos.add(vbo);
        GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER, vbo);
        FloatBuffer buffer = Utils.storeDataInFloatBuffer(data);
        GL30.glBufferData(GL30.GL_ARRAY_BUFFER, buffer, GL30.GL_STATIC_DRAW);
        GL20.glVertexAttribPointer(attributeNumber, vertexCount, GL11.GL_FLOAT, false, 0, 0);
        GL15.glBindBuffer(GL30.GL_ARRAY_BUFFER, 0);
    }

    /**
     * Cleans up OpenGL resources used by the ObjectLoader.
     * Deletes all VAOs, VBOs, and textures created by this class.
     */
    public void cleanup() {
        for (int vao : vaos) {
            GL30.glDeleteVertexArrays(vao);
        }
        for (int vbo : vbos) {
            GL30.glDeleteBuffers(vbo);
        }
        for (int texture : textures) {
            GL11.glDeleteTextures(texture);
        }
    }

    /**
     * Unbinds the currently bound VAO.
     */
    private void unbind() {
        GL30.glBindVertexArray(0);
    }
}
