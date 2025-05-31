package com.discardsoft.j3D.tools.jEdit.ui;

import com.discardsoft.j3D.tools.jEdit.model.*;
import com.discardsoft.j3D.tools.jEdit.JEditWindow;

import javax.swing.*;

/**
 * Toolbar panel with common editing tools and view controls.
 */
public class ToolbarPanel extends JToolBar {
    
    private JEditWindow parent;
    private ButtonGroup viewModeGroup;
    
    public ToolbarPanel(JEditWindow parent) {
        this.parent = parent;
        setFloatable(false);
        setRollover(true);
        
        setupTools();
    }
    
    private void setupTools() {
        // File operations
        add(createButton("New", "Create new level", this::newLevel));
        add(createButton("Open", "Open level", this::openLevel));
        add(createButton("Save", "Save level", this::saveLevel));
        addSeparator();
        
        // Entity operations
        add(createButton("Add Cube", "Add cube entity", this::addCube));
        add(createButton("Add Sphere", "Add sphere entity", this::addSphere));
        add(createButton("Delete", "Delete selected", this::deleteSelected));
        addSeparator();
        
        // View mode buttons
        add(new JLabel("View: "));
        viewModeGroup = new ButtonGroup();
        
        JToggleButton topView = createToggleButton("Top", "Top view", () -> setViewMode(ViewMode.TOP));
        JToggleButton sideView = createToggleButton("Side", "Side view", () -> setViewMode(ViewMode.SIDE));
        JToggleButton frontView = createToggleButton("Front", "Front view", () -> setViewMode(ViewMode.FRONT));
        
        viewModeGroup.add(topView);
        viewModeGroup.add(sideView);
        viewModeGroup.add(frontView);
        
        add(topView);
        add(sideView);
        add(frontView);
        
        // Set default view
        topView.setSelected(true);
        
        addSeparator();
        
        // Zoom controls
        add(createButton("Zoom In", "Zoom in", this::zoomIn));
        add(createButton("Zoom Out", "Zoom out", this::zoomOut));
        add(createButton("Reset Zoom", "Reset zoom", this::resetZoom));
        addSeparator();
        
        // Grid toggle
        add(createButton("Grid", "Toggle grid", this::toggleGrid));
    }
    
    private JButton createButton(String text, String tooltip, Runnable action) {
        JButton button = new JButton(text);
        button.setToolTipText(tooltip);
        button.addActionListener(e -> action.run());
        button.setFocusable(false);
        return button;
    }
    
    private JToggleButton createToggleButton(String text, String tooltip, Runnable action) {
        JToggleButton button = new JToggleButton(text);
        button.setToolTipText(tooltip);
        button.addActionListener(e -> action.run());
        button.setFocusable(false);
        return button;
    }
    
    // Action handlers
    private void newLevel() {
        // Delegate to main window
        parent.newLevel();
    }
    
    private void openLevel() {
        // Delegate to main window
        parent.openLevel();
    }
    
    private void saveLevel() {
        // Delegate to main window
        parent.saveLevel();
    }
    
    private void addCube() {
        EditorEntity entity = new EditorEntity();
        entity.setName("Cube " + (parent.getLevelModel().getEntities().size() + 1));
        entity.setModelName("P_Cube");
        parent.getLevelModel().addEntity(entity);
        parent.getStatusPanel().setStatus("Added cube entity");
    }
    
    private void addSphere() {
        EditorEntity entity = new EditorEntity();
        entity.setName("Sphere " + (parent.getLevelModel().getEntities().size() + 1));
        entity.setModelName("P_Sphere_Medium");
        parent.getLevelModel().addEntity(entity);
        parent.getStatusPanel().setStatus("Added sphere entity");
    }
    
    private void deleteSelected() {
        parent.getLevelModel().deleteSelected();
    }
    
    private void setViewMode(ViewMode mode) {
        parent.getViewport().setViewMode(mode);
        parent.getStatusPanel().setStatus("Switched to " + mode.getDisplayName());
    }
    
    private void zoomIn() {
        parent.getViewport().zoomIn();
    }
    
    private void zoomOut() {
        parent.getViewport().zoomOut();
    }
    
    private void resetZoom() {
        parent.getViewport().resetZoom();
    }
    
    private void toggleGrid() {
        parent.getViewport().toggleGrid();
    }
}
