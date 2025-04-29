#version 400 core

layout (location=0) in vec3 position;
layout (location=1) in vec2 textureCoord;
layout (location=2) in vec3 normal;

out vec3 color;
out vec2 fragTextureCoord;
out vec3 fragNormal;
out vec3 fragPosition;
out vec3 toLightVector;
out vec3 toCameraVector;

uniform mat4 transformationMatrix;
uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;

uniform vec3 lightPosition;
uniform vec3 lightColor;
uniform vec3 ambientLight;
uniform vec3 cameraPosition;

void main() {
    vec4 worldPosition = transformationMatrix * vec4(position, 1.0);
    gl_Position = projectionMatrix * viewMatrix * worldPosition;

    // Pass the fragment position in world space
    fragPosition = worldPosition.xyz;
    
    // Pass the texture coordinates
    fragTextureCoord = textureCoord;
    
    // Transform normals to world space
    mat3 normalMatrix = mat3(transpose(inverse(transformationMatrix)));
    fragNormal = normalize(normalMatrix * normal);
    
    // Calculate the vector from fragment to light
    toLightVector = lightPosition - worldPosition.xyz;
    
    // Calculate vector from fragment to camera
    toCameraVector = cameraPosition - worldPosition.xyz;
    
    // Calculate basic lighting for backward compatibility
    // This is now fully calculated in the fragment shader
    vec3 lightDir = normalize(toLightVector);
    
    // Ambient light (1st pass)
    vec3 ambient = ambientLight;
    
    // Diffuse light (2nd pass)
    float diff = max(dot(fragNormal, lightDir), 0.2);
    vec3 diffuse = diff * lightColor;
    
    // Specular highlights (3rd pass)
    vec3 viewDir = normalize(toCameraVector);
    vec3 reflectDir = reflect(-lightDir, fragNormal);
    float spec = pow(max(dot(viewDir, reflectDir), 0.0), 32.0); // reflectivity
    vec3 specular = spec * lightColor;
    
    // All passes combined (basic lighting model for compatibility)
    color = ambient + diffuse + specular;
}