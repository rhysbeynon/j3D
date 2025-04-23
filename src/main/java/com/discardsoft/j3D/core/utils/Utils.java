package com.discardsoft.j3D.core.utils;

import org.lwjgl.system.MemoryUtil;

import java.io.InputStream;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

/**
 * Utility class providing helper methods for common engine operations.
 * <p>
 * Contains methods for buffer management, resource loading, and other
 * utility functions used throughout the engine.
 * </p>
 * 
 * @author DISCVRD Software
 * @version 0.1
 */
public final class Utils {
    
    /**
     * Private constructor to prevent instantiation of utility class.
     */
    private Utils() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    /**
     * Creates a FloatBuffer from a float array.
     * <p>
     * Allocates memory for the buffer, fills it with the provided data,
     * and flips the buffer to prepare it for reading.
     * </p>
     *
     * @param data The float array to store in the buffer
     * @return A FloatBuffer containing the data
     */
    public static FloatBuffer storeDataInFloatBuffer(float[] data) {
        FloatBuffer buffer = MemoryUtil.memAllocFloat(data.length);
        buffer.put(data).flip();
        return buffer;
    }

    /**
     * Creates an IntBuffer from an int array.
     * <p>
     * Allocates memory for the buffer, fills it with the provided data,
     * and flips the buffer to prepare it for reading.
     * </p>
     *
     * @param data The int array to store in the buffer
     * @return An IntBuffer containing the data
     */
    public static IntBuffer storeDataInIntBuffer(int[] data) {
        IntBuffer buffer = MemoryUtil.memAllocInt(data.length);
        buffer.put(data).flip();
        return buffer;
    }

    /**
     * Loads a text resource from the classpath.
     * <p>
     * Reads the entire contents of a text file and returns it as a string.
     * </p>
     *
     * @param path The path to the resource, relative to the classpath
     * @return The contents of the resource as a string
     * @throws Exception If the resource cannot be read
     */
    public static String loadResource(String path) throws Exception {
        try (InputStream in = Utils.class.getResourceAsStream(path);
             Scanner scanner = new Scanner(in, StandardCharsets.UTF_8.name())) {
            return scanner.useDelimiter("\\A").next();
        } catch (NullPointerException e) {
            throw new Exception("Resource not found: " + path, e);
        }
    }
}
