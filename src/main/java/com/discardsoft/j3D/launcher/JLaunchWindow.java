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
    
    private static final String TITLE = "j3D";
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
        JButton jEditButton = createMainButton("Launch jEDIT Editor", this::launchJEDIT);
        gbc.gridy = 1;
        centerPanel.add(jEditButton, gbc);
        
        return centerPanel;
    }
    
    /**
     * Creates the bottom panel with secondary buttons.
     */
    private JPanel createBottomPanel() {
        JPanel bottomPanel = new JPanel();
        bottomPanel.setBackground(BACKGROUND_COLOR);
        bottomPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 20));
        
        // Exit button
        JButton exitButton = createSecondaryButton("Exit", this::exitApplication);
        bottomPanel.add(exitButton);
        
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
     * Launches the j3D Engine as a separate JVM process.
     * This is required on macOS to properly handle the -XstartOnFirstThread argument for OpenGL.
     */
    private void launchJ3D(ActionEvent e) {
        try {
            System.out.println("Launching j3D Engine...");
            
            // Launch j3D as a separate JVM process with proper OpenGL arguments
            Thread j3dThread = new Thread(() -> {
                try {
                    // Get the current Java executable path
                    String javaHome = System.getProperty("java.home");
                    String javaBin = javaHome + "/bin/java";
                    
                    // Get the classpath from current JVM
                    String classpath = System.getProperty("java.class.path");
                    
                    System.out.println("DEBUG: Java binary: " + javaBin);
                    System.out.println("DEBUG: Classpath: " + classpath);
                    
                    // Build the command with proper JVM arguments for macOS OpenGL
                    ProcessBuilder pb = new ProcessBuilder();
                    pb.command(javaBin,
                        "-XstartOnFirstThread",              // Required for OpenGL on macOS
                        "-Djava.awt.headless=false",         // Ensure GUI is enabled
                        "-cp", classpath,                    // Use current classpath
                        "com.discardsoft.j3D.Main"          // Main class to launch
                    );
                    
                    System.out.println("DEBUG: Command to execute: " + pb.command());
                    
                    // Redirect error stream to see any errors
                    pb.redirectErrorStream(true);
                    
                    // Start the process
                    Process process = pb.start();
                    
                    // Read and print the output to see what's happening
                    java.io.BufferedReader reader = new java.io.BufferedReader(
                        new java.io.InputStreamReader(process.getInputStream()));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        System.out.println("j3D Output: " + line);
                    }
                    
                    // Monitor process in background
                    int exitCode = process.waitFor();
                    System.out.println("DEBUG: j3D process exited with code: " + exitCode);
                    
                    // Restore the launcher window when j3D exits
                    SwingUtilities.invokeLater(() -> {
                        setState(JFrame.NORMAL);
                        toFront();
                        requestFocus();
                        System.out.println("j3D Engine closed - launcher restored");
                        
                        // Show error message only if j3D exited with an error code
                        if (exitCode != 0) {
                            JOptionPane.showMessageDialog(this,
                                "j3D Engine exited with code: " + exitCode,
                                "j3D Engine Exit",
                                JOptionPane.WARNING_MESSAGE);
                        }
                    });
                    
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
     * When jEDIT is closed, the launcher will be restored.
     */
    private void launchJEDIT(ActionEvent e) {
        try {
            System.out.println("Launching jEDIT Editor...");
            
            // Create and start jEDIT in a separate thread
            Thread jEditThread = new Thread(() -> {
                try {
                    // Create a callback to restore the launcher when jEDIT closes
                    Runnable onJEditClosed = () -> {
                        SwingUtilities.invokeLater(() -> {
                            // Restore the launcher window
                            setState(JFrame.NORMAL);
                            toFront();
                            requestFocus();
                            System.out.println("jEDIT closed - launcher restored");
                        });
                    };
                    
                    // Create the jEDIT window with our callback
                    SwingUtilities.invokeAndWait(() -> {
                        try {
                            System.out.println("Creating jEDIT window...");
                            com.discardsoft.j3D.tools.jEdit.JEditWindow editor = 
                                new com.discardsoft.j3D.tools.jEdit.JEditWindow();
                            
                            // Set up a window listener to detect when jEDIT is closed
                            editor.addWindowListener(new java.awt.event.WindowAdapter() {
                                @Override
                                public void windowClosed(java.awt.event.WindowEvent e) {
                                    onJEditClosed.run();
                                }
                            });
                            
                            System.out.println("Setting jEDIT window visible...");
                            editor.setVisible(true);
                            System.out.println("jEDIT window should now be visible");
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
            jEditThread.setDaemon(true);
            jEditThread.start();
            
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
     * Safely exits the j3D application.
     */
    private void exitApplication(ActionEvent e) {
        int result = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to exit j3D?",
            "Exit j3D",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE);
            
        if (result == JOptionPane.YES_OPTION) {
            System.out.println("Exiting j3D Launcher...");
            System.exit(0);
        }
    }
    
    /**
     * Opens the help link in the default browser.
     */
    private void openHelp(ActionEvent e) {
        try {
            Desktop desktop = Desktop.getDesktop();
            if (desktop.isSupported(Desktop.Action.BROWSE)) {
                URI helpUri = new URI("https://github.com/rhysbeynon");
                desktop.browse(helpUri);
            } else {
                // Fallback: show URL in a dialog
                JOptionPane.showMessageDialog(this,
                    "Please open this URL in your browser:\nhttps://github.com/rhysbeynon",
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
