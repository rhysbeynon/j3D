/***********************************************************|-
 -|      04/10/2025 | DISCVRD Software | Rhys Beynon        |-
 -|        j3D is a lightweight custom-built engine         |-
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
import com.discardsoft.j3D.game.TestGame;
import org.lwjgl.Version;

public class Main {

    private static WindowManager window;
    private static TestGame game;

    public static void main(String[] args) {
        System.out.println("Starting j3D...");
        System.out.println("LWJGL Version: " + Version.getVersion());
        window = new WindowManager(Consts.TITLE, 800, 600, false);
        game = new TestGame();
        EngineManager engine = new EngineManager();
        try {
            engine.start();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static WindowManager getWindow() {
        return window;
    }

    public static TestGame getGame() {
        return game;
    }
}