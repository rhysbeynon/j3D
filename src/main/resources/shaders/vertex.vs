#version 400 core

layout (location=0) in vec3 position;
layout (location=1) in vec2 textureCoord;
layout (location=2) in vec3 normal;

out vec3 color;
out vec2 fragTextureCoord;

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

    //translate normals into world space
    mat3 normalMatrix = mat3(transpose(inverse(transformationMatrix)));
    vec3 worldNormal = normalize(normalMatrix * normal);

    vec3 lightDir = normalize(lightPosition - worldPosition.xyz);

    //ambient light (1st pass)
    vec3 ambient = ambientLight;
    //diffuse light (2nd pass)
    float diff = max(dot(worldNormal, lightDir), 0.0);
    vec3 diffuse = diff * lightColor;
    //specular highlights (3rd pass)
    vec3 viewDir = normalize(cameraPosition - worldPosition.xyz);
    vec3 reflectDir = reflect(-lightDir, worldNormal);
    float spec = pow(max(dot(viewDir, reflectDir), 0.0), 16.0); //reflectivity
    vec3 specular = spec * lightColor;

    //all passes combined
    color = ambient + diffuse + specular;

    //pass texture coords to fragment shader
    fragTextureCoord = textureCoord;
}