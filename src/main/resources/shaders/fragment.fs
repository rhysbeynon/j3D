#version 400 core

in vec2 fragTextureCoord;
in vec3 color;
in vec3 fragNormal;
in vec3 fragPosition;
in vec3 toLightVector;
in vec3 toCameraVector;

out vec4 fragColor;

uniform sampler2D textureSampler;
uniform vec3 lightPosition;
uniform vec3 lightColor;
uniform vec3 ambientLight;
uniform vec3 cameraPosition;
uniform int useTransparency;

void main() {
    // Sample the texture
    vec4 textureColor = texture(textureSampler, fragTextureCoord);
    
    // Discard fully transparent fragments if transparency is enabled
    if (useTransparency == 1 && textureColor.a < 0.01) {
        discard;
    }
    
    // Calculate lighting
    vec3 unitNormal = normalize(fragNormal);
    vec3 unitLightVector = normalize(toLightVector);
    
    // Diffuse lighting
    float diffuseFactor = max(dot(unitNormal, unitLightVector), 0.0);
    vec3 diffuse = diffuseFactor * lightColor;
    
    // Specular lighting
    vec3 unitToCameraVector = normalize(toCameraVector);
    vec3 lightDirection = -unitLightVector;
    vec3 reflectedLightDirection = reflect(lightDirection, unitNormal);
    float specularFactor = max(dot(reflectedLightDirection, unitToCameraVector), 0.0);
    float dampedFactor = pow(specularFactor, 10.0); // Shininess factor
    vec3 specular = dampedFactor * 0.5 * lightColor; // Specular strength = 0.5
    
    // Combine lighting and texture color
    vec3 lighting = ambientLight + diffuse + specular;
    
    // Final color with proper alpha for transparency
    if (useTransparency == 1) {
        fragColor = vec4(lighting, 1.0) * textureColor;
    } else {
        // For opaque objects, ignore alpha from texture
        fragColor = vec4(lighting * textureColor.rgb, 1.0);
    }
}