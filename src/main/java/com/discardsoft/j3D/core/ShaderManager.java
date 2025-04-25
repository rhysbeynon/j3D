// -|+++++++++++++++++++++++++++++++++++++++++++++++++++++++++|-
// -|              04/16/2025 | DiscardSoft                   |-
// -|        j3D is a lightweight custom-built engine         |-
// -|        Made with LWJGL, openGL, JOML, and other         |-
// -|        helpful libraries for use on DiscardSoft         |-
// -|              Shader Manager class for j3D               |-
// -|    Comments are always written above relevant context.  |-
// -|   ++++++++++++++++++++++++++++++++++++++++++++++++++    |-
// -|               Version: 0.06a In Development             |-
// -|   *some comments may be written by AI for convenience   |-
// -|+++++++++++++++++++++++++++++++++++++++++++++++++++++++++|-

package com.discardsoft.j3D.core;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL20;
import org.lwjgl.system.MemoryStack;

import java.util.HashMap;
import java.util.Map;

/**
 * Manages OpenGL shader programs and uniform variables.
 * <p>
 * This class handles the creation, compilation, and linking of vertex and fragment shaders.
 * It also provides methods to create and set uniform variables that are passed to the shaders.
 * </p>
 * 
 * @author DiscardSoft
 * @version 0.1
 */
public class ShaderManager {

    /** The OpenGL shader program ID */
    private final int programId;
    
    /** The vertex shader ID */
    private int vertexShaderId;
    
    /** The fragment shader ID */
    private int fragmentShaderId;
    
    /** Map of uniform names to their locations in the shader program */
    private final Map<String, Integer> uniformLocations;

    /**
     * Constructs a new shader manager and creates an OpenGL shader program.
     *
     * @throws Exception If shader program creation fails
     */
    public ShaderManager() throws Exception {
        programId = GL20.glCreateProgram();
        if (programId == 0) {
            throw new Exception("Failed to create shader program using glCreateProgram");
        }

        uniformLocations = new HashMap<>();
    }

    /**
     * Creates a uniform variable in the shader program.
     * <p>
     * This method must be called after the shader program is linked and before setting the uniform.
     * </p>
     *
     * @param uniformName The name of the uniform in the shader
     * @throws Exception If the uniform cannot be found in the shader program
     */
    public void createUniform(String uniformName) throws Exception {
        int location = GL20.glGetUniformLocation(programId, uniformName);
        if (location < 0) {
            throw new Exception("Failed to find uniform: " + uniformName);
        }
        uniformLocations.put(uniformName, location);
    }

    /**
     * Sets a 4x4 matrix uniform value.
     *
     * @param uniformName The name of the uniform
     * @param value The matrix value to set
     */
    public void setUniform(String uniformName, Matrix4f value) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            GL20.glUniformMatrix4fv(
                uniformLocations.get(uniformName), 
                false, 
                value.get(stack.mallocFloat(16))
            );
        }
    }

    /**
     * Sets a 4D vector uniform value.
     *
     * @param uniformName The name of the uniform
     * @param value The vector value to set
     */
    public void setUniform(String uniformName, Vector4f value) {
        GL20.glUniform4f(
            uniformLocations.get(uniformName), 
            value.x, value.y, value.z, value.w
        );
    }

    /**
     * Sets a 3D vector uniform value.
     *
     * @param uniformName The name of the uniform
     * @param value The vector value to set
     */
    public void setUniform(String uniformName, Vector3f value) {
        GL20.glUniform3f(
            uniformLocations.get(uniformName), 
            value.x, value.y, value.z
        );
    }

    /**
     * Sets a float uniform value.
     *
     * @param uniformName The name of the uniform
     * @param value The float value to set
     */
    public void setUniform(String uniformName, float value) {
        GL20.glUniform1f(uniformLocations.get(uniformName), value);
    }

    /**
     * Sets an integer uniform value.
     *
     * @param uniformName The name of the uniform
     * @param value The integer value to set
     */
    public void setUniform(String uniformName, int value) {
        GL20.glUniform1i(uniformLocations.get(uniformName), value);
    }

    /**
     * Sets a boolean uniform value.
     *
     * @param uniformName The name of the uniform
     * @param value The boolean value to set
     */
    public void setUniform(String uniformName, boolean value) {
        GL20.glUniform1f(
            uniformLocations.get(uniformName), 
            value ? 1.0f : 0.0f
        );
    }

    /**
     * Creates and compiles a vertex shader.
     *
     * @param shaderCode The GLSL source code for the vertex shader
     * @throws Exception If shader creation or compilation fails
     */
    public void createVertexShader(String shaderCode) throws Exception {
        vertexShaderId = createShader(shaderCode, GL20.GL_VERTEX_SHADER);
    }

    /**
     * Creates and compiles a fragment shader.
     *
     * @param shaderCode The GLSL source code for the fragment shader
     * @throws Exception If shader creation or compilation fails
     */
    public void createFragmentShader(String shaderCode) throws Exception {
        fragmentShaderId = createShader(shaderCode, GL20.GL_FRAGMENT_SHADER);
    }

    /**
     * Creates and compiles a shader of the specified type.
     *
     * @param shaderCode The GLSL source code for the shader
     * @param shaderType The type of shader to create (GL_VERTEX_SHADER or GL_FRAGMENT_SHADER)
     * @return The shader ID
     * @throws Exception If shader creation or compilation fails
     */
    private int createShader(String shaderCode, int shaderType) throws Exception {
        int shaderId = GL20.glCreateShader(shaderType);
        if (shaderId == 0) {
            throw new Exception("Failed to create shader of type: " + shaderType);
        }

        GL20.glShaderSource(shaderId, shaderCode);
        GL20.glCompileShader(shaderId);
        
        if (GL20.glGetShaderi(shaderId, GL20.GL_COMPILE_STATUS) == 0) {
            String shaderTypeName = shaderType == GL20.GL_VERTEX_SHADER ? "vertex" : "fragment";
            throw new Exception(
                "Failed to compile " + shaderTypeName + " shader: " + 
                GL20.glGetShaderInfoLog(shaderId, 512)
            );
        }

        GL20.glAttachShader(programId, shaderId);
        return shaderId;
    }

    /**
     * Links the shader program.
     * <p>
     * This method must be called after creating the vertex and fragment shaders
     * and before using the shader program.
     * </p>
     *
     * @throws Exception If shader program linking fails
     */
    public void link() throws Exception {
        GL20.glLinkProgram(programId);
        if (GL20.glGetProgrami(programId, GL20.GL_LINK_STATUS) == 0) {
            throw new Exception(
                "Failed to link shader program: " + 
                GL20.glGetProgramInfoLog(programId, 512)
            );
        }
        
        // Detach shaders after linking to free resources
        if (vertexShaderId != 0) {
            GL20.glDetachShader(programId, vertexShaderId);
        }
        if (fragmentShaderId != 0) {
            GL20.glDetachShader(programId, fragmentShaderId);
        }
    }

    /**
     * Binds the shader program for use.
     * <p>
     * This method must be called before setting uniforms or drawing with the shader.
     * </p>
     */
    public void bind() {
        GL20.glUseProgram(programId);
    }

    /**
     * Unbinds the current shader program.
     */
    public void unbind() {
        GL20.glUseProgram(0);
    }

    /**
     * Releases resources used by this shader manager.
     * <p>
     * This should be called when the shader is no longer needed.
     * </p>
     */
    public void cleanup() {
        unbind();
        if (programId != 0) {
            GL20.glDeleteProgram(programId);
        }
    }
}
