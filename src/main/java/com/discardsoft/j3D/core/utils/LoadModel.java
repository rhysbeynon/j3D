package com.discardsoft.j3D.core.utils;

import com.discardsoft.j3D.core.ObjectLoader;
import com.discardsoft.j3D.core.entity.Model;
import com.discardsoft.j3D.core.entity.Texture;

public class LoadModel {
    static ObjectLoader loader = new ObjectLoader();

    public static Model model(String name) {
        try {
            Model model = loader.importOBJ("src/main/resources/models/" + name + ".obj");
            model.setTexture(new Texture(loader.loadTexture("src/main/resources/textures/" + name + ".png")));
            return model;
        } catch (Exception e) {
            e.printStackTrace();
            return null; // Return null if loading fails
        }
    }

    public static Model cube() {
        try {
            return loader.importOBJ("src/main/resources/models/engine/P_Cube.obj");
        } catch (Exception e) {
            e.printStackTrace();
            return null; // Return null if loading fails
        }
    }

    public static Model sphere_small() {
        try {
            return loader.importOBJ("src/main/resources/models/engine/P_Sphere_Small.obj");
        } catch (Exception e) {
            e.printStackTrace();
            return null; // Return null if loading fails
        }
    }

    public static Model sphere_medium() {
        try {
            return loader.importOBJ("src/main/resources/models/engine/P_Sphere_Medium.obj");
        } catch (Exception e) {
            e.printStackTrace();
            return null; // Return null if loading fails
        }
    }

    public static Model error() {
        try {
            return loader.importOBJ("src/main/resources/models/engine/errmodel.obj");
        } catch (Exception e) {
            e.printStackTrace();
            return null; // Return null if loading fails
        }
    }
}
