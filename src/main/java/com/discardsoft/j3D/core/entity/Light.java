package com.discardsoft.j3D.core.entity;

import org.joml.Vector3f;

public class Light {
    private final Vector3f position;
    private final Vector3f color;

    public Light() {
        this.position = new Vector3f(0, 0, 0);
        this.color = new Vector3f(1, 1, 1); // Default white light
    }

    public Light(Vector3f position, Vector3f color) {
        this.position = position;
        this.color = color;
    }

    public Vector3f getPosition() {
        return position;
    }

    public Vector3f getColor() {
        return color;
    }
    
}
