package com.discardsoft.j3D.tools.jEdit.model;

/**
 * Enumeration of available view modes in the editor.
 */
public enum ViewMode {
    TOP("Top View", "Looking down from above (Y-axis)"),
    SIDE("Side View", "Looking from the side (X-axis)"), 
    FRONT("Front View", "Looking from the front (Z-axis)");
    
    private final String displayName;
    private final String description;
    
    ViewMode(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }
    
    public String getDisplayName() { return displayName; }
    public String getDescription() { return description; }
}
