package com.discardsoft.j3D.tools.jEdit.ui;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.*;

/**
 * Status bar panel showing current status messages.
 */
public class StatusPanel extends JPanel {
    
    private JLabel statusLabel;
    private JLabel coordinateLabel;
    private JLabel zoomLabel;
    
    public StatusPanel() {
        setLayout(new BorderLayout());
        setBorder(new BevelBorder(BevelBorder.LOWERED));
        setPreferredSize(new Dimension(0, 25));
        
        setupComponents();
    }
    
    private void setupComponents() {
        statusLabel = new JLabel("Ready");
        statusLabel.setBorder(BorderFactory.createEmptyBorder(3, 10, 3, 10));
        
        coordinateLabel = new JLabel("X: 0.0, Y: 0.0");
        coordinateLabel.setBorder(BorderFactory.createEmptyBorder(3, 10, 3, 10));
        
        zoomLabel = new JLabel("Zoom: 100%");
        zoomLabel.setBorder(BorderFactory.createEmptyBorder(3, 10, 3, 10));
        
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        rightPanel.add(coordinateLabel);
        rightPanel.add(new JSeparator(SwingConstants.VERTICAL));
        rightPanel.add(zoomLabel);
        
        add(statusLabel, BorderLayout.CENTER);
        add(rightPanel, BorderLayout.EAST);
    }
    
    public void setStatus(String status) {
        statusLabel.setText(status);
    }
    
    public void setCoordinates(float x, float y) {
        coordinateLabel.setText(String.format("X: %.1f, Y: %.1f", x, y));
    }
    
    public void setZoom(float zoomPercent) {
        zoomLabel.setText(String.format("Zoom: %.0f%%", zoomPercent));
    }
}
