package com.discardsoft.j3D.core.ui;

import com.discardsoft.j3D.core.WindowManager;
import com.discardsoft.j3D.core.entity.Texture;

/**
 * A menu panel that can contain UI elements and handle interactions.
 * <p>
 * Menus are specialized panels that can be toggled on and off
 * and can contain various interactive UI elements.
 * </p>
 *
 * @author DiscardSoft
 * @version 0.1
 */
public class Menu extends Panel {
    
    /** Menu active state flag */
    private boolean active;
    
    /** Menu name identifier */
    private final String name;
    
    /**
     * Creates a new menu with the specified texture.
     * 
     * @param texture The texture to use for this menu
     * @param name The name identifier for this menu
     * @param zIndex The z-index for rendering order
     */
    public Menu(Texture texture, String name, int zIndex) {
        super(texture, zIndex);
        this.name = name;
        this.active = false;
        this.visible = false;
    }
    
    /**
     * Creates a new menu with the specified texture path.
     * 
     * @param texturePath The path to the texture resource
     * @param name The name identifier for this menu
     * @param zIndex The z-index for rendering order
     */
    public Menu(String texturePath, String name, int zIndex) {
        super(texturePath, zIndex);
        this.name = name;
        this.active = false;
        this.visible = false;
    }
    
    @Override
    public void update(WindowManager window) {
        if (!active || !visible) {
            return;
        }
        
        // Process input events for this menu
        handleInput(window);
        
        // Update all child elements
        super.update(window);
    }
    
    /**
     * Handle input events for this menu.
     * Override this method in subclasses to implement menu-specific input handling.
     * 
     * @param window The window manager for input processing
     */
    protected void handleInput(WindowManager window) {
        // Base implementation does nothing
        // Override in subclasses for specific menu behavior
    }
    
    /**
     * Shows this menu and sets it as active.
     */
    public void show() {
        this.visible = true;
        this.active = true;
    }
    
    /**
     * Hides this menu and sets it as inactive.
     */
    public void hide() {
        this.visible = false;
        this.active = false;
    }
    
    /**
     * Toggles the visibility of this menu.
     * 
     * @return true if the menu is now visible, false otherwise
     */
    public boolean toggleVisibility() {
        if (visible) {
            hide();
        } else {
            show();
        }
        return visible;
    }
    
    /**
     * Checks if this menu is currently active.
     * 
     * @return true if the menu is active, false otherwise
     */
    public boolean isActive() {
        return active;
    }
    
    /**
     * Sets whether this menu is active.
     * 
     * @param active true to make the menu active, false otherwise
     */
    public void setActive(boolean active) {
        this.active = active;
    }
    
    /**
     * Gets the name of this menu.
     * 
     * @return The menu name
     */
    public String getName() {
        return name;
    }
}