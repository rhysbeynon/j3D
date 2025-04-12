package com.discardsoft.j3D.core;

import com.discardsoft.j3D.core.entity.Model;
import com.discardsoft.j3D.core.utils.Utils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

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

    public Model loadModel(float[] vertices, int[] indices) {
        int id = createVAO();
        storeIndicesBuffer(indices);
        storeDataInAttributeList(0, 3, vertices);
        unbind();
        return new Model(id, vertices.length / 3);
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
    }

    /*
    simple opengl function to bind the VAO to a zero value.
    This effectively unbinds the VAO.
     */
    private void unbind() {
        GL30.glBindVertexArray(0);
    }
}
