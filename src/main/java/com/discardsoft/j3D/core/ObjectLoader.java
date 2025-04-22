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

import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
     */
    public Model loadModel(float[] vertices, float[] textureCoords, float[] normalsArray, int[] indices) {
        int id = createVAO();
        storeIndicesBuffer(indices);
        storeDataInAttributeList(0, 3, vertices);
        storeDataInAttributeList(1, 2, textureCoords);
        storeDataInAttributeList(2, 3, normalsArray);
        unbind();
        return new Model(id, indices.length);
    }

    /**
     * Loads a texture from a file.
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

    /*
     *
     * Imports a 3D model from an OBJ file, including vertices, texture coordinates, and normals.
     *
     */
    public Model importOBJ(String filePath) throws Exception {
        List<Float> vertices = new ArrayList<>();
        List<Float> textureCoords = new ArrayList<>();
        List<Float> normals = new ArrayList<>();
        Map<String, Integer> uniqueVertices = new HashMap<>();
        List<Float> finalVertices = new ArrayList<>();
        List<Float> finalTextureCoords = new ArrayList<>();
        List<Float> finalNormals = new ArrayList<>();
        List<Integer> indices = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] tokens = line.split(" ");
                switch (tokens[0]) {
                    case "v": // Vertex position
                        vertices.add(Float.parseFloat(tokens[1]));
                        vertices.add(Float.parseFloat(tokens[2]));
                        vertices.add(Float.parseFloat(tokens[3]));
                        break;
                    case "vt": // Texture coordinate
                        textureCoords.add(Float.parseFloat(tokens[1]));
                        textureCoords.add(Float.parseFloat(tokens[2]));
                        break;
                    case "vn": // Normal vector
                        normals.add(Float.parseFloat(tokens[1]));
                        normals.add(Float.parseFloat(tokens[2]));
                        normals.add(Float.parseFloat(tokens[3]));
                        break;
                    case "f": // Face (indices)
                        for (int i = 1; i < tokens.length; i++) {
                            String[] parts = tokens[i].split("/");
                            int vertexIndex = Integer.parseInt(parts[0]) - 1;
                            int textureIndex = parts.length > 1 && !parts[1].isEmpty() ? Integer.parseInt(parts[1]) - 1 : -1;
                            int normalIndex = parts.length > 2 && !parts[2].isEmpty() ? Integer.parseInt(parts[2]) - 1 : -1;

                            // Create a unique key for the vertex combination
                            String key = vertexIndex + "/" + textureIndex + "/" + normalIndex;
                            if (!uniqueVertices.containsKey(key)) {
                                // Add vertex position
                                finalVertices.add(vertices.get(vertexIndex * 3));
                                finalVertices.add(vertices.get(vertexIndex * 3 + 1));
                                finalVertices.add(vertices.get(vertexIndex * 3 + 2));

                                // Add texture coordinate if available
                                if (textureIndex != -1) {
                                    finalTextureCoords.add(textureCoords.get(textureIndex * 2));
                                    finalTextureCoords.add(textureCoords.get(textureIndex * 2 + 1));
                                } else {
                                    finalTextureCoords.add(0.0f);
                                    finalTextureCoords.add(0.0f);
                                }

                                // Add normal vector if available
                                if (normalIndex != -1) {
                                    finalNormals.add(normals.get(normalIndex * 3));
                                    finalNormals.add(normals.get(normalIndex * 3 + 1));
                                    finalNormals.add(normals.get(normalIndex * 3 + 2));
                                } else {
                                    finalNormals.add(0.0f);
                                    finalNormals.add(0.0f);
                                    finalNormals.add(0.0f);
                                }

                                // Store the index of this unique vertex
                                uniqueVertices.put(key, uniqueVertices.size());
                            }

                            // Add the index to the indices list
                            indices.add(uniqueVertices.get(key));
                        }
                        break;
                }
            }
        } catch (Exception e) {
            System.err.println("Failed to load model: " + filePath + ".");
            return importOBJ("src/main/resources/models/errmodel.obj");
        }

        float[] verticesArray = new float[finalVertices.size()];
        for (int i = 0; i < finalVertices.size(); i++) {
            verticesArray[i] = finalVertices.get(i);
        }

        float[] textureCoordsArray = new float[finalTextureCoords.size()];
        for (int i = 0; i < finalTextureCoords.size(); i++) {
            textureCoordsArray[i] = finalTextureCoords.get(i);
        }

        float[] normalsArray = new float[finalNormals.size()];
        for (int i = 0; i < finalNormals.size(); i++) {
            normalsArray[i] = finalNormals.get(i);
        }

        int[] indicesArray = new int[indices.size()];
        for (int i = 0; i < indices.size(); i++) {
            indicesArray[i] = indices.get(i);
        }

        return loadModel(verticesArray, textureCoordsArray, normalsArray, indicesArray);
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
