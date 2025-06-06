package com.discardsoft.j3D.tools.jEdit.ui;

import com.discardsoft.j3D.tools.jEdit.model.*;
import org.joml.Vector3f;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

/**
 * Properties panel for editing selected entities.
 */
public class PropertiesPanel extends JPanel {
    
    private LevelModel levelModel;
    private boolean updating = false;
    
    // Entity properties
    private JTextField nameField;
    private JComboBox<String> modelComboBox;
    private JSpinner posXSpinner, posYSpinner, posZSpinner;
    private JSpinner rotXSpinner, rotYSpinner, rotZSpinner;
    private JSpinner scaleXSpinner, scaleYSpinner, scaleZSpinner;
    
    // Level properties
    private JTextField levelNameField;
    private JTextArea levelDescField;
    private JTextField authorField;
    
    // Terrain properties
    private JCheckBox terrainEnabledCheckBox;
    private JSpinner terrainSizeSpinner;
    private JSpinner terrainGridCountSpinner;
    private JSpinner terrainHeightSpinner;
    private JSpinner terrainPosXSpinner, terrainPosYSpinner, terrainPosZSpinner;
    private JTextField terrainTextureField;
    private JSpinner terrainTextureRepeatSpinner;
    
    public PropertiesPanel(LevelModel levelModel) {
        this.levelModel = levelModel;
        
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(300, 0));
        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        setupComponents();
        setupEventHandlers();
        updateFromSelection();
    }
    
    private void setupComponents() {
        JTabbedPane tabs = new JTabbedPane();
        
        // Entity properties tab
        JPanel entityPanel = createEntityPropertiesPanel();
        tabs.addTab("Entity", entityPanel);
        
        // Level properties tab
        JPanel levelPanel = createLevelPropertiesPanel();
        tabs.addTab("Level", levelPanel);
        
        // Terrain properties tab
        JPanel terrainPanel = createTerrainPropertiesPanel();
        tabs.addTab("Terrain", terrainPanel);
        
        add(tabs, BorderLayout.CENTER);
    }
    
    private JPanel createEntityPropertiesPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        
        // Basic properties
        JPanel basicPanel = new JPanel(new GridBagLayout());
        basicPanel.setBorder(new TitledBorder("Basic Properties"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(2, 2, 2, 2);
        
        // Name
        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.WEST;
        basicPanel.add(new JLabel("Name:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        nameField = new JTextField();
        basicPanel.add(nameField, gbc);
        
        // Model
        gbc.gridx = 0; gbc.gridy = 1; gbc.anchor = GridBagConstraints.WEST; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        basicPanel.add(new JLabel("Model:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        modelComboBox = new JComboBox<>(new String[]{
            "P_Cube", "P_Sphere_Small", "P_Sphere_Medium", 
            "Suzanne", "PlayerCapsule", "grass_x", "grass2D"
        });
        basicPanel.add(modelComboBox, gbc);
        
        panel.add(basicPanel);
        
        // Transform properties
        JPanel transformPanel = new JPanel(new GridBagLayout());
        transformPanel.setBorder(new TitledBorder("Transform"));
        
        // Position
        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.WEST; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        transformPanel.add(new JLabel("Position:"), gbc);
        
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 0.33;
        posXSpinner = createFloatSpinner(-1000, 1000, 0.1);
        transformPanel.add(posXSpinner, gbc);
        
        gbc.gridx = 2;
        posYSpinner = createFloatSpinner(-1000, 1000, 0.1);
        transformPanel.add(posYSpinner, gbc);
        
        gbc.gridx = 3;
        posZSpinner = createFloatSpinner(-1000, 1000, 0.1);
        transformPanel.add(posZSpinner, gbc);
        
        // Rotation
        gbc.gridx = 0; gbc.gridy = 1; gbc.anchor = GridBagConstraints.WEST; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        transformPanel.add(new JLabel("Rotation:"), gbc);
        
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 0.33;
        rotXSpinner = createFloatSpinner(-360, 360, 1.0);
        transformPanel.add(rotXSpinner, gbc);
        
        gbc.gridx = 2;
        rotYSpinner = createFloatSpinner(-360, 360, 1.0);
        transformPanel.add(rotYSpinner, gbc);
        
        gbc.gridx = 3;
        rotZSpinner = createFloatSpinner(-360, 360, 1.0);
        transformPanel.add(rotZSpinner, gbc);
        
        // Scale
        gbc.gridx = 0; gbc.gridy = 2; gbc.anchor = GridBagConstraints.WEST; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        transformPanel.add(new JLabel("Scale:"), gbc);
        
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 0.33;
        scaleXSpinner = createFloatSpinner(0.01, 100, 0.1);
        transformPanel.add(scaleXSpinner, gbc);
        
        gbc.gridx = 2;
        scaleYSpinner = createFloatSpinner(0.01, 100, 0.1);
        transformPanel.add(scaleYSpinner, gbc);
        
        gbc.gridx = 3;
        scaleZSpinner = createFloatSpinner(0.01, 100, 0.1);
        transformPanel.add(scaleZSpinner, gbc);
        
        panel.add(transformPanel);
        
        // Add some spacing
        panel.add(Box.createVerticalGlue());
        
        return panel;
    }
    
    private JPanel createLevelPropertiesPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Level name
        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.WEST;
        panel.add(new JLabel("Level Name:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        levelNameField = new JTextField();
        panel.add(levelNameField, gbc);
        
        // Author
        gbc.gridx = 0; gbc.gridy = 1; gbc.anchor = GridBagConstraints.WEST; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        panel.add(new JLabel("Author:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        authorField = new JTextField();
        panel.add(authorField, gbc);
        
        // Description
        gbc.gridx = 0; gbc.gridy = 2; gbc.anchor = GridBagConstraints.NORTHWEST; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        panel.add(new JLabel("Description:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.BOTH; gbc.weightx = 1.0; gbc.weighty = 1.0;
        levelDescField = new JTextArea(5, 20);
        levelDescField.setLineWrap(true);
        levelDescField.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(levelDescField);
        panel.add(scrollPane, gbc);
        
        return panel;
    }
    
    private JPanel createTerrainPropertiesPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        
        // Terrain Enable/Disable
        JPanel enablePanel = new JPanel(new GridBagLayout());
        enablePanel.setBorder(new TitledBorder("Terrain Settings"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(2, 2, 2, 2);
        
        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.WEST;
        terrainEnabledCheckBox = new JCheckBox("Enable Terrain");
        enablePanel.add(terrainEnabledCheckBox, gbc);
        
        panel.add(enablePanel);
        
        // Terrain Properties
        JPanel propsPanel = new JPanel(new GridBagLayout());
        propsPanel.setBorder(new TitledBorder("Terrain Properties"));
        
        // Size
        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.WEST; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        propsPanel.add(new JLabel("Size:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        terrainSizeSpinner = createFloatSpinner(1.0, 10000.0, 1.0);
        propsPanel.add(terrainSizeSpinner, gbc);
        
        // Grid Count
        gbc.gridx = 0; gbc.gridy = 1; gbc.anchor = GridBagConstraints.WEST; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        propsPanel.add(new JLabel("Grid Count:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        terrainGridCountSpinner = new JSpinner(new SpinnerNumberModel(64, 4, 512, 1));
        propsPanel.add(terrainGridCountSpinner, gbc);
        
        // Height
        gbc.gridx = 0; gbc.gridy = 2; gbc.anchor = GridBagConstraints.WEST; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        propsPanel.add(new JLabel("Height:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        terrainHeightSpinner = createFloatSpinner(-1000.0, 1000.0, 0.1);
        propsPanel.add(terrainHeightSpinner, gbc);
        
        // Position
        gbc.gridx = 0; gbc.gridy = 3; gbc.anchor = GridBagConstraints.WEST; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        propsPanel.add(new JLabel("Position:"), gbc);
        
        JPanel posPanel = new JPanel(new GridLayout(1, 3, 2, 0));
        terrainPosXSpinner = createFloatSpinner(-10000.0, 10000.0, 1.0);
        terrainPosYSpinner = createFloatSpinner(-10000.0, 10000.0, 1.0);
        terrainPosZSpinner = createFloatSpinner(-10000.0, 10000.0, 1.0);
        posPanel.add(terrainPosXSpinner);
        posPanel.add(terrainPosYSpinner);
        posPanel.add(terrainPosZSpinner);
        
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        propsPanel.add(posPanel, gbc);
        
        // Texture
        gbc.gridx = 0; gbc.gridy = 4; gbc.anchor = GridBagConstraints.WEST; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        propsPanel.add(new JLabel("Texture:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        terrainTextureField = new JTextField();
        propsPanel.add(terrainTextureField, gbc);
        
        // Texture Repeat
        gbc.gridx = 0; gbc.gridy = 5; gbc.anchor = GridBagConstraints.WEST; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        propsPanel.add(new JLabel("Texture Repeat:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        terrainTextureRepeatSpinner = createFloatSpinner(0.1, 1000.0, 1.0);
        propsPanel.add(terrainTextureRepeatSpinner, gbc);
        
        panel.add(propsPanel);
        
        // Add some spacing
        panel.add(Box.createVerticalGlue());
        
        return panel;
    }

    private JSpinner createFloatSpinner(double min, double max, double step) {
        // Ensure the default value is within min-max range
        double defaultValue = 0.0;
        // If 0.0 is outside the range, use the minimum value as default
        if (defaultValue < min) {
            defaultValue = min;
        } else if (defaultValue > max) {
            defaultValue = max;
        }
        
        SpinnerNumberModel model = new SpinnerNumberModel(defaultValue, min, max, step);
        JSpinner spinner = new JSpinner(model);
        JSpinner.NumberEditor editor = new JSpinner.NumberEditor(spinner, "0.0");
        spinner.setEditor(editor);
        return spinner;
    }
    
    private void setupEventHandlers() {
        // Entity property change handlers
        nameField.addActionListener(this::updateEntityFromFields);
        modelComboBox.addActionListener(this::updateEntityFromFields);
        
        posXSpinner.addChangeListener(e -> updateEntityFromFields(null));
        posYSpinner.addChangeListener(e -> updateEntityFromFields(null));
        posZSpinner.addChangeListener(e -> updateEntityFromFields(null));
        
        rotXSpinner.addChangeListener(e -> updateEntityFromFields(null));
        rotYSpinner.addChangeListener(e -> updateEntityFromFields(null));
        rotZSpinner.addChangeListener(e -> updateEntityFromFields(null));
        
        scaleXSpinner.addChangeListener(e -> updateEntityFromFields(null));
        scaleYSpinner.addChangeListener(e -> updateEntityFromFields(null));
        scaleZSpinner.addChangeListener(e -> updateEntityFromFields(null));
        
        // Level property change handlers
        levelNameField.addActionListener(this::updateLevelFromFields);
        authorField.addActionListener(this::updateLevelFromFields);
        levelDescField.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                updateLevelFromFields(null);
            }
        });
        
        // Terrain property change handlers
        terrainEnabledCheckBox.addActionListener(this::updateTerrainFromFields);
        terrainSizeSpinner.addChangeListener(e -> updateTerrainFromFields(null));
        terrainGridCountSpinner.addChangeListener(e -> updateTerrainFromFields(null));
        terrainHeightSpinner.addChangeListener(e -> updateTerrainFromFields(null));
        terrainPosXSpinner.addChangeListener(e -> updateTerrainFromFields(null));
        terrainPosYSpinner.addChangeListener(e -> updateTerrainFromFields(null));
        terrainPosZSpinner.addChangeListener(e -> updateTerrainFromFields(null));
        terrainTextureField.addActionListener(this::updateTerrainFromFields);
        terrainTextureRepeatSpinner.addChangeListener(e -> updateTerrainFromFields(null));
        
        // Listen for selection changes
        levelModel.addChangeListener(e -> updateFromSelection());
    }
    
    private void updateEntityFromFields(ActionEvent e) {
        if (updating) return;
        
        List<EditorEntity> selected = levelModel.getSelectedEntities();
        if (selected.isEmpty()) return;
        
        // Update all selected entities
        for (EditorEntity entity : selected) {
            entity.setName(nameField.getText());
            entity.setModelName((String) modelComboBox.getSelectedItem());
            
            Vector3f position = new Vector3f(
                ((Number) posXSpinner.getValue()).floatValue(),
                ((Number) posYSpinner.getValue()).floatValue(),
                ((Number) posZSpinner.getValue()).floatValue()
            );
            entity.setPosition(position);
            
            Vector3f rotation = new Vector3f(
                ((Number) rotXSpinner.getValue()).floatValue(),
                ((Number) rotYSpinner.getValue()).floatValue(),
                ((Number) rotZSpinner.getValue()).floatValue()
            );
            entity.setRotation(rotation);
            
            Vector3f scale = new Vector3f(
                ((Number) scaleXSpinner.getValue()).floatValue(),
                ((Number) scaleYSpinner.getValue()).floatValue(),
                ((Number) scaleZSpinner.getValue()).floatValue()
            );
            entity.setScale(scale);
        }
    }
    
    private void updateLevelFromFields(ActionEvent e) {
        if (updating) return;
        
        levelModel.setLevelName(levelNameField.getText());
        levelModel.setAuthor(authorField.getText());
        levelModel.setLevelDescription(levelDescField.getText());
    }
    
    private void updateTerrainFromFields(ActionEvent e) {
        if (updating) return;
        
        levelModel.setTerrainEnabled(terrainEnabledCheckBox.isSelected());
        levelModel.setTerrainSize(((Number) terrainSizeSpinner.getValue()).floatValue());
        levelModel.setTerrainGridCount(((Number) terrainGridCountSpinner.getValue()).intValue());
        levelModel.setTerrainHeight(((Number) terrainHeightSpinner.getValue()).floatValue());
        
        Vector3f position = new Vector3f(
            ((Number) terrainPosXSpinner.getValue()).floatValue(),
            ((Number) terrainPosYSpinner.getValue()).floatValue(),
            ((Number) terrainPosZSpinner.getValue()).floatValue()
        );
        levelModel.setTerrainPosition(position);
        
        levelModel.setTerrainTexture(terrainTextureField.getText());
        levelModel.setTerrainTextureRepeat(((Number) terrainTextureRepeatSpinner.getValue()).floatValue());
    }
    
    private void updateFromSelection() {
        updating = true;
        
        List<EditorEntity> selected = levelModel.getSelectedEntities();
        
        // Update entity properties
        if (selected.isEmpty()) {
            // Clear fields
            nameField.setText("");
            modelComboBox.setSelectedIndex(0);
            posXSpinner.setValue(0.0);
            posYSpinner.setValue(0.0);
            posZSpinner.setValue(0.0);
            rotXSpinner.setValue(0.0);
            rotYSpinner.setValue(0.0);
            rotZSpinner.setValue(0.0);
            scaleXSpinner.setValue(1.0);
            scaleYSpinner.setValue(1.0);
            scaleZSpinner.setValue(1.0);
            
            // Disable fields
            setEntityFieldsEnabled(false);
        } else if (selected.size() == 1) {
            // Single selection - show properties
            EditorEntity entity = selected.get(0);
            nameField.setText(entity.getName());
            modelComboBox.setSelectedItem(entity.getModelName());
            
            Vector3f pos = entity.getPosition();
            posXSpinner.setValue((double) pos.x);
            posYSpinner.setValue((double) pos.y);
            posZSpinner.setValue((double) pos.z);
            
            Vector3f rot = entity.getRotation();
            rotXSpinner.setValue((double) rot.x);
            rotYSpinner.setValue((double) rot.y);
            rotZSpinner.setValue((double) rot.z);
            
            Vector3f scale = entity.getScale();
            scaleXSpinner.setValue((double) scale.x);
            scaleYSpinner.setValue((double) scale.y);
            scaleZSpinner.setValue((double) scale.z);
            
            setEntityFieldsEnabled(true);
        } else {
            // Multiple selection - show mixed values or averages
            nameField.setText("<Multiple>");
            modelComboBox.setSelectedItem("<Multiple>");
            
            // Could calculate averages here
            setEntityFieldsEnabled(true);
        }
        
        // Update level properties
        levelNameField.setText(levelModel.getLevelName());
        authorField.setText(levelModel.getAuthor());
        levelDescField.setText(levelModel.getLevelDescription());
        
        // Update terrain properties
        terrainEnabledCheckBox.setSelected(levelModel.isTerrainEnabled());
        terrainSizeSpinner.setValue((double) levelModel.getTerrainSize());
        terrainGridCountSpinner.setValue(levelModel.getTerrainGridCount());
        terrainHeightSpinner.setValue((double) levelModel.getTerrainHeight());
        
        Vector3f terrainPos = levelModel.getTerrainPosition();
        terrainPosXSpinner.setValue((double) terrainPos.x);
        terrainPosYSpinner.setValue((double) terrainPos.y);
        terrainPosZSpinner.setValue((double) terrainPos.z);
        
        terrainTextureField.setText(levelModel.getTerrainTexture());
        terrainTextureRepeatSpinner.setValue((double) levelModel.getTerrainTextureRepeat());
        
        updating = false;
    }
    
    private void setEntityFieldsEnabled(boolean enabled) {
        nameField.setEnabled(enabled);
        modelComboBox.setEnabled(enabled);
        posXSpinner.setEnabled(enabled);
        posYSpinner.setEnabled(enabled);
        posZSpinner.setEnabled(enabled);
        rotXSpinner.setEnabled(enabled);
        rotYSpinner.setEnabled(enabled);
        rotZSpinner.setEnabled(enabled);
        scaleXSpinner.setEnabled(enabled);
        scaleYSpinner.setEnabled(enabled);
        scaleZSpinner.setEnabled(enabled);
    }
}
