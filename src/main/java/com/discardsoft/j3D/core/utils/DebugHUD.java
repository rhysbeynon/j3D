package com.discardsoft.j3D.core.utils;

import com.discardsoft.j3D.core.entity.Camera;
import com.discardsoft.j3D.core.entity.Entity;
import com.discardsoft.j3D.core.entity.Player;
import com.discardsoft.j3D.core.scene.TestScene;
import org.joml.Vector3f;

/**
 * Debug heads-up display for development and troubleshooting.
 * <p>
 * Provides real-time information about game state, performance,
 * camera position, and other debug data when activated with F3.
 * </p>
 * 
 * @author DISCVRD Software
 * @version 0.1
 */
public class DebugHUD {
    
    private final TextRenderer textRenderer;
    private final float[] TEXT_COLOR = {1.0f, 1.0f, 1.0f}; // White text
    private final int PADDING = 10;
    
    /**
     * Constructs a new debug HUD.
     *
     * @param screenWidth The width of the screen in pixels
     * @param screenHeight The height of the screen in pixels
     */
    public DebugHUD(int screenWidth, int screenHeight) {
        textRenderer = new TextRenderer(screenWidth, screenHeight);
    }
    
    /**
     * Renders the debug HUD with current game information.
     *
     * @param fps Current frames per second
     * @param player The player entity
     * @param scene The current game scene
     */
    public void render(int fps, Player player, TestScene scene) {
        StringBuilder debugInfo = new StringBuilder();
        
        // Game information
        debugInfo.append("j3D ").append(Consts.VERSION).append("\n");
        debugInfo.append("FPS: ").append(fps).append("\n");
        
        // Player position and rotation
        Camera camera = player.getCamera();
        Vector3f position = camera.getPosition();
        debugInfo.append(String.format("XYZ: %.2f / %.2f / %.2f\n", position.x, position.y, position.z));
        
        // Camera direction
        Vector3f rotation = camera.getRotation();
        debugInfo.append(String.format("Pitch/Yaw: %.1f / %.1f\n", rotation.x, rotation.y));
        
        // Free camera mode status
        debugInfo.append("Camera mode: ").append(player.isFreeCameraMode() ? "FREE" : "PLAYER").append("\n");
        
        // Entity count
        int entityCount = scene.getEntities().size();
        debugInfo.append("Entities: ").append(entityCount).append("\n");
        
        // Render the debug information
        textRenderer.renderText(debugInfo.toString(), PADDING, PADDING, TEXT_COLOR);
    }
    
    /**
     * Updates the screen dimensions when the window is resized.
     *
     * @param width The new screen width in pixels
     * @param height The new screen height in pixels
     */
    public void updateScreenSize(int width, int height) {
        textRenderer.updateScreenSize(width, height);
    }
}