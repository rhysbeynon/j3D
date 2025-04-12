package com.discardsoft.j3D.core.utils;

import org.lwjgl.system.MemoryUtil;

import java.io.InputStream;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class Utils {


    /*
    storeDataInFloatBuffer is the method for creating and handling
    memory for data buffers for models and meshes.

     */
    public static FloatBuffer storeDataInFloatBuffer(float[] data) {
        //create a memory buffer for our data and add the data to it
        FloatBuffer buffer = MemoryUtil.memAllocFloat(data.length);
        //add data to the buffer and flip it so that we read in bottom-to-top
        buffer.put(data).flip();
        //return buffer from function
        return buffer;
    }

    public static IntBuffer storeDataInIntBuffer(int[] data) {
        //create a memory buffer for our data and add the data to it
        IntBuffer buffer = MemoryUtil.memAllocInt(data.length);
        //add data to the buffer and flip it so that we read in bottom-to-top
        buffer.put(data).flip();
        //return buffer from function
        return buffer;
    }

    public static String loadResource(String path) throws Exception {
        String result;
        try (InputStream in = Utils.class.getResourceAsStream(path);
             Scanner scanner = new Scanner(in, StandardCharsets.UTF_8.name())) {
            result = scanner.useDelimiter("\\A").next();
        }
        return result;
    }

}
