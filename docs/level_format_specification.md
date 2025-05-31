# j3D Level File Format Specification

## Overview

The j3D level file format uses JSON to store scene data in a structured, human-readable format. This allows for easy editing, version control, and tool integration.

## File Structure

```json
{
  "metadata": {
    "name": "Level Name",
    "description": "Level description",
    "version": "1.0",
    "author": "Author Name",
    "created": "2025-05-31T12:00:00Z",
    "modified": "2025-05-31T12:00:00Z"
  },
  "lighting": {
    "position": [10.0, 10.0, 10.0],
    "color": [1.0, 1.0, 1.0],
    "ambient": [0.05, 0.1, 0.1]
  },
  "terrain": {
    "enabled": true,
    "size": 512.0,
    "gridCount": 64,
    "height": 0.0,
    "position": [-256.0, 0.0, -256.0],
    "texture": "ground2",
    "textureRepeat": 64.0
  },
  "entities": [
    {
      "id": "unique_entity_id",
      "name": "Entity Display Name",
      "model": "suzanne",
      "position": [0.0, 1.0, -5.0],
      "rotation": [0.0, 0.0, 0.0],
      "scale": [1.0, 1.0, 1.0],
      "properties": {
        "hasTransparentTexture": false,
        "billboardY": false,
        "billboardFull": false,
        "customTexture": null
      },
      "tags": ["animated", "decoration"]
    }
  ],
  "spawns": [
    {
      "id": "player_spawn",
      "name": "Player Spawn",
      "position": [0.0, 0.0, 0.0],
      "rotation": [0.0, 0.0, 0.0]
    }
  ]
}
```

## Field Descriptions

### Metadata
- `name`: Human-readable level name
- `description`: Level description
- `version`: Level format version
- `author`: Creator of the level
- `created`: ISO 8601 timestamp of creation
- `modified`: ISO 8601 timestamp of last modification

### Lighting
- `position`: [x, y, z] position of the main light source
- `color`: [r, g, b] color values (0.0-1.0)
- `ambient`: [r, g, b] ambient light values (0.0-1.0)

### Terrain
- `enabled`: Whether terrain should be generated
- `size`: Total terrain size in world units
- `gridCount`: Number of grid cells per axis
- `height`: Y-position of terrain
- `position`: [x, y, z] position of terrain
- `texture`: Name of texture file (without extension)
- `textureRepeat`: How many times to repeat texture across terrain

### Entities
- `id`: Unique identifier for the entity
- `name`: Human-readable name for editor display
- `model`: Model name (from LoadModel.model() or file path)
- `position`: [x, y, z] world position
- `rotation`: [x, y, z] rotation in degrees
- `scale`: [x, y, z] scale factors
- `properties`: Entity-specific properties
- `tags`: Array of string tags for organization

### Spawns
- `id`: Unique spawn point identifier
- `name`: Human-readable spawn name
- `position`: [x, y, z] spawn position
- `rotation`: [x, y, z] spawn orientation

## Example Levels

### Simple Test Level
```json
{
  "metadata": {
    "name": "Test Level",
    "description": "Basic test scene with terrain and objects",
    "version": "1.0",
    "author": "j3D Engine",
    "created": "2025-05-31T12:00:00Z",
    "modified": "2025-05-31T12:00:00Z"
  },
  "lighting": {
    "position": [30.0, 50.0, 30.0],
    "color": [1.0, 1.0, 1.0],
    "ambient": [0.3, 0.3, 0.3]
  },
  "terrain": {
    "enabled": true,
    "size": 512.0,
    "gridCount": 64,
    "height": 0.0,
    "position": [-256.0, 0.0, -256.0],
    "texture": "ground2",
    "textureRepeat": 64.0
  },
  "entities": [
    {
      "id": "suzanne_1",
      "name": "Spinning Monkey",
      "model": "suzanne",
      "position": [0.0, 1.0, -5.0],
      "rotation": [0.0, 0.0, 0.0],
      "scale": [1.0, 1.0, 1.0],
      "properties": {
        "hasTransparentTexture": false,
        "billboardY": false,
        "billboardFull": false
      },
      "tags": ["animated", "test"]
    }
  ],
  "spawns": [
    {
      "id": "player_spawn",
      "name": "Player Start",
      "position": [0.0, 0.0, 0.0],
      "rotation": [0.0, 0.0, 0.0]
    }
  ]
}
```

## File Naming Convention

- Use `.j3dl` extension (j3D Level)
- Snake_case naming: `test_level.j3dl`, `forest_area.j3dl`
- Store in `src/main/resources/levels/` directory
