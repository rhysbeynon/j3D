package com.discardsoft.j3D.tools.jEdit;

import com.discardsoft.j3D.tools.jEdit.ui.*;
import com.discardsoft.j3D.tools.jEdit.model.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;

/**
 * Main window for the jEDIT level editor.
 * 
 * This class creates and manages the main editor interface including:
 * - Menu bar with file operations
 * - Toolbar with common tools
 * - 2D viewport with grid and entity representations
 * - View switching (top, side, front)
 * - Zoom and pan controls
 */
public class JEditWindow extends JFrame {
    
    private static final String TITLE = "jEDIT - j3D Level Editor";
    private static final int DEFAULT_WIDTH = 1200;
    private static final int DEFAULT_HEIGHT = 800;
    
    // Core components
    private LevelModel levelModel;
    private ViewportPanel viewport;
    private ToolbarPanel toolbar;
    private PropertiesPanel propertiesPanel;
    private StatusPanel statusPanel;
    
    // File handling
    private File currentFile;
    private boolean hasUnsavedChanges;
    private File rootDirectory; // The root directory of the jEDIT application
    
    /**
     * Gets the root directory of the jEDIT application.
     * This is used as the default directory for file choosers.
     */
    private File getRootDirectory() {
        if (rootDirectory == null) {
            try {
                // Get the directory containing the application
                rootDirectory = new File(System.getProperty("user.dir"));
            } catch (Exception e) {
                // If we can't get the application directory, use the home directory
                rootDirectory = new File(System.getProperty("user.home"));
            }
        }
        return rootDirectory;
    }
    
    public JEditWindow() {
        initializeComponents();
        setupLayout();
        setupMenuBar();
        setupEventHandlers();
        
        // Set window properties
        setTitle(TITLE);
        setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Create new empty level
        newLevel();
    }
    
    private void initializeComponents() {
        // Initialize the level model
        levelModel = new LevelModel();
        
        // Create UI components
        viewport = new ViewportPanel(levelModel);
        toolbar = new ToolbarPanel(this);
        propertiesPanel = new PropertiesPanel(levelModel);
        statusPanel = new StatusPanel();
        
        // Set up component relationships
        levelModel.addChangeListener(e -> {
            hasUnsavedChanges = true;
            updateTitle();
            viewport.repaint();
            statusPanel.setStatus("Level modified");
        });
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // Main content area
        JSplitPane mainSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        mainSplit.setLeftComponent(viewport);
        mainSplit.setRightComponent(propertiesPanel);
        mainSplit.setDividerLocation(900);
        mainSplit.setResizeWeight(0.8);
        
        // Add components to frame
        add(toolbar, BorderLayout.NORTH);
        add(mainSplit, BorderLayout.CENTER);
        add(statusPanel, BorderLayout.SOUTH);
    }
    
    private void setupMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        
        // File menu
        JMenu fileMenu = new JMenu("File");
        fileMenu.add(createMenuItem("New Level", "Ctrl+N", this::newLevel));
        fileMenu.add(createMenuItem("Open Level...", "Ctrl+O", this::openLevel));
        fileMenu.addSeparator();
        fileMenu.add(createMenuItem("Save Level", "Ctrl+S", this::saveLevel));
        fileMenu.add(createMenuItem("Save Level As...", "Ctrl+Shift+S", this::saveLevelAs));
        fileMenu.addSeparator();
        fileMenu.add(createMenuItem("Export to j3D...", null, this::exportLevel));
        fileMenu.add(createMenuItem("Import from j3D...", null, this::importLevel));
        fileMenu.addSeparator();
        fileMenu.add(createMenuItem("Exit", "Ctrl+Q", this::exitApplication));
        
        // Edit menu
        JMenu editMenu = new JMenu("Edit");
        editMenu.add(createMenuItem("Undo", "Ctrl+Z", this::undo));
        editMenu.add(createMenuItem("Redo", "Ctrl+Y", this::redo));
        editMenu.addSeparator();
        editMenu.add(createMenuItem("Delete Selected", "Delete", this::deleteSelected));
        editMenu.add(createMenuItem("Select All", "Ctrl+A", this::selectAll));
        
        // View menu
        JMenu viewMenu = new JMenu("View");
        viewMenu.add(createMenuItem("Top View", "1", () -> viewport.setViewMode(ViewMode.TOP)));
        viewMenu.add(createMenuItem("Side View", "2", () -> viewport.setViewMode(ViewMode.SIDE)));
        viewMenu.add(createMenuItem("Front View", "3", () -> viewport.setViewMode(ViewMode.FRONT)));
        viewMenu.addSeparator();
        viewMenu.add(createMenuItem("Zoom In", "Ctrl+=", () -> viewport.zoomIn()));
        viewMenu.add(createMenuItem("Zoom Out", "Ctrl+-", () -> viewport.zoomOut()));
        viewMenu.add(createMenuItem("Reset Zoom", "Ctrl+0", () -> viewport.resetZoom()));
        viewMenu.addSeparator();
        viewMenu.add(createMenuItem("Show Grid", "G", () -> viewport.toggleGrid()));
        
        // Help menu
        JMenu helpMenu = new JMenu("Help");
        helpMenu.add(createMenuItem("About jEDIT", null, this::showAbout));
        
        menuBar.add(fileMenu);
        menuBar.add(editMenu);
        menuBar.add(viewMenu);
        menuBar.add(helpMenu);
        
