package com.discardsoft.j3D.core.ui;

import com.discardsoft.j3D.core.WindowManager;
import com.discardsoft.j3D.core.utils.LoadModel;
import com.discardsoft.j3D.core.utils.Settings;

import org.lwjgl.glfw.GLFW;

/**
 * Specialized menu for the game's pause functionality.
 * <p>
 * The pause menu is shown when the game is paused and provides
 * options to resume the game or access other menus.
 * </p>
 *
 * @author DiscardSoft
 * @version 0.1
 */
public class PauseMenu extends Menu {
    
    /** Path to the pause menu background texture */
    private static final String MENU_TEXTURE = LoadModel.TEXTURES_PATH + "menuscreen.png";
    
    /** Default z-index for the pause menu */
    private static final int Z_INDEX = 10;
    
    /** Callback for the resume action */
    private Runnable resumeAction;
    
    /**
     * Creates a new pause menu.
     */
    public PauseMenu() {
        super(MENU_TEXTURE, "pauseMenu", Z_INDEX);
        
        setSize(Settings.UI_SCALE, Settings.UI_SCALE);
    }
    
    /**
     * Sets the action to be executed when resuming the game.
     * 
     * @param resumeAction The callback to execute when resuming
     */
    public void setResumeAction(Runnable resumeAction) {
        this.resumeAction = resumeAction;
    }
    
    @Override
    protected void handleInput(WindowManager window) {
        // Check for ESC key to close/hide the pause menu
        if (window.isKeyPressedBuffered(GLFW.GLFW_KEY_ESCAPE)) {
            hide();
            if (resumeAction != null) {
                resumeAction.run();
            }
        }
        
        // Removed the mouse click to resume game logic
    }
    
    @Override
    public void show() {
        super.show();
        // Add any show animation or sound effect here
    }
    
    @Override
    public void hide() {
        super.hide();
        // Add any hide animation or sound effect here
    }
}