/***********************************************************|-
 -|              04/10/2025 | DISCVRD Software              |-
 -|        j3D is a lightweight custom build engine         |-
 -|        Made with LWJGL, openGL, JOML, and other         |-
 -|        helpful libraries for use on DiscardSoft         |-
 -|              Render Manager class for j3D               |-
 -|    Comments are always written above relevant context.  |-
 -|   ++++++++++++++++++++++++++++++++++++++++++++++++++    |-
 -|               Version: 0.1 In Development               |-
 -|**********************************************************/

package com.discardsoft.j3D.core;

import com.discardsoft.j3D.Main;
import org.lwjgl.opengl.GL11;

public class RenderManager {
    private final WindowManager window;

    public RenderManager() {
        window = Main.getWindow();
    }

    public void init() throws Exception {

    }

    public void render() {

    }

    public void clear() {
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
    }

    public void cleanup() {

    }
}
