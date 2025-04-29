package com.discardsoft.j3D.core.ui;

import com.discardsoft.j3D.core.WindowManager;
import com.discardsoft.j3D.core.ShaderManager;
import com.discardsoft.j3D.core.utils.Utils;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Manages the rendering and updating of all UI elements.
 * <p>
 * The UIManager class is responsible for initializing UI shaders,
 * managing UI elements, and rendering the UI hierarchy with proper
 * ordering and transparency.
 * </p>
 *
 * @author DiscardSoft
 * @version 0.1
 */
public class UIManager {
    
    /** The window manager reference */
    private final WindowManager window;
    
    /** List of all UI elements */
    private final List<UIElement> elements;
    
    /** Shader program manager for UI rendering */
    private final ShaderManager uiShader;
    
    /**
     * Creates a new UI manager.
     * 
     * @param window The window manager reference
     * @throws Exception If shader initialization fails
     */
    public UIManager(WindowManager window) throws Exception {
        this.window = window;
        this.elements = new ArrayList<>();
        
        // Initialize UI shader
        uiShader = new ShaderManager();
        uiShader.createVertexShader(Utils.loadResource("/shaders/ui/ui_vertex.glsl"));
        uiShader.createFragmentShader(Utils.loadResource("/shaders/ui/ui_fragment.glsl"));
        uiShader.link();
        
        // Create uniforms
        uiShader.createUniform("projectionMatrix");
        uiShader.createUniform("modelMatrix");
        uiShader.createUniform("textureSampler");
        uiShader.createUniform("useTransparency");
    }
    
    /**
     * Adds a UI element to be managed and rendered.
     * 
     * @param element The UI element to add
     */
    public void addElement(UIElement element) {
        elements.add(element);
        sortElements();
    }
    
    /**
     * Removes a UI element from management.
     * 
     * @param element The UI element to remove
     */
    public void removeElement(UIElement element) {
        elements.remove(element);
    }
    
    /**
     * Sorts the UI elements by z-index for proper rendering order.
     */
    private void sortElements() {
        elements.sort(Comparator.comparingInt(UIElement::getZIndex));
    }
    
    /**
     * Updates all UI elements.
     * 
     * @param window The window manager for input processing
     */
    public void update(WindowManager window) {
        for (UIElement element : elements) {
            if (element.isVisible()) {
                element.update(window);
            }
        }
    }
    
    /**
     * Renders all visible UI elements.
     */
    public void render() {
        if (elements.isEmpty()) {
            return;
        }
        
        // Bind UI shader
        uiShader.bind();
        
        // Set orthographic projection matrix for 2D rendering
        uiShader.setUniform("projectionMatrix", window.getOrthographicMatrix());
        uiShader.setUniform("textureSampler", 0);
        
        // Set up OpenGL state for UI rendering
        // Save previous state
        boolean depthTestEnabled = GL11.glIsEnabled(GL11.GL_DEPTH_TEST);
        
        // Disable depth testing temporarily for UI rendering
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        
        // Enable blending for transparency
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        
        // Render each UI element
        for (UIElement element : elements) {
            if (!element.isVisible()) {
                continue;
            }
            
            // Set transparency flag based on the element
            uiShader.setUniform("useTransparency", element.hasTransparency() ? 1 : 0);
            
            // Set model matrix for this element
            uiShader.setUniform("modelMatrix", element.getModelMatrix());
            
            // Render the element if it's a Panel
            if (element instanceof Panel) {
                ((Panel) element).renderModel();
            }
            
            // Let the element render its children
            element.render();
        }
        
        // Restore OpenGL state
        if (depthTestEnabled) {
            GL11.glEnable(GL11.GL_DEPTH_TEST);
        }
        GL11.glDisable(GL11.GL_BLEND);
        
        // Unbind shader
        uiShader.unbind();
    }
    
    /**
     * Releases all resources used by the UI manager and its elements.
     */
    public void cleanup() {
        uiShader.cleanup();
        for (UIElement element : elements) {
            element.cleanup();
        }
    }
}