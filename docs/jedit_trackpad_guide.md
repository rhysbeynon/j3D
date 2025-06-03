# jEDIT Trackpad Controls Guide

jEDIT now includes enhanced trackpad support for macOS users, making the level editor much more intuitive to use with trackpads.

## New Trackpad Controls

### Panning (Moving the View)
- **Two-finger scroll vertically**: Pan up and down in the viewport
- **Shift + Two-finger scroll**: Pan horizontally (left and right)
- **Two-finger tap + drag**: Alternative panning method
- **Middle mouse or Ctrl+Left mouse**: Traditional panning (still works)

### Zooming
- **Ctrl + Two-finger scroll**: Zoom in and out (simulates pinch gesture)
- **Ctrl + Plus/Minus**: Keyboard zoom shortcuts (from menu)
- **Mouse wheel**: Zoom in and out (on non-trackpad mice)

### Entity Selection and Movement
- **Single click**: Select an entity
- **Shift + click**: Multi-select entities
- **Drag selected entity**: Move entities around the viewport
- **Click empty space**: Clear selection

### Keyboard Shortcuts (Enhanced for trackpad workflow)
- **1, 2, 3**: Switch between Top, Side, and Front views
- **G**: Toggle grid visibility
- **Delete**: Delete selected entities
- **Ctrl+Z/Y**: Undo/Redo (when implemented)

## Technical Improvements

### Trackpad Detection
jEDIT automatically detects when you're using a trackpad on macOS and adjusts:
- **Zoom sensitivity**: Smoother zoom increments (1.05x vs 1.1x for mouse wheel)
- **Pan sensitivity**: Adjusts based on current zoom level for consistent feel
- **Scroll direction**: Natural scrolling direction support

### Enhanced Responsiveness
- **Variable sensitivity**: Pan sensitivity scales with zoom level
- **Smooth scrolling**: Better handling of trackpad scroll events
- **Natural gestures**: Two-finger scrolling feels natural and responsive

## Tips for Best Experience

1. **Use two fingers for navigation**: This is the most natural way to move around
2. **Hold Shift for horizontal panning**: When you need to move left/right specifically
3. **Zoom before detailed work**: Get close to your entities before fine-tuning positions
4. **Use keyboard shortcuts**: Combined with trackpad, they provide the fastest workflow

## Fallback Support
All traditional mouse controls still work:
- Middle mouse button for panning
- Ctrl+Left mouse for panning
- Scroll wheel for zooming
- Right-click context menus (when implemented)

## Compatibility
- **macOS**: Full trackpad gesture support
- **Windows/Linux**: Enhanced scroll wheel handling, traditional mouse controls
- **All platforms**: Keyboard shortcuts and menu controls available

The trackpad improvements make jEDIT significantly more pleasant to use on macOS laptops and external trackpads, providing a more modern and intuitive editing experience.
