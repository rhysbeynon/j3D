package com.discardsoft.j3D.launcher;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URI;

/**
 * Main launcher window for j3D project.
 * 
 * jLAUNCH provides a simple interface to launch either:
 * - j3D Engine (main game/engine)
 * - jEDIT Level Editor
 * 
 * Also includes a placeholder login system and help link.
 * 
 * @author DiscardSoft
 * @version 1.0
 */
public class JLaunchWindow extends JFrame {
    
    private static final String TITLE = "jLAUNCH - j3D Project Launcher";
    private static final int WINDOW_WIDTH = 500;
    private static final int WINDOW_HEIGHT = 400;
    
    // UI Colors - using vanilla system style like jEDIT
    private static final Color BACKGROUND_COLOR = Color.WHITE;
    private static final Color TEXT_COLOR = Color.BLACK;
    
    /**
     * Creates and initializes the launcher window.
     */
    public JLaunchWindow() {
        setupWindow();
        setupComponents();
        centerWindow();
    }
    
    /**
     * Sets up the main window properties.
     */
    private void setupWindow() {
        setTitle(TITLE);
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        
        // Use vanilla system background
        getContentPane().setBackground(BACKGROUND_COLOR);
        setLayout(new BorderLayout());
    }
    
    /**
     * Creates and arranges all UI components.
     */
    private void setupComponents() {
        // Create main panel with padding
        JPanel mainPanel = new JPanel();
        mainPanel.setBackground(BACKGROUND_COLOR);
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));
        
        // Title label
        JLabel titleLabel = createTitleLabel();
        mainPanel.add(titleLabel, BorderLayout.NORTH);
        
        // Center panel with main buttons
        JPanel centerPanel = createCenterPanel();
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        
        // Bottom panel with secondary buttons
        JPanel bottomPanel = createBottomPanel();
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
        
        add(mainPanel, BorderLayout.CENTER);
    }
    
    /**
     * Creates the title label.
     */
    private JLabel createTitleLabel() {
        JLabel titleLabel = new JLabel("jLAUNCH", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Dialog", Font.BOLD, 28));
        titleLabel.setForeground(TEXT_COLOR);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        return titleLabel;
    }
    
    /**
     * Creates the center panel with main launch buttons.
     */
    private JPanel createCenterPanel() {
        JPanel centerPanel = new JPanel();
        centerPanel.setBackground(BACKGROUND_COLOR);
        centerPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        
        // j3D Engine button
        JButton j3dButton = createMainButton("Launch j3D Engine", this::launchJ3D);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        centerPanel.add(j3dButton, gbc);
        
        // jEDIT Editor button
        JButton jeditButton = createMainButton("Launch jEDIT Editor", this::launchJEdit);
        gbc.gridy = 1;
        centerPanel.add(jeditButton, gbc);
        
        return centerPanel;
    }
    
    /**
     * Creates the bottom panel with secondary buttons.
     */
    private JPanel createBottomPanel() {
        JPanel bottomPanel = new JPanel();
        bottomPanel.setBackground(BACKGROUND_COLOR);
        bottomPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 20));
        
        // Login button (placeholder)
        JButton loginButton = createSecondaryButton("Login", this::showLoginTodo);
        bottomPanel.add(loginButton);
        
        // Help button
        JButton helpButton = createSecondaryButton("?", this::openHelp);
        helpButton.setPreferredSize(new Dimension(40, 30));
        bottomPanel.add(helpButton);
        
        return bottomPanel;
    }
    
    /**
     * Creates a main action button with consistent styling.
     */
    private JButton createMainButton(String text, ActionListener action) {
        JButton button = new JButton(text);
        button.setFont(new Font("Dialog", Font.PLAIN, 14));
        button.setPreferredSize(new Dimension(300, 40));
        // Use default system button styling - no custom colors
        button.addActionListener(action);
        return button;
    }
    
    /**
     * Creates a secondary button with smaller styling.
     */
    private JButton createSecondaryButton(String text, ActionListener action) {
        JButton button = new JButton(text);
        button.setFont(new Font("Dialog", Font.PLAIN, 12));
        button.setPreferredSize(new Dimension(80, 30));
        // Use default system button styling - no custom colors
        button.addActionListener(action);
        return button;
    }
    
    /**
     * Centers the window on the screen.
     */
    private void centerWindow() {
        setLocationRelativeTo(null);
    }
    
    /**
     * Launches the j3D Engine.
     */
    private void launchJ3D(ActionEvent e) {
        try {
            System.out.println("Launching j3D Engine...");
            
            // Create and start j3D in a separate thread
            Thread j3dThread = new Thread(() -> {
                try {
                    com.discardsoft.j3D.Main.main(new String[]{});
                } catch (Exception ex) {
                    SwingUtilities.invokeLater(() -> {
                        JOptionPane.showMessageDialog(this,
                            "Failed to launch j3D Engine: " + ex.getMessage(),
                            "Launch Error",
                            JOptionPane.ERROR_MESSAGE);
                    });
                    ex.printStackTrace();
                }
            });
            j3dThread.setDaemon(true);
            j3dThread.start();
            
            // Minimize launcher window
            setState(JFrame.ICONIFIED);
            
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                "Failed to launch j3D Engine: " + ex.getMessage(),
                "Launch Error",
                JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
    
    /**
     * Launches the jEDIT Level Editor.
     */
    private void launchJEdit(ActionEvent e) {
        try {
            System.out.println("Launching jEDIT Editor...");
            
            // Create and start jEDIT in a separate thread
            Thread jeditThread = new Thread(() -> {
                try {
                    com.discardsoft.j3D.tools.jEdit.SimpleJEditLauncher.main(new String[]{});
                } catch (Exception ex) {
                    SwingUtilities.invokeLater(() -> {
                        JOptionPane.showMessageDialog(this,
                            "Failed to launch jEDIT Editor: " + ex.getMessage(),
                            "Launch Error",
                            JOptionPane.ERROR_MESSAGE);
                    });
                    ex.printStackTrace();
                }
            });
            jeditThread.setDaemon(true);
            jeditThread.start();
            
            // Minimize launcher window
            setState(JFrame.ICONIFIED);
            
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                "Failed to launch jEDIT Editor: " + ex.getMessage(),
                "Launch Error",
                JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
    
    /**
     * Shows the login TODO placeholder.
     */
    private void showLoginTodo(ActionEvent e) {
        JOptionPane.showMessageDialog(this,
            "Login functionality is not yet implemented.\n\nThis is a placeholder for future authentication features.",
            "Login - TODO",
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    /**
     * Opens the help link in the default browser.
     */
    private void openHelp(ActionEvent e) {
        try {
            Desktop desktop = Desktop.getDesktop();
            if (desktop.isSupported(Desktop.Action.BROWSE)) {
                URI helpUri = new URI("https://www.google.com");
                desktop.browse(helpUri);
            } else {
                // Fallback: show URL in a dialog
                JOptionPane.showMessageDialog(this,
                    "Please open this URL in your browser:\nhttps://www.google.com",
                    "Help",
                    JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                "Failed to open help link: " + ex.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
}
