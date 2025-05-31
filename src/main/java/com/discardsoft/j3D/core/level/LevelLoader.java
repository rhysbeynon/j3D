package com.discardsoft.j3D.core.level;

import com.discardsoft.j3D.core.ObjectLoader;
import com.discardsoft.j3D.core.entity.Entity;
import com.discardsoft.j3D.core.entity.Light;
import com.discardsoft.j3D.core.entity.Model;
import com.discardsoft.j3D.core.entity.terrain.Terrain;
import com.discardsoft.j3D.core.entity.terrain.TerrainEntity;
import com.discardsoft.j3D.core.scene.BaseScene;
import com.discardsoft.j3D.core.utils.LoadModel;
import com.discardsoft.j3D.core.utils.Utils;
import org.joml.Vector3f;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles loading and saving of j3D level files.
 * <p>
 * This class provides functionality to convert between the engine's scene format
 * and the JSON-based level file format, enabling persistent level storage and
 * external level editing tools.
 * </p>
 *
 * @author DiscardSoft
 * @version 0.1
 */
public class LevelLoader {
    
    private final Gson gson;
    private final ObjectLoader objectLoader;
    
    /**
     * Creates a new level loader with the specified object loader.
     *
     * @param objectLoader The object loader to use for creating models and textures
     */
    public LevelLoader(ObjectLoader objectLoader) {
        this.gson = new GsonBuilder()
            .setPrettyPrinting()
            .create();
        this.objectLoader = objectLoader;
    }
    
    /**
     * Loads a level from a JSON file and creates a scene.
     *
     * @param levelPath The path to the level file
     * @return A loaded scene containing all level data
     * @throws Exception If the level file cannot be loaded or parsed
     */
    public LoadedLevel loadLevel(String levelPath) throws Exception {
        String jsonContent = Utils.loadResource(levelPath);
        JsonObject levelData = gson.fromJson(jsonContent, JsonObject.class);
        
        LoadedLevel level = new LoadedLevel();
        
        // Load metadata
        if (levelData.has("metadata")) {
            JsonObject metadata = levelData.getAsJsonObject("metadata");
            level.metadata = new LevelMetadata();
            level.metadata.name = getStringOrDefault(metadata, "name", "Untitled Level");
            level.metadata.description = getStringOrDefault(metadata, "description", "");
            level.metadata.version = getStringOrDefault(metadata, "version", "1.0");
            level.metadata.author = getStringOrDefault(metadata, "author", "Unknown");
            level.metadata.created = getStringOrDefault(metadata, "created", "");
            level.metadata.modified = getStringOrDefault(metadata, "modified", "");
        }
        
        // Load lighting
        Light light = loadLight(levelData);
        level.scene = new LoadedScene(light);
        
        // Load terrain
        if (levelData.has("terrain")) {
            JsonObject terrainData = levelData.getAsJsonObject("terrain");
            if (terrainData.get("enabled").getAsBoolean()) {
                TerrainEntity terrain = loadTerrain(terrainData);
                level.scene.addEntity(terrain);
                level.terrain = terrain;
            }
        }
        
        // Load entities
        if (levelData.has("entities")) {
            JsonArray entitiesArray = levelData.getAsJsonArray("entities");
            for (JsonElement entityElement : entitiesArray) {
                JsonObject entityData = entityElement.getAsJsonObject();
                Entity entity = loadEntity(entityData);
                level.scene.addEntity(entity);
                level.entities.add(new LoadedEntity(entity, entityData));
            }
        }
        
        // Load spawn points
        if (levelData.has("spawns")) {
            JsonArray spawnsArray = levelData.getAsJsonArray("spawns");
            for (JsonElement spawnElement : spawnsArray) {
                JsonObject spawnData = spawnElement.getAsJsonObject();
                SpawnPoint spawn = loadSpawnPoint(spawnData);
                level.spawns.add(spawn);
            }
        }
        
        return level;
    }
    
