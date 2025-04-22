package com.discardsoft.j3D.core.entity;

import org.joml.Vector3f;

public class Light {
    private final Vector3f position;
    private final Vector3f color;
    private final Vector3f ambient;

    public Light() {
        this.position = new Vector3f(0, 0, 0);
        this.color = new Vector3f(1, 1, 1); // Default white light
        this.ambient = new Vector3f(0.2f, 0.2f, 0.2f);
    }

    public Light(Vector3f position, Vector3f color, Vector3f ambient) {
        this.position = position;
        this.color = color;
        this.ambient = ambient;
    }

    public Vector3f getPosition() {
        return position;
    }

    public Vector3f getColor() {
        return color;
    }
    public Vector3f getAmbient() {
        return ambient;
    }
    
}
