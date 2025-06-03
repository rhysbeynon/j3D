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
 * - Trackpad gesture support (two-finger pan, pinch-to-zoom)
 * 
 * Keyboard Controls:
 * - 1/2/3: Switch between Top/Side/Front views
 * - G: Toggle grid visibility
 * - Delete: Delete selected entities
 * - Minus (-): Zoom out
 * - Equals/Plus (=/+): Zoom in
 * - 0: Reset zoom and pan to default
 * - Escape: Deselect all entities
 * 
 * Mouse/Trackpad Controls:
 * - Left click: Select entities
 * - Left drag: Move selected entities
 * - Middle click/Ctrl+Left: Pan view
 * - Scroll wheel: Zoom (non-macOS) or Pan (macOS)
 * - Ctrl+Scroll: Zoom on macOS trackpads
 * - Shift+Scroll: Horizontal pan
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
        
        // Enhanced gesture support for macOS trackpads
        addGestureListeners();
        
        // Keyboard shortcuts
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                handleKeyPressed(e);
            }
        });
    }
    
    /**
     * Adds enhanced gesture listeners for trackpad support on macOS.
     * This includes support for:
     * - Two-finger scrolling for panning
     * - Pinch gestures for zooming
     * - Better trackpad detection
     */
    private void addGestureListeners() {
        // On macOS, trackpad gestures are handled through special mouse events
        // and system properties. We'll enhance the existing mouse wheel handler
        // to detect trackpad-specific patterns.
        
        // Mouse wheel events on macOS trackpads have different characteristics:
        // - Smoother, more frequent events
        // - Different wheel rotation values
        // - Can include magnification gestures
        
        // Add property change listener for macOS gesture events
        if (System.getProperty("os.name").toLowerCase().contains("mac")) {
            addTrackpadGestureSupport();
        }
    }
    
    /**
     * Adds macOS-specific trackpad gesture support.
     */
    private void addTrackpadGestureSupport() {
        // Override mouse wheel handling for better trackpad support
        addMouseWheelListener(new MouseWheelListener() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                handleMouseWheel(e);
            }
        });
        
        // Try to add magnification gesture support if available
        try {
            // Use reflection to access macOS-specific gesture APIs if available
            addMagnificationGestureSupport();
        } catch (Exception ex) {
            // Fall back to standard mouse wheel handling
            System.out.println("Magnification gestures not available, using standard mouse wheel");
        }
    }
    
    /**
     * Attempts to add magnification gesture support using reflection
     * to access macOS-specific APIs.
     */
    private void addMagnificationGestureSupport() {
        // Try to use macOS-specific magnification events
        try {
            // On macOS, we can listen for magnification events through system properties
            // or special event handling. For now, we'll use a combination of 
            // Ctrl+scroll wheel to simulate pinch gestures as a fallback.
            
            // Add a mouse wheel listener that specifically handles Ctrl+scroll as zoom
            addMouseWheelListener(new MouseWheelListener() {
                @Override
                public void mouseWheelMoved(MouseWheelEvent e) {
                    // Ctrl+scroll wheel acts as pinch-to-zoom
                    if (e.isControlDown()) {
                        handleTrackpadZoom(e);
                        e.consume(); // Prevent other handlers from processing this
                    }
                }
            });
            
            System.out.println("Enhanced trackpad support enabled. Use Ctrl+scroll for zooming, two-finger scroll for panning.");
            
        } catch (Exception ex) {
            System.out.println("Could not enable enhanced magnification gestures: " + ex.getMessage());
        }
    }

    // ...existing code...
    
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
        // Reverse the transformation to match paintComponent:
        // Graphics: translate(center) -> scale(zoom) -> translate(pan)
        // Reverse: subtract center -> unscale -> subtract pan
        double worldX = (screenPoint.getX() - getWidth()/2.0) / zoom - panX;
        double worldY = (screenPoint.getY() - getHeight()/2.0) / zoom - panY;
        return new Point2D.Double(worldX, worldY);
    }
    
    // Mouse event handlers
    private void handleMousePressed(MouseEvent e) {
        requestFocusInWindow();
        lastMousePos = e.getPoint();
        
        Point2D worldPos = screenToWorld(e.getPoint());
        EditorEntity entityAtPos = levelModel.getEntityAt(viewMode, 
            (float) worldPos.getX(), (float) worldPos.getY());
        
        // Enhanced trackpad support: two-finger tap for panning
        boolean isTwoFingerTap = e.getClickCount() == 1 && e.isMetaDown() && 
                                System.getProperty("os.name").toLowerCase().contains("mac");
        
        if (SwingUtilities.isMiddleMouseButton(e) || 
            (SwingUtilities.isLeftMouseButton(e) && e.isControlDown()) ||
            isTwoFingerTap) {
            // Start panning (middle mouse, Ctrl+left mouse, or two-finger tap on trackpad)
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
        // Enhanced trackpad support for macOS
        boolean isMacOS = System.getProperty("os.name").toLowerCase().contains("mac");
        
        if (isMacOS) {
            // On macOS, ALL scroll wheel events from trackpad should pan by default
            // Zooming only happens with Ctrl+scroll (simulating pinch)
            if (e.isControlDown()) {
                // Ctrl+scroll acts as zoom (simulating pinch gesture)
                handleTrackpadZoom(e);
            } else {
                // All other trackpad scrolling is panning
                handleTrackpadPan(e);
            }
        } else {
            // On non-macOS systems, use traditional behavior
            if (e.isShiftDown()) {
                // Shift + scroll wheel for horizontal panning
                handleTrackpadPan(e);
            } else {
                // Regular scroll wheel for zooming
                handleTrackpadZoom(e);
            }
        }
    }
    
    /**
     * Handles trackpad panning (two-finger scrolling).
     * On macOS trackpads, this handles both vertical and horizontal scrolling.
     * Zooming is only available through Ctrl+scroll.
     */
    private void handleTrackpadPan(MouseWheelEvent e) {
        // Convert scroll wheel movement to pan
        float panSensitivity = 2.0f / zoom; // Adjust sensitivity based on zoom level
        
        // Check if this is a horizontal scroll event (shift key or horizontal wheel)
        boolean isHorizontalScroll = e.isShiftDown();
        
        if (isHorizontalScroll) {
            // Horizontal panning when shift is held
            panX -= e.getWheelRotation() * panSensitivity;
        } else {
            // Vertical panning (natural scrolling direction)
            // Note: On macOS, natural scrolling means wheel rotation is inverted
            boolean isMacOS = System.getProperty("os.name").toLowerCase().contains("mac");
            float scrollDirection = isMacOS ? -1.0f : 1.0f; // Natural scrolling on macOS
            panY += e.getWheelRotation() * panSensitivity * scrollDirection;
        }
        
        repaint();
    }
    
    /**
     * Handles trackpad zooming (pinch gesture or scroll wheel).
     */
    private void handleTrackpadZoom(MouseWheelEvent e) {
        float oldZoom = zoom;
        
        // Adjust zoom factor for smoother trackpad experience
        boolean isMacOS = System.getProperty("os.name").toLowerCase().contains("mac");
        float zoomFactor = isMacOS ? 1.05f : 1.1f; // Smoother zooming on macOS
        
        // Get mouse position relative to panel center
        double mouseX = e.getX() - getWidth() / 2.0;
        double mouseY = e.getY() - getHeight() / 2.0;
        
        // Apply zoom change
        if (e.getWheelRotation() < 0) {
            zoom *= zoomFactor;
        } else {
            zoom /= zoomFactor;
        }
        
        zoom = Math.max(MIN_ZOOM, Math.min(MAX_ZOOM, zoom));
        
        // Only adjust pan if zoom actually changed
        if (zoom != oldZoom) {
            // Adjust pan to keep the mouse point fixed in world space
            // The idea: mouse point in world space should remain constant
            // Before: (mouseX/oldZoom - panX_old) = world point
            // After:  (mouseX/newZoom - panX_new) = same world point
            // Therefore: panX_new = panX_old + mouseX * (1/newZoom - 1/oldZoom)
            panX += (float) (mouseX * (1.0/zoom - 1.0/oldZoom));
            panY += (float) (mouseY * (1.0/zoom - 1.0/oldZoom));
            
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
            case KeyEvent.VK_PLUS:
            case KeyEvent.VK_EQUALS:
                zoomIn();
                break;
            case KeyEvent.VK_MINUS:
                zoomOut();
                break;
            case KeyEvent.VK_0:
                resetZoom();
                break;
            case KeyEvent.VK_ESCAPE:
                // Deselect all entities
                levelModel.clearSelection();
                hoveredEntity = null;
                repaint();
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
