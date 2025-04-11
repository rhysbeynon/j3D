package com.discardsoft.j3D.core;

public interface ILogic {

    void init() throws Exception;

    void input();

    void update();

    void render();

    void cleanup();

}