    /**
     * Saves a level to a JSON file.
     *
     * @param level The level data to save
     * @param filePath The path where to save the level file
     * @throws IOException If the file cannot be written
     */
    public void saveLevel(LoadedLevel level, String filePath) throws IOException {
        JsonObject levelData = new JsonObject();
        
        // Save metadata
        if (level.metadata != null) {
            JsonObject metadata = new JsonObject();
            metadata.addProperty("name", level.metadata.name);
            metadata.addProperty("description", level.metadata.description);
            metadata.addProperty("version", level.metadata.version);
            metadata.addProperty("author", level.metadata.author);
            metadata.addProperty("created", level.metadata.created);
            metadata.addProperty("modified", level.metadata.modified);
            levelData.add("metadata", metadata);
        }
        
        // Save lighting
        if (level.scene != null) {
            Light light = level.scene.getLight();
            JsonObject lighting = new JsonObject();
            lighting.add("position", positionVectorToJson(light.getPosition()));
            lighting.add("color", colorVectorToJson(light.getColor()));
            lighting.add("ambient", colorVectorToJson(light.getAmbient()));
            levelData.add("lighting", lighting);
        }
        
        // Save terrain
        if (level.terrain != null) {
            JsonObject terrain = saveTerrainToJson(level.terrain);
            levelData.add("terrain", terrain);
        } else {
            JsonObject terrain = new JsonObject();
            terrain.addProperty("enabled", false);
            levelData.add("terrain", terrain);
        }
        
        // Save entities
        JsonArray entitiesArray = new JsonArray();
        for (LoadedEntity loadedEntity : level.entities) {
            JsonObject entityData = saveEntityToJson(loadedEntity);
            entitiesArray.add(entityData);
        }
        levelData.add("entities", entitiesArray);
        
        // Save spawn points
        JsonArray spawnsArray = new JsonArray();
        for (SpawnPoint spawn : level.spawns) {
            JsonObject spawnData = new JsonObject();
            spawnData.addProperty("id", spawn.id);
            spawnData.addProperty("name", spawn.name);
            spawnData.add("position", positionVectorToJson(spawn.position));
            spawnData.add("rotation", positionVectorToJson(spawn.rotation));
            spawnsArray.add(spawnData);
        }
        levelData.add("spawns", spawnsArray);
        
        // Write to file
        try (FileWriter writer = new FileWriter(filePath)) {
            gson.toJson(levelData, writer);
        }
    }
    
    private Light loadLight(JsonObject levelData) {
        if (!levelData.has("lighting")) {
            return new Light(); // Default light
        }
        
        JsonObject lighting = levelData.getAsJsonObject("lighting");
        Vector3f position = jsonObjectToVector(lighting.getAsJsonObject("position"));
        Vector3f color = jsonObjectToColorVector(lighting.getAsJsonObject("color"));
        Vector3f ambient = jsonObjectToColorVector(lighting.getAsJsonObject("ambient"));
        
        return new Light(position, color, ambient);
    }
    
    private TerrainEntity loadTerrain(JsonObject terrainData) throws Exception {
        float size = terrainData.get("size").getAsFloat();
        int gridCount = terrainData.get("grid_count").getAsInt();
        float height = terrainData.get("height").getAsFloat();
        Vector3f position = jsonObjectToVector(terrainData.getAsJsonObject("position"));
        String texture = terrainData.get("texture").getAsString();
        float textureRepeat = terrainData.get("texture_repeat").getAsFloat();
        
        String texturePath = "src/main/resources/textures/" + texture;
        
        Terrain terrain = new Terrain(
            size,
            gridCount,
            height,
            position,
            objectLoader,
            texturePath,
            textureRepeat
        );
        
        return new TerrainEntity(terrain);
    }
    
    private Entity loadEntity(JsonObject entityData) {
        String modelName = entityData.get("model").getAsString();
        Vector3f position = jsonObjectToVector(entityData.getAsJsonObject("position"));
        Vector3f rotation = jsonObjectToVector(entityData.getAsJsonObject("rotation"));
        Vector3f scale = jsonObjectToVector(entityData.getAsJsonObject("scale"));
        
        Model model = LoadModel.model(modelName);
        Entity entity = new Entity(model, position, rotation, scale);
        
        // Load properties
        if (entityData.has("properties")) {
            JsonObject properties = entityData.getAsJsonObject("properties");
            if (properties.has("hasTransparentTexture")) {
                entity.setHasTransparentTexture(properties.get("hasTransparentTexture").getAsBoolean());
            }
            if (properties.has("billboardY")) {
                entity.setBillboardY(properties.get("billboardY").getAsBoolean());
            }
            if (properties.has("billboardFull")) {
                entity.setBillboardFull(properties.get("billboardFull").getAsBoolean());
            }
        }
        
        return entity;
    }
    
    private SpawnPoint loadSpawnPoint(JsonObject spawnData) {
        String id = spawnData.get("id").getAsString();
        String name = spawnData.get("name").getAsString();
        Vector3f position = jsonObjectToVector(spawnData.getAsJsonObject("position"));
        Vector3f rotation = jsonObjectToVector(spawnData.getAsJsonObject("rotation"));
        
        return new SpawnPoint(id, name, position, rotation);
    }
    
