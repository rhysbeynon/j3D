package com.discardsoft.j3D.tools.jEdit;

import javax.swing.*;
import java.awt.*;

/**
 * A very simple launcher for jEDIT that provides detailed exception information.
 */
public class SimpleJEditLauncher {
    
    public static void main(String[] args) {
        try {
            // Set macOS specific properties
            System.setProperty("apple.laf.useScreenMenuBar", "true");
            System.setProperty("apple.awt.application.name", "jEDIT");
            
            // Set system look and feel
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            
            // Create and show the editor window on the EDT
            SwingUtilities.invokeLater(() -> {
                try {
                    System.out.println("Creating jEDIT window...");
                    JEditWindow editor = new JEditWindow();
                    System.out.println("Setting window visible...");
                    editor.setVisible(true);
                    System.out.println("Window should now be visible");
                } catch (Throwable e) {
                    e.printStackTrace();
                    showErrorDialog("Error creating editor window", e);
                }
            });
        } catch (Throwable e) {
            e.printStackTrace();
            showErrorDialog("Error initializing launcher", e);
        }
    }
    
    private static void showErrorDialog(String message, Throwable e) {
        // Build detailed error message
        StringBuilder sb = new StringBuilder();
        sb.append(message).append(":\n\n");
        sb.append(e.toString()).append("\n\n");
        
        // Add stack trace
        for (StackTraceElement element : e.getStackTrace()) {
            sb.append("  at ").append(element.toString()).append("\n");
        }
        
        // Show error dialog
        JOptionPane.showMessageDialog(
            null,
            sb.toString(),
            "jEDIT Error",
            JOptionPane.ERROR_MESSAGE
        );
    }
}
