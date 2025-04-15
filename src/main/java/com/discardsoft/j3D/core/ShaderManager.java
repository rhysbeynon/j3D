package com.discardsoft.j3D.core;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL20;
import org.lwjgl.system.MemoryStack;

import java.util.HashMap;
import java.util.Map;

public class ShaderManager {

    private final int programID;
    private int vertexShaderID;
    private int fragmentShaderID;
    private final Map<String, Integer> uniforms;

    public ShaderManager() throws Exception{
        programID = GL20.glCreateProgram();
        if(programID == 0) {
            throw new Exception("Failed to create shader program using glCreateProgram");
        }

        uniforms = new HashMap<>();
    }

    public void createUniform(String uniformName) throws Exception {
        int uniformLocation = GL20.glGetUniformLocation(programID, uniformName);
        if(uniformLocation < 0) {
            throw new Exception("Failed to find uniform " + uniformName);
        }
        uniforms.put(uniformName, uniformLocation);
    }


    /*
    "Set uniform" method variations.
    Uniforms are used to give shared instructions to the vertices in
    an entity.
     */
    public void setUniform(String uniformName, Matrix4f value) {
        try(MemoryStack stack = MemoryStack.stackPush()) {
            GL20.glUniformMatrix4fv(uniforms.get(uniformName), false, value.get(stack.mallocFloat(16)));
        }
    }
    public void setUniform(String uniformName, Vector4f value) {
        GL20.glUniform4f(uniforms.get(uniformName), value.x, value.y, value.z, value.w);
    }
    public void setUniform(String uniformName, Vector3f value) {
        GL20.glUniform3f(uniforms.get(uniformName), value.x, value.y, value.z);
    }
    public void setUniform(String uniformName, float value) {
        GL20.glUniform1f(uniforms.get(uniformName), value);
    }
    public void setUniform(String uniformName, int value) {
        GL20.glUniform1i(uniforms.get(uniformName), value);
    }
    public void setUniform(String uniformName, boolean value) {
        float res = 0;
        if(value) {
            res = 1;
        }
        GL20.glUniform1f(uniforms.get(uniformName), res);
    }

    public void createVertexShader(String shaderCode) throws Exception {
        vertexShaderID = createShader(shaderCode, GL20.GL_VERTEX_SHADER);
    }

    public void createFragmentShader(String shaderCode) throws Exception {
        fragmentShaderID = createShader(shaderCode, GL20.GL_FRAGMENT_SHADER);
    }

    public int createShader(String shaderCode, int shaderType) throws Exception {
        int shaderID = GL20.glCreateShader(shaderType);
        if(shaderID == 0) {
            throw new Exception("Failed to create shader");
        }

        GL20.glShaderSource(shaderID, shaderCode);
        GL20.glCompileShader(shaderID);
        if(GL20.glGetShaderi(shaderID, GL20.GL_COMPILE_STATUS) == 0) {
            throw new Exception("Failed to compile " + shaderType + " shader code: " + GL20.glGetShaderInfoLog(shaderID, 512));
        }

        GL20.glAttachShader(programID, shaderID);

        return shaderID;
    }

    public void link() throws Exception {
        GL20.glLinkProgram(programID);
        if(GL20.glGetProgrami(programID, GL20.GL_LINK_STATUS) == 0) {
            throw new Exception("Failed to link shader code: " + GL20.glGetProgramInfoLog(programID, 512));
        }
        if(vertexShaderID != 0) {
            GL20.glDetachShader(programID, vertexShaderID);
        }
        if(fragmentShaderID != 0) {
            GL20.glDetachShader(programID, fragmentShaderID);
        }
        // Validation is not necessary here as we validate during rendering
        // GL20.glValidateProgram(programID);
        // if(GL20.glGetProgrami(programID, GL20.GL_VALIDATE_STATUS) == 0) {
        //     System.err.println("Warning validating shader code: " + GL20.glGetProgramInfoLog(programID, 512));
        // }
    }


    /*
    Bind and unbind functions.
    Simple stuff.
     */
    public void bind() {
        GL20.glUseProgram(programID);
    }
    public void unbind() {
        GL20.glUseProgram(0);
    }

    public void cleanup() {
        unbind();
        if(programID != 0) {
            GL20.glDeleteProgram(programID);
        }
    }

}
