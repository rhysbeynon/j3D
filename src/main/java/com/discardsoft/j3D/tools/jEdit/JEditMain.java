package com.discardsoft.j3D.tools.jEdit;

import javax.swing.*;

/**
 * Main class for jEDIT - A 2D level editor for j3D engine.
 * 
 * jEDIT provides a simple 2D interface for creating and editing 3D levels
 * with support for multiple views (top, side, front) and standard level
 * editing operations.
 * 
 * @author DiscardSoft
 * @version 1.0
 */
public class JEditMain {
    
    public static void main(String[] args) {
        // Set macOS specific properties
        System.setProperty("apple.laf.useScreenMenuBar", "true");
        System.setProperty("com.apple.mrj.application.apple.menu.about.name", "jEDIT");
        System.setProperty("apple.awt.application.name", "jEDIT");
        
        // Disable LWJGL's automatic initialization that might conflict with AWT/Swing
        System.setProperty("org.lwjgl.glfw.LibraryName", "disabled");
        
        // Set system look and feel for better integration
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("Failed to set system look and feel: " + e.getMessage());
        }
        
        // Debug information
        System.out.println("Starting jEDIT application...");
        System.out.println("Java version: " + System.getProperty("java.version"));
        System.out.println("OS: " + System.getProperty("os.name") + " " + System.getProperty("os.version"));
        
        // Create and show the editor window
        SwingUtilities.invokeLater(() -> {
            try {
                System.out.println("Creating jEDIT window");
                JEditWindow editor = new JEditWindow();
                System.out.println("Setting window visible");
                editor.setVisible(true);
                System.out.println("Window should be visible now");
            } catch (Exception e) {
                System.err.println("Exception while creating jEDIT window:");
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, 
                    "Failed to start jEDIT: " + e.getMessage(),
                    "jEDIT Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}
