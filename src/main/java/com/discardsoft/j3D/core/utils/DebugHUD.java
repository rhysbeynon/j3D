package com.discardsoft.j3D.core.utils;

import com.discardsoft.j3D.core.entity.Camera;
import com.discardsoft.j3D.core.entity.Entity;
import com.discardsoft.j3D.core.entity.Player;
import com.discardsoft.j3D.core.scene.TestScene;
import org.joml.Vector3f;

/**
 * Provides debugging information overlay for development.
 * <p>
 * This class renders text-based debugging information to help
 * during development, including FPS count, player position,
 * and other useful metrics.
 * </p>
 *
 * @author DiscardSoft
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
     * @deprecated Use {@link #render(int, Player, TestScene, float)} instead
     */
    @Deprecated
    public void render(int fps, Player player, TestScene scene) {
        render(fps, player, scene, Settings.CAMERA_MOVE_SPEED);
    }
    
    /**
     * Renders the debug HUD with current game information including camera speed.
     *
     * @param fps Current frames per second
     * @param player The player entity
     * @param scene The current game scene
     * @param cameraMoveSpeed Current camera movement speed
     */
    public void render(int fps, Player player, TestScene scene, float cameraMoveSpeed) {
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
        
        // Camera speed (only shown in free camera mode)
        if (player.isFreeCameraMode()) {
            debugInfo.append(String.format("Camera speed: %.2f (scroll to adjust)\n", cameraMoveSpeed));
        }
        
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
    
    /**
     * Renders a pause overlay in the center of the screen.
     * This is displayed when the game is paused.
     */
    public void renderPauseOverlay() {
        // Get the screen dimensions from the text renderer
        int screenWidth = textRenderer.getScreenWidth();
        int screenHeight = textRenderer.getScreenHeight();
        
        // Create the pause message
        String pauseMessage = "GAME PAUSED\nClick to resume";
        
        // Calculate text position for center of the screen
        // Estimate text width (this is a rough approximation)
        int textWidth = pauseMessage.indexOf('\n') * 10;  // Approximate character width
        int textHeight = 50;  // Approximate height for two lines
        
        int x = (screenWidth - textWidth) / 2;
        int y = (screenHeight - textHeight) / 2;
        
        // Use a semi-transparent background
        float[] backgroundColor = {0.0f, 0.0f, 0.0f, 0.7f};  // Black with 70% opacity
        textRenderer.renderTextWithBackground(pauseMessage, x, y, TEXT_COLOR, backgroundColor);
    }
}