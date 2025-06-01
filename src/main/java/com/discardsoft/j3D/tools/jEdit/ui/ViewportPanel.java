package com.discardsoft.j3D.tools.jEdit.ui;

import com.discardsoft.j3D.tools.jEdit.model.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.List;

/**
 * Main 2D viewport for level editing.
 * 
 * This panel provides the main editing area with:
 * - 2D representation of 3D entities
 * - Grid background
 * - Zoom and pan capabilities
 * - Mouse interaction for selection and movement
 * - Multiple view modes (top, side, front)
 */
public class ViewportPanel extends JPanel {
    
    private static final Color BACKGROUND_COLOR = Color.WHITE;
    private static final Color GRID_COLOR = new Color(220, 220, 220);
    private static final Color ENTITY_COLOR = Color.BLACK;
    private static final Color SELECTED_COLOR = Color.BLUE;
    private static final Color HOVER_COLOR = new Color(100, 100, 255, 100);
    
    private static final float GRID_SIZE = 10.0f;
    private static final float MIN_ZOOM = 0.1f;
    private static final float MAX_ZOOM = 10.0f;
    
    private LevelModel levelModel;
    private ViewMode viewMode = ViewMode.TOP;
    
    // View state
    private float zoom = 1.0f;
    private float panX = 0.0f;
    private float panY = 0.0f;
    private boolean showGrid = true;
    
    // Mouse interaction state
    private Point2D lastMousePos;
    private boolean isPanning = false;
    private boolean isDragging = false;
    private EditorEntity hoveredEntity = null;
    private Point2D dragStartWorld = null;
    private Point2D dragStartMouse = null;
    
    public ViewportPanel(LevelModel levelModel) {
        this.levelModel = levelModel;
        
        setBackground(BACKGROUND_COLOR);
        setFocusable(true);
        
        setupEventHandlers();
        
        // Listen for model changes
        levelModel.addChangeListener(e -> repaint());
    }
    
    private void setupEventHandlers() {
        MouseAdapter mouseHandler = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                handleMousePressed(e);
            }
            
            @Override
            public void mouseReleased(MouseEvent e) {
                handleMouseReleased(e);
            }
            
            @Override
            public void mouseDragged(MouseEvent e) {
                handleMouseDragged(e);
            }
            
