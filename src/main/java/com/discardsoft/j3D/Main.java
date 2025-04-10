/***********************************************************|-
 -|      04/10/2025 | DISCVRD Software | Rhys Beynon        |-
 -|        j3D is a lightweight custom build engine         |-
 -|        Made with LWJGL, openGL, JOML, and other         |-
 -|        helpful libraries for use on DiscardSoft         |-
 -|           Main class and entry point for j3D            |-
 -|    Comments are always written above relevant context.  |-
 -|   ++++++++++++++++++++++++++++++++++++++++++++++++++    |-
 -|               Version: 0.1 In Development               |-
 -|**********************************************************/

package com.discardsoft.j3D;

import com.discardsoft.j3D.core.EngineManager;
import com.discardsoft.j3D.core.WindowManager;
import com.discardsoft.j3D.core.utils.Consts;
import org.lwjgl.Version;

public class Main {

    private static WindowManager window;
    private static EngineManager engine;

    public static void main(String[] args) {
        window = new WindowManager(Consts.TITLE, 800, 600, false);
        engine = new EngineManager();
        try {
            engine.start();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static WindowManager getWindow() {
        return window;
    }
}