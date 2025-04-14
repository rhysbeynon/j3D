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

public class ObjectLoader {

    /*
    We need vaos and vbos for the graphics pipeline.
    VAOs are a list of vertices, while VBOs are
    a list of VAOS, in a buffer to be rendered.
     */
    //Vertex Buffer Objects List
    private List<Integer> vaos = new ArrayList<>();
    //Vertex Array Objects list
    private List<Integer> vbos = new ArrayList<>();
    //Texture Object List
    private List<Integer> textures = new ArrayList<>();

    public Model loadModel(float[] vertices, float[] textureCoords, int[] indices) {
        int id = createVAO();
        storeIndicesBuffer(indices);
        storeDataInAttributeList(0, 3, vertices);
        storeDataInAttributeList(1, 2, textureCoords);
        unbind();
        return new Model(id, indices.length);
    }

    public int loadTexture(String path) throws Exception {
        int width;
        int height;
        ByteBuffer buffer;
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer widthBuffer = stack.mallocInt(1);
            IntBuffer heightBuffer = stack.mallocInt(1);
            IntBuffer channelBuffer = stack.mallocInt(1);

            buffer = STBImage.stbi_load(path, widthBuffer, heightBuffer, channelBuffer, 4);
            if(buffer == null) {
                //loads missing texture file if selection not found
                path = "src/main/resources/textures/errtex.png";
                buffer = STBImage.stbi_load(path, widthBuffer, heightBuffer, channelBuffer, 4);
                if(buffer == null) {
                    //throws fit if missing texture file could be loaded (Should never happen)
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

    private int createVAO() {
        int id = GL30.glGenVertexArrays();
        vaos.add(id);
        GL30.glBindVertexArray(id);
        return id;
    }

    private void storeIndicesBuffer(int[] indices) {
        int vbo = GL15.glGenBuffers();
        vbos.add(vbo);
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vbo);
        IntBuffer buffer = Utils.storeDataInIntBuffer(indices);
        GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
    }

    private void storeDataInAttributeList(int attributeNumber, int vertexCount, float[] data) {
        //generate VBO as an integer
        int vbo = GL15.glGenBuffers();
        //add generated buffer object to vbo list "vbos"
        vbos.add(vbo);
        //bind buffer object using openGL
        GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER, vbo);
        //Store vbo in buffer using our own method, and flip the order to be read from old-new
        FloatBuffer buffer = Utils.storeDataInFloatBuffer(data);
        //assigning data to our buffer so it draws normally
        GL30.glBufferData(GL30.GL_ARRAY_BUFFER, buffer, GL30.GL_STATIC_DRAW);
        //more openGL information to pass over. We make sure openGL uses floats for the buffer.
        GL20.glVertexAttribPointer(attributeNumber, vertexCount, GL11.GL_FLOAT, false, 0, 0);
        //we then arbitrarily bind the buffer to 0
        GL15.glBindBuffer(GL30.GL_ARRAY_BUFFER, 0);
    }

    /*
    Cleanup method for models
     */
    public void cleanup() {
        //for every VAO, delete it.
        for (int vao : vaos) {
            GL30.glDeleteVertexArrays(vao);
        }
        //for every VBO, delete that as well.
        for (int vbo : vbos) {
            GL30.glDeleteBuffers(vbo);
        }
        //for every texture, delete those too.
        for (int texture : textures) {
            GL11.glDeleteTextures(texture);
        }
    }

    /*
    simple opengl function to bind the VAO to a zero value.
    This effectively unbinds the VAO.
     */
    private void unbind() {
        GL30.glBindVertexArray(0);
    }
}
