package com.discardsoft.j3D.core.utils;

/**
 * Utility class for rendering text to the screen.
 * <p>
 * Provides simple 2D text rendering capabilities for debug information
 * and user interface elements.
 * </p>
 *
 * @author DiscardSoft
 * @version 0.1
 */
public class TextRenderer {
    
    // Screen dimensions (kept for compatibility)
    private int screenWidth;
    private int screenHeight;
    
    /**
     * Constructs a new text renderer for the specified screen dimensions.
     *
     * @param screenWidth The width of the screen in pixels
     * @param screenHeight The height of the screen in pixels
     */
    public TextRenderer(int screenWidth, int screenHeight) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
    }
    
    /**
     * Renders debug text to the console instead of on screen.
     * This avoids OpenGL compatibility issues.
     *
     * @param text The text to output
     * @param x The x position (ignored in this implementation)
     * @param y The y position (ignored in this implementation) 
     * @param color The color (ignored in this implementation)
     */
    public void renderText(String text, int x, int y, float[] color) {
        if (text == null || text.isEmpty()) {
            return;
        }
        
        // Output debug information to console instead of rendering on screen
        System.out.println("====== DEBUG INFO ======");
        System.out.println(text);
        System.out.println("=======================");
    }
    
    /**
     * Updates the screen dimensions when the window is resized.
     *
     * @param width The new screen width in pixels
     * @param height The new screen height in pixels
     */
    public void updateScreenSize(int width, int height) {
        this.screenWidth = width;
        this.screenHeight = height;
    }
    
    /**
     * Gets the screen width.
     *
     * @return The current screen width in pixels
     */
    public int getScreenWidth() {
        return screenWidth;
    }
    
    /**
     * Gets the screen height.
     *
     * @return The current screen height in pixels
     */
    public int getScreenHeight() {
        return screenHeight;
    }
    
    /**
     * Renders text with a background rectangle.
     * 
     * @param text The text to render
     * @param x The x position in pixels
     * @param y The y position in pixels
     * @param textColor The color of the text
     * @param backgroundColor The color of the background (with alpha)
     */
    public void renderTextWithBackground(String text, int x, int y, float[] textColor, float[] backgroundColor) {
        if (text == null || text.isEmpty()) {
            return;
        }
        
        // Output pause message to console instead of rendering on screen
        System.out.println("====== PAUSE OVERLAY ======");
        System.out.println(text);
        System.out.println("==========================");
    }
}