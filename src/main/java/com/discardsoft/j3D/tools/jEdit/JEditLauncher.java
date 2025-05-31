package com.discardsoft.j3D.tools.jEdit;

import javax.swing.*;
import java.awt.*;

/**
 * A clean launcher for jEDIT that avoids potential conflicts with LWJGL.
 * This launcher creates a Swing window without any LWJGL dependencies.
 */
public class JEditLauncher {
    
    public static void main(String[] args) {
        // Set macOS specific properties
        System.setProperty("apple.laf.useScreenMenuBar", "true");
        System.setProperty("com.apple.mrj.application.apple.menu.about.name", "jEDIT");
        System.setProperty("apple.awt.application.name", "jEDIT");
        
        // Debug information
        System.out.println("Starting jEDIT launcher...");
        System.out.println("Java version: " + System.getProperty("java.version"));
        System.out.println("OS: " + System.getProperty("os.name") + " " + System.getProperty("os.version"));
        
        // Set system look and feel for better integration
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            System.out.println("Set look and feel: " + UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("Failed to set system look and feel: " + e.getMessage());
        }
        
        // Create and show the editor window
        SwingUtilities.invokeLater(() -> {
            try {
                System.out.println("Creating jEDIT window");
                
                // Create the window first
                JFrame testFrame = new JFrame("jEDIT");
                testFrame.setSize(400, 300);
                testFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                JLabel label = new JLabel("Loading jEDIT", SwingConstants.CENTER);
                label.setFont(new Font("Arial", Font.BOLD, 24));
                testFrame.add(label);
                testFrame.setLocationRelativeTo(null);
                
                // Show the loading window to verify Swing is working
                System.out.println("Showing loading window");
                testFrame.setVisible(true);
                System.out.println("Loading window should be visible now");
                
                // Wait a bit then try to show the actual editor
                Timer timer = new Timer(2000, e -> {
                    try {
                        System.out.println("Creating actual jEDIT window");
                        JEditWindow editor = new JEditWindow();
                        System.out.println("Setting editor window visible");
                        editor.setVisible(true);
                        System.out.println("Editor window should be visible now");
                        
                        // Close loading window
                        testFrame.dispose();
                    } catch (Exception ex) {
                        System.err.println("Exception creating editor window:");
                        ex.printStackTrace();
                    }
                });
                timer.setRepeats(false);
                timer.start();
                
            } catch (Exception e) {
                System.err.println("Exception in launcher:");
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, 
                    "Failed to start jEDIT: " + e.getMessage(),
                    "jEDIT Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}
