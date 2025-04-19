#version 400 core

in vec2 fragTextureCoord;
in vec3 color;

out vec4 fragColor;

uniform sampler2D textureSampler;

void main() {
    vec4 textureColor = texture(textureSampler, fragTextureCoord);
    fragColor = vec4(color, 1.0) * textureColor;
}