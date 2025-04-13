package com.discardsoft.j3D.core.utils;

import com.discardsoft.j3D.core.entity.Camera;
import com.discardsoft.j3D.core.entity.Entity;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Transformation {

    public static Matrix4f createTransformationMatrix(Entity entity) {
        Matrix4f matrix = new Matrix4f();
        matrix.identity().translate(entity.getPos()).
                rotateX((float) Math.toRadians(entity.getRot().x)).
                rotateY((float) Math.toRadians(entity.getRot().y)).
                rotateZ((float) Math.toRadians(entity.getRot().z)).
                scale(entity.getScale());
        return matrix;
    };

    public static Matrix4f getViewMatrix(Camera camera) {
        Vector3f pos = camera.getPosition();
        Vector3f rot = camera.getRotation();
        Matrix4f matrix = new Matrix4f();
        matrix.identity();
        matrix.rotate((float) Math.toRadians(rot.x), new Vector3f(1, 0, 0)).
                rotate((float) Math.toRadians(rot.y), new Vector3f(0, 1, 0)).
                rotate((float) Math.toRadians(rot.z), new Vector3f(0, 0, 1));
        matrix.translate(-pos.x, -pos.y, -pos.z);
        return matrix;
    }

}