            @Override
            public void mouseMoved(MouseEvent e) {
                handleMouseMoved(e);
            }
            
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                handleMouseWheel(e);
            }
        };
        
        addMouseListener(mouseHandler);
        addMouseMotionListener(mouseHandler);
        addMouseWheelListener(mouseHandler);
        
        // Keyboard shortcuts
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                handleKeyPressed(e);
            }
        });
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Apply view transformation
        AffineTransform originalTransform = g2d.getTransform();
        
        // Center the view and apply zoom and pan
        g2d.translate(getWidth() / 2.0, getHeight() / 2.0);
        g2d.scale(zoom, zoom);
        g2d.translate(panX, panY);
        
        // Draw grid
        if (showGrid) {
            drawGrid(g2d);
        }
        
        // Draw entities
        drawEntities(g2d);
        
        // Draw selection rectangles
        drawSelectionRectangles(g2d);
        
        g2d.setTransform(originalTransform);
        
        // Draw UI overlays (zoom level, etc.)
        drawOverlays(g2d);
        
        g2d.dispose();
    }
    
    private void drawGrid(Graphics2D g2d) {
        g2d.setColor(GRID_COLOR);
        g2d.setStroke(new BasicStroke(1.0f / zoom));
        
        // Calculate visible bounds
        Rectangle2D visibleBounds = getVisibleWorldBounds();
        
        // Calculate grid lines
        float startX = (float) Math.floor(visibleBounds.getMinX() / GRID_SIZE) * GRID_SIZE;
        float endX = (float) Math.ceil(visibleBounds.getMaxX() / GRID_SIZE) * GRID_SIZE;
        float startY = (float) Math.floor(visibleBounds.getMinY() / GRID_SIZE) * GRID_SIZE;
        float endY = (float) Math.ceil(visibleBounds.getMaxY() / GRID_SIZE) * GRID_SIZE;
        
        // Draw vertical lines
        for (float x = startX; x <= endX; x += GRID_SIZE) {
            g2d.drawLine((int) x, (int) visibleBounds.getMinY(), 
                        (int) x, (int) visibleBounds.getMaxY());
        }
        
        // Draw horizontal lines
        for (float y = startY; y <= endY; y += GRID_SIZE) {
            g2d.drawLine((int) visibleBounds.getMinX(), (int) y,
                        (int) visibleBounds.getMaxX(), (int) y);
        }
        
        // Draw axes
        g2d.setStroke(new BasicStroke(2.0f / zoom));
        g2d.setColor(Color.GRAY);
        g2d.drawLine((int) visibleBounds.getMinX(), 0, (int) visibleBounds.getMaxX(), 0);
        g2d.drawLine(0, (int) visibleBounds.getMinY(), 0, (int) visibleBounds.getMaxY());
    }
    
    private void drawEntities(Graphics2D g2d) {
        List<EditorEntity> entities = levelModel.getEntities();
        
        for (EditorEntity entity : entities) {
            if (!entity.isVisible()) continue;
            
            drawEntity(g2d, entity);
        }
    }
    
    private void drawEntity(Graphics2D g2d, EditorEntity entity) {
        float x = entity.getX2D(viewMode);
        float y = entity.getY2D(viewMode);
        float width = entity.getWidth2D(viewMode);
        float height = entity.getHeight2D(viewMode);
        
        // Entity rectangle
        Rectangle2D.Float rect = new Rectangle2D.Float(
            x - width/2, y - height/2, width, height
        );
        
        // Fill if hovered
        if (entity == hoveredEntity) {
            g2d.setColor(HOVER_COLOR);
            g2d.fill(rect);
        }
        
        // Outline
        g2d.setStroke(new BasicStroke(entity.isSelected() ? 2.0f / zoom : 1.0f / zoom));
        g2d.setColor(entity.isSelected() ? SELECTED_COLOR : ENTITY_COLOR);
        g2d.draw(rect);
        
        // Draw entity name
        if (zoom > 0.5f) {
            g2d.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, (int) (12 / zoom)));
            FontMetrics fm = g2d.getFontMetrics();
            String name = entity.getName();
            float textWidth = fm.stringWidth(name);
            float textHeight = fm.getHeight();
            
            g2d.setColor(Color.BLACK);
            g2d.drawString(name, x - textWidth/2, y + height/2 + textHeight);
        }
    }
    
    private void drawSelectionRectangles(Graphics2D g2d) {
        // TODO: Implement selection rectangle drawing for multi-select
    }
    
    private void drawOverlays(Graphics2D g2d) {
        // Draw view mode indicator
        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
        g2d.drawString(viewMode.getDisplayName(), 10, 25);
        
        // Draw zoom level
        g2d.drawString(String.format("%.0f%%", zoom * 100), 10, 45);
        
        // Draw entity count
        g2d.drawString("Entities: " + levelModel.getEntities().size(), 10, 65);
    }
    
    private Rectangle2D getVisibleWorldBounds() {
        double worldWidth = getWidth() / zoom;
        double worldHeight = getHeight() / zoom;
        
        // The center of the visible area in world coordinates
        // This accounts for the pan offset
        double centerX = -panX;
        double centerY = -panY;
        
        return new Rectangle2D.Double(
            centerX - worldWidth/2,
            centerY - worldHeight/2,
            worldWidth,
            worldHeight
        );
    }
    
    private Point2D screenToWorld(Point2D screenPoint) {
        // Reverse the transformation: screen -> center -> unzoom -> unpan
        double worldX = (screenPoint.getX() - getWidth()/2.0) / zoom - panX;
        double worldY = (screenPoint.getY() - getHeight()/2.0) / zoom - panY;
        return new Point2D.Double(worldX, worldY);
    }
    
    private Point2D worldToScreen(Point2D worldPoint) {
        // Apply the transformation: world -> pan -> zoom -> center
        double screenX = (worldPoint.getX() + panX) * zoom + getWidth()/2.0;
        double screenY = (worldPoint.getY() + panY) * zoom + getHeight()/2.0;
        return new Point2D.Double(screenX, screenY);
    }
    
    // Mouse event handlers
    private void handleMousePressed(MouseEvent e) {
        requestFocusInWindow();
        lastMousePos = e.getPoint();
        
        Point2D worldPos = screenToWorld(e.getPoint());
        EditorEntity entityAtPos = levelModel.getEntityAt(viewMode, 
            (float) worldPos.getX(), (float) worldPos.getY());
        
        if (SwingUtilities.isMiddleMouseButton(e) || 
            (SwingUtilities.isLeftMouseButton(e) && e.isControlDown())) {
            // Start panning
            isPanning = true;
            setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
        } else if (SwingUtilities.isLeftMouseButton(e)) {
            if (entityAtPos != null) {
                // Start dragging entity
                if (!entityAtPos.isSelected()) {
                    if (!e.isShiftDown()) {
                        levelModel.clearSelection();
                    }
                    levelModel.selectEntity(entityAtPos, e.isShiftDown());
                }
                isDragging = true;
                dragStartWorld = worldPos;
                dragStartMouse = e.getPoint();
                setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
            } else {
                // Clear selection if clicking empty space
                if (!e.isShiftDown()) {
                    levelModel.clearSelection();
                }
            }
        }
    }
    
    private void handleMouseReleased(MouseEvent e) {
        isPanning = false;
        isDragging = false;
        dragStartWorld = null;
        dragStartMouse = null;
        setCursor(Cursor.getDefaultCursor());
    }
    
    private void handleMouseDragged(MouseEvent e) {
        if (lastMousePos == null) return;
        
        double deltaX = e.getX() - lastMousePos.getX();
        double deltaY = e.getY() - lastMousePos.getY();
        
        if (isPanning) {
            // Pan the view
            panX += deltaX / zoom;
            panY += deltaY / zoom;
            repaint();
        } else if (isDragging && dragStartWorld != null) {
            // Move selected entities
            Point2D currentWorld = screenToWorld(e.getPoint());
            double worldDeltaX = currentWorld.getX() - dragStartWorld.getX();
            double worldDeltaY = currentWorld.getY() - dragStartWorld.getY();
            
            for (EditorEntity entity : levelModel.getSelectedEntities()) {
                float currentX = entity.getX2D(viewMode);
                float currentY = entity.getY2D(viewMode);
                entity.setPosition2D(viewMode, 
                    (float) (currentX + worldDeltaX), 
                    (float) (currentY + worldDeltaY));
            }
            
            dragStartWorld = currentWorld;
            repaint();
        }
        
        lastMousePos = e.getPoint();
    }
    
    private void handleMouseMoved(MouseEvent e) {
        Point2D worldPos = screenToWorld(e.getPoint());
        EditorEntity newHovered = levelModel.getEntityAt(viewMode, 
            (float) worldPos.getX(), (float) worldPos.getY());
        
        if (newHovered != hoveredEntity) {
            hoveredEntity = newHovered;
            repaint();
        }
        
        // Update status bar with coordinates
        if (getParent() instanceof Container) {
            // Find status panel and update coordinates
            // This is a bit hacky but works for our simple case
        }
    }
    
    private void handleMouseWheel(MouseWheelEvent e) {
        float oldZoom = zoom;
        float zoomFactor = 1.1f;
        
        if (e.getWheelRotation() < 0) {
            zoom *= zoomFactor;
        } else {
            zoom /= zoomFactor;
        }
        
        zoom = Math.max(MIN_ZOOM, Math.min(MAX_ZOOM, zoom));
        
        // Zoom towards mouse position
        if (zoom != oldZoom) {
            Point2D mouseWorld = screenToWorld(e.getPoint());
            // Adjust pan to keep mouse position constant
            panX = (float) (mouseWorld.getX() - (e.getX() - getWidth()/2.0) / zoom);
            panY = (float) (mouseWorld.getY() - (e.getY() - getHeight()/2.0) / zoom);
            repaint();
        }
    }
    
    private void handleKeyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_DELETE:
                levelModel.deleteSelected();
                break;
            case KeyEvent.VK_1:
                setViewMode(ViewMode.TOP);
                break;
            case KeyEvent.VK_2:
                setViewMode(ViewMode.SIDE);
                break;
            case KeyEvent.VK_3:
                setViewMode(ViewMode.FRONT);
                break;
            case KeyEvent.VK_G:
                toggleGrid();
                break;
        }
    }
    
    // Public interface
    public void setViewMode(ViewMode mode) {
        this.viewMode = mode;
        repaint();
    }
    
    public ViewMode getViewMode() {
        return viewMode;
    }
    
    public void zoomIn() {
        zoom = Math.min(MAX_ZOOM, zoom * 1.2f);
        repaint();
    }
    
    public void zoomOut() {
        zoom = Math.max(MIN_ZOOM, zoom / 1.2f);
        repaint();
    }
    
    public void resetZoom() {
        zoom = 1.0f;
        panX = 0.0f;
        panY = 0.0f;
        repaint();
    }
    
    public void toggleGrid() {
        showGrid = !showGrid;
        repaint();
    }
    
    public float getZoom() {
        return zoom;
    }
}