        setJMenuBar(menuBar);
    }
    
    private JMenuItem createMenuItem(String text, String accelerator, Runnable action) {
        JMenuItem item = new JMenuItem(text);
        if (accelerator != null) {
            item.setAccelerator(KeyStroke.getKeyStroke(accelerator));
        }
        item.addActionListener(e -> action.run());
        return item;
    }
    
    private void setupEventHandlers() {
        // Window closing handler
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                exitApplication();
            }
        });
    }
    
    private void updateTitle() {
        String title = TITLE;
        if (currentFile != null) {
            title += " - " + currentFile.getName();
        } else {
            title += " - Untitled";
        }
        if (hasUnsavedChanges) {
            title += " *";
        }
        setTitle(title);
    }
    
    // File operations
    public void newLevel() {
        if (!confirmDiscardChanges()) return;
        
        levelModel.clear();
        currentFile = null;
        hasUnsavedChanges = false;
        updateTitle();
        statusPanel.setStatus("New level created");
    }
    
    public void openLevel() {
        if (!confirmDiscardChanges()) return;
        
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
            "j3D Level Files (*.j3dl)", "j3dl"));
        chooser.setCurrentDirectory(getRootDirectory());
        
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                File file = chooser.getSelectedFile();
                levelModel.loadFromFile(file);
                currentFile = file;
                hasUnsavedChanges = false;
                updateTitle();
                statusPanel.setStatus("Level loaded: " + file.getName());
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                    "Failed to open level: " + e.getMessage(),
                    "Open Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    public void saveLevel() {
        if (currentFile == null) {
            saveLevelAs();
        } else {
            try {
                levelModel.saveToFile(currentFile);
                hasUnsavedChanges = false;
                updateTitle();
                statusPanel.setStatus("Level saved: " + currentFile.getName());
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                    "Failed to save level: " + e.getMessage(),
                    "Save Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    public void saveLevelAs() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
            "j3D Level Files (*.j3dl)", "j3dl"));
        chooser.setCurrentDirectory(getRootDirectory());
        
        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                File file = chooser.getSelectedFile();
                if (!file.getName().endsWith(".j3dl")) {
                    file = new File(file.getAbsolutePath() + ".j3dl");
                }
                levelModel.saveToFile(file);
                currentFile = file;
                hasUnsavedChanges = false;
                updateTitle();
                statusPanel.setStatus("Level saved: " + file.getName());
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                    "Failed to save level: " + e.getMessage(),
                    "Save Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void exportLevel() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
            "j3D Level Files (*.json)", "json"));
        chooser.setCurrentDirectory(getRootDirectory());
        
        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                File file = chooser.getSelectedFile();
                if (!file.getName().endsWith(".json")) {
                    file = new File(file.getAbsolutePath() + ".json");
                }
                levelModel.exportToJ3D(file);
                statusPanel.setStatus("Level exported: " + file.getName());
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                    "Failed to export level: " + e.getMessage(),
                    "Export Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void importLevel() {
        if (!confirmDiscardChanges()) return;
        
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
            "j3D Level Files (*.json)", "json"));
        chooser.setCurrentDirectory(getRootDirectory());
        
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                File file = chooser.getSelectedFile();
                levelModel.importFromJ3D(file);
                currentFile = null;
                hasUnsavedChanges = true;
                updateTitle();
                statusPanel.setStatus("Level imported: " + file.getName());
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                    "Failed to import level: " + e.getMessage(),
                    "Import Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void exitApplication() {
        if (!confirmDiscardChanges()) return;
        
        // Dispose the window instead of calling System.exit(0)
        // This allows the launcher to detect the window closing and restore itself
        dispose();
    }
    
    // Edit operations
    private void undo() {
        // TODO: Implement undo functionality
        statusPanel.setStatus("Undo not yet implemented");
    }
    
    private void redo() {
        // TODO: Implement redo functionality
        statusPanel.setStatus("Redo not yet implemented");
    }
    
    private void deleteSelected() {
        levelModel.deleteSelected();
        statusPanel.setStatus("Selected entities deleted");
    }
    
    private void selectAll() {
        levelModel.selectAll();
        statusPanel.setStatus("All entities selected");
    }
    
    private void showAbout() {
        JOptionPane.showMessageDialog(this,
            "jEDIT v1.0\n" +
            "2D Level Editor for j3D Engine\n\n" +
            "Created by DiscardSoft\n" +
            "Built with Java Swing",
            "About jEDIT",
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    private boolean confirmDiscardChanges() {
        if (!hasUnsavedChanges) return true;
        
        int result = JOptionPane.showConfirmDialog(this,
            "You have unsaved changes. Do you want to save before continuing?",
            "Unsaved Changes",
            JOptionPane.YES_NO_CANCEL_OPTION);
        
        if (result == JOptionPane.YES_OPTION) {
            saveLevel();
            return !hasUnsavedChanges; // Only continue if save was successful
        } else if (result == JOptionPane.NO_OPTION) {
            return true;
        } else {
            return false; // Cancel
        }
    }
    
    /**
     * Creates a JFileChooser with the root directory as the current directory.
     * @param filter The file extension filter to use
     * @return A new JFileChooser instance
     */
    private JFileChooser createFileChooser(javax.swing.filechooser.FileNameExtensionFilter filter) {
        JFileChooser chooser = new JFileChooser();
        chooser.setCurrentDirectory(getRootDirectory());
        if (filter != null) {
            chooser.setFileFilter(filter);
        }
        return chooser;
    }
    
    // Getters for components
    public LevelModel getLevelModel() { return levelModel; }
    public ViewportPanel getViewport() { return viewport; }
    public StatusPanel getStatusPanel() { return statusPanel; }
}