    private JsonObject saveTerrainToJson(TerrainEntity terrainEntity) {
        JsonObject terrain = new JsonObject();
        terrain.addProperty("enabled", true);
        
        Terrain terrainData = terrainEntity.getTerrain();
        terrain.addProperty("size", terrainData.getSize());
        terrain.addProperty("gridCount", terrainData.getGridCount());
        terrain.addProperty("height", terrainData.getHeight());
        terrain.add("position", positionVectorToJson(terrainData.getPosition()));
        terrain.addProperty("texture", "ground2"); // Default for now
        terrain.addProperty("textureRepeat", terrainData.getTextureRepeatCount());
        
        return terrain;
    }
    
    private JsonObject saveEntityToJson(LoadedEntity loadedEntity) {
        JsonObject entityData = new JsonObject();
        Entity entity = loadedEntity.entity;
        
        entityData.addProperty("id", loadedEntity.originalData.get("id").getAsString());
        entityData.addProperty("name", loadedEntity.originalData.get("name").getAsString());
        entityData.addProperty("model", "suzanne"); // TODO: Get actual model name
        entityData.add("position", positionVectorToJson(entity.getPosition()));
        entityData.add("rotation", positionVectorToJson(entity.getRotation()));
        entityData.add("scale", positionVectorToJson(entity.getScale()));
        
        JsonObject properties = new JsonObject();
        properties.addProperty("hasTransparentTexture", entity.hasTransparentTexture());
        properties.addProperty("billboardY", entity.isBillboardY());
        properties.addProperty("billboardFull", entity.isBillboardFull());
        entityData.add("properties", properties);
        
        // Copy tags if they exist
        if (loadedEntity.originalData.has("tags")) {
            entityData.add("tags", loadedEntity.originalData.get("tags"));
        }
        
        return entityData;
    }
    
    private Vector3f jsonToVector(JsonArray jsonArray) {
        return new Vector3f(
            jsonArray.get(0).getAsFloat(),
            jsonArray.get(1).getAsFloat(),
            jsonArray.get(2).getAsFloat()
        );
    }
    
    private Vector3f jsonObjectToVector(JsonObject jsonObject) {
        return new Vector3f(
            jsonObject.get("x").getAsFloat(),
            jsonObject.get("y").getAsFloat(),
            jsonObject.get("z").getAsFloat()
        );
    }
    
    private Vector3f jsonObjectToColorVector(JsonObject jsonObject) {
        return new Vector3f(
            jsonObject.get("r").getAsFloat(),
            jsonObject.get("g").getAsFloat(),
            jsonObject.get("b").getAsFloat()
        );
    }
    
    private JsonArray vectorToJson(Vector3f vector) {
        JsonArray array = new JsonArray();
        array.add(vector.x);
        array.add(vector.y);
        array.add(vector.z);
        return array;
    }
    
    private JsonObject colorVectorToJson(Vector3f vector) {
        JsonObject object = new JsonObject();
        object.addProperty("r", vector.x);
        object.addProperty("g", vector.y);
        object.addProperty("b", vector.z);
        return object;
    }
    
    private JsonObject positionVectorToJson(Vector3f vector) {
        JsonObject object = new JsonObject();
        object.addProperty("x", vector.x);
        object.addProperty("y", vector.y);
        object.addProperty("z", vector.z);
        return object;
    }
    
    private String getStringOrDefault(JsonObject object, String key, String defaultValue) {
        return object.has(key) ? object.get(key).getAsString() : defaultValue;
    }
    
    /**
     * A scene implementation that can be loaded from level files.
     */
    public static class LoadedScene extends BaseScene {
        public LoadedScene(Light light) {
            super(light);
        }
        
        @Override
        public void initialize() {
            // Scene is initialized by the loader
        }
        
        @Override
        public void update(float deltaTime) {
            // Default update behavior - can be overridden
        }
    }
    
    /**
     * Container for loaded level data.
     */
    public static class LoadedLevel {
        public LevelMetadata metadata;
        public LoadedScene scene;
        public TerrainEntity terrain;
        public List<LoadedEntity> entities = new ArrayList<>();
        public List<SpawnPoint> spawns = new ArrayList<>();
    }
    
    /**
     * Metadata for a level.
     */
    public static class LevelMetadata {
        public String name;
        public String description;
        public String version;
        public String author;
        public String created;
        public String modified;
    }
    
    /**
     * Container for an entity and its original JSON data.
     */
    public static class LoadedEntity {
        public Entity entity;
        public JsonObject originalData;
        
        public LoadedEntity(Entity entity, JsonObject originalData) {
            this.entity = entity;
            this.originalData = originalData;
        }
    }
    
    /**
     * Represents a spawn point in the level.
     */
    public static class SpawnPoint {
        public String id;
        public String name;
        public Vector3f position;
        public Vector3f rotation;
        
        public SpawnPoint(String id, String name, Vector3f position, Vector3f rotation) {
            this.id = id;
            this.name = name;
            this.position = new Vector3f(position);
            this.rotation = new Vector3f(rotation);
        }
    }
}
