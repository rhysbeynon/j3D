package com.discardsoft.j3D.launcher;

import javax.swing.*;

/**
 * Main entry point for jLAUNCH - the j3D Project Launcher.
 * 
 * This application provides a simple GUI to launch either:
 * - j3D Engine (main game/engine)
 * - jEDIT Level Editor
 * 
 * Also includes placeholder login functionality and help access.
 * 
 * @author DiscardSoft
 * @version 1.0
 */
public class JLaunchMain {
    
    public static void main(String[] args) {
        // Set macOS specific properties for better integration
        System.setProperty("apple.laf.useScreenMenuBar", "true");
        System.setProperty("apple.awt.application.name", "jLAUNCH");
        System.setProperty("com.apple.mrj.application.apple.menu.about.name", "jLAUNCH");
        
        // Debug information
        System.out.println("Starting jLAUNCH - j3D Project Launcher");
        System.out.println("Java version: " + System.getProperty("java.version"));
        System.out.println("OS: " + System.getProperty("os.name") + " " + System.getProperty("os.version"));
        
        // Set system look and feel for better platform integration
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            System.out.println("Set look and feel: " + UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("Failed to set system look and feel: " + e.getMessage());
            // Continue with default look and feel
        }
        
        // Create and show the launcher window on the Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            try {
                System.out.println("Creating jLAUNCH window...");
                JLaunchWindow launcher = new JLaunchWindow();
                launcher.setVisible(true);
                System.out.println("jLAUNCH window should now be visible");
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null,
                    "Failed to start jLAUNCH: " + e.getMessage(),
                    "jLAUNCH Error",
                    JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            }
        });
    }
}
