#!/bin/zsh
# Script to update jEDIT launcher icon

# Use the j3D logo as the icon for our launcher
ICON_FILE="/Users/rhysbeynon/j3D/src/main/resources/j3D_DARK.png"
APP_PATH="$HOME/Applications/jEDIT.app"

if [ -f "$ICON_FILE" ] && [ -d "$APP_PATH" ]; then
    echo "Setting icon for jEDIT launcher..."
    
    # Convert PNG to ICNS (requires libicns)
    mkdir -p /tmp/jedit_iconset/icon.iconset
    sips -z 16 16 "$ICON_FILE" --out /tmp/jedit_iconset/icon.iconset/icon_16x16.png
    sips -z 32 32 "$ICON_FILE" --out /tmp/jedit_iconset/icon.iconset/icon_16x16@2x.png
    sips -z 32 32 "$ICON_FILE" --out /tmp/jedit_iconset/icon.iconset/icon_32x32.png
    sips -z 64 64 "$ICON_FILE" --out /tmp/jedit_iconset/icon.iconset/icon_32x32@2x.png
    sips -z 128 128 "$ICON_FILE" --out /tmp/jedit_iconset/icon.iconset/icon_128x128.png
    sips -z 256 256 "$ICON_FILE" --out /tmp/jedit_iconset/icon.iconset/icon_128x128@2x.png
    sips -z 256 256 "$ICON_FILE" --out /tmp/jedit_iconset/icon.iconset/icon_256x256.png
    sips -z 512 512 "$ICON_FILE" --out /tmp/jedit_iconset/icon.iconset/icon_256x256@2x.png
    sips -z 512 512 "$ICON_FILE" --out /tmp/jedit_iconset/icon.iconset/icon_512x512.png
    sips -z 1024 1024 "$ICON_FILE" --out /tmp/jedit_iconset/icon.iconset/icon_512x512@2x.png
    
    iconutil -c icns /tmp/jedit_iconset/icon.iconset -o /tmp/jedit_iconset/jedit.icns
    
    # Set the icon
    cp /tmp/jedit_iconset/jedit.icns "$APP_PATH/Contents/Resources/applet.icns"
    
    # Clean up
    rm -rf /tmp/jedit_iconset
    
    # Touch the app to update Finder
    touch "$APP_PATH"
    
    echo "Icon updated successfully."
else
    echo "Error: Could not find icon file or application."
fi
